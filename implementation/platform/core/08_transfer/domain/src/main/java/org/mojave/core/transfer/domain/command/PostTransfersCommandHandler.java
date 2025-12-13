/*-
 * ================================================================================
 * Mojave
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */
package org.mojave.core.transfer.domain.command;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.handy.Snowflake;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.common.datatype.enums.Direction;
import org.mojave.core.common.datatype.enums.fspiop.EndpointType;
import org.mojave.core.common.datatype.enums.transfer.AbortReason;
import org.mojave.core.common.datatype.identifier.transaction.TransactionId;
import org.mojave.core.common.datatype.identifier.transfer.TransferId;
import org.mojave.core.common.datatype.identifier.transfer.UdfTransferId;
import org.mojave.core.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.core.common.datatype.type.participant.FspCode;
import org.mojave.core.participant.contract.data.FspData;
import org.mojave.core.participant.store.ParticipantStore;
import org.mojave.core.transaction.contract.command.CloseTransactionCommand;
import org.mojave.core.transaction.producer.publisher.CloseTransactionPublisher;
import org.mojave.core.transfer.contract.command.PostTransfersCommand;
import org.mojave.core.transfer.contract.command.step.financial.ReservePayerPositionStep;
import org.mojave.core.transfer.contract.command.step.financial.RollbackReservationStep;
import org.mojave.core.transfer.contract.command.step.fspiop.ForwardToDestinationStep;
import org.mojave.core.transfer.contract.command.step.fspiop.UnwrapRequestStep;
import org.mojave.core.transfer.contract.command.step.stateful.AbortTransferStep;
import org.mojave.core.transfer.contract.command.step.stateful.ReceiveTransferStep;
import org.mojave.core.transfer.contract.command.step.stateful.ReserveTransferStep;
import org.mojave.core.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import org.mojave.core.wallet.contract.exception.position.PositionLimitExceededException;
import org.mojave.fspiop.component.error.FspiopErrors;
import org.mojave.fspiop.component.exception.FspiopException;
import org.mojave.fspiop.component.type.Payer;
import org.mojave.fspiop.component.handy.FspiopErrorResponder;
import org.mojave.fspiop.component.handy.FspiopUrls;
import org.mojave.fspiop.service.api.transfers.RespondTransfers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class PostTransfersCommandHandler implements PostTransfersCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostTransfersCommandHandler.class);

    private final ParticipantStore participantStore;

    // Stateful steps
    private final ReceiveTransferStep receiveTransferStep;

    private final ReserveTransferStep reserveTransferStep;

    private final AbortTransferStep abortTransferStep;

    // Financial steps
    private final ReservePayerPositionStep reservePayerPositionStep;

    private final RollbackReservationStep rollbackReservationStep;

    // FSPIOP steps
    private final UnwrapRequestStep unwrapRequestStep;

    private final ForwardToDestinationStep forwardToDestinationStep;

    private final RespondTransfers respondTransfers;

    private final CloseTransactionPublisher closeTransactionPublisher;

    public PostTransfersCommandHandler(ParticipantStore participantStore,
                                       ReceiveTransferStep receiveTransferStep,
                                       ReserveTransferStep reserveTransferStep,
                                       AbortTransferStep abortTransferStep,
                                       ReservePayerPositionStep reservePayerPositionStep,
                                       RollbackReservationStep rollbackReservationStep,
                                       UnwrapRequestStep unwrapRequestStep,
                                       ForwardToDestinationStep forwardToDestinationStep,
                                       RespondTransfers respondTransfers,
                                       CloseTransactionPublisher closeTransactionPublisher) {

        assert participantStore != null;
        assert receiveTransferStep != null;
        assert reserveTransferStep != null;
        assert abortTransferStep != null;
        assert reservePayerPositionStep != null;
        assert rollbackReservationStep != null;
        assert unwrapRequestStep != null;
        assert forwardToDestinationStep != null;
        assert respondTransfers != null;
        assert closeTransactionPublisher != null;

        this.participantStore = participantStore;
        this.receiveTransferStep = receiveTransferStep;
        this.reserveTransferStep = reserveTransferStep;
        this.abortTransferStep = abortTransferStep;
        this.reservePayerPositionStep = reservePayerPositionStep;
        this.rollbackReservationStep = rollbackReservationStep;
        this.unwrapRequestStep = unwrapRequestStep;
        this.forwardToDestinationStep = forwardToDestinationStep;
        this.respondTransfers = respondTransfers;
        this.closeTransactionPublisher = closeTransactionPublisher;
    }

    @Override
    @Write
    public Output execute(Input input) {

        MDC.put("REQ_ID", String.valueOf(Snowflake.get().nextId()));

        var startAt = System.nanoTime();

        LOGGER.info("PostTransfersCommandHandler : input : ({})", ObjectLogger.log(input));

        final var CONTEXT = "PostTransfers";

        var udfTransferId = new UdfTransferId(input.transfersPostRequest().getTransferId());

        TransactionId transactionId = null;
        Instant transactionAt = null;
        TransferId transferId;
        PositionUpdateId positionReservationId;

        Exception errorOccurred = null;

        FspCode payerFspCode = null;
        FspData payerFsp = null;
        FspCode payeeFspCode = null;
        FspData payeeFsp = null;

        try {

            payerFspCode = new FspCode(input.request().payer().fspCode());
            payerFsp = this.participantStore.getFspData(payerFspCode);

            payeeFspCode = new FspCode(input.request().payee().fspCode());
            payeeFsp = this.participantStore.getFspData(payeeFspCode);

            var request = input.transfersPostRequest();

            // 1. Unwrap the request.
            var unwrapRequestOutput = this.unwrapRequestStep.execute(
                new UnwrapRequestStep.Input(CONTEXT, udfTransferId, payerFsp, payeeFsp, request));

            var ilpCondition = unwrapRequestOutput.ilpCondition();
            var ilpPacket = unwrapRequestOutput.ilpPacket();
            var agreement = unwrapRequestOutput.agreement();

            // 2. Open a transaction. And receive the Transfer.
            var receiveTransferOutput = this.receiveTransferStep.execute(
                new ReceiveTransferStep.Input(
                    CONTEXT, udfTransferId, payerFsp, payeeFsp, ilpCondition, ilpPacket, agreement,
                    unwrapRequestOutput.requestExpiration(), request.getExtensionList()));

            transactionId = receiveTransferOutput.transactionId();
            transactionAt = receiveTransferOutput.transactionAt();
            transferId = receiveTransferOutput.transferId();

            // 3. Reserve a position of Payer.
            ReservePayerPositionStep.Output reservePayerPositionOutput = null;

            try {

                reservePayerPositionOutput = this.reservePayerPositionStep.execute(
                    new ReservePayerPositionStep.Input(
                        CONTEXT, transactionId, transactionAt, payerFsp, payeeFsp,
                        agreement.transferAmount().getCurrency(),
                        new BigDecimal(agreement.transferAmount().getAmount())));

                positionReservationId = reservePayerPositionOutput.positionReservationId();

            } catch (NoPositionUpdateForTransactionException e) {

                LOGGER.error("Error:", e);

                this.abortTransferStep.execute(
                    new AbortTransferStep.Input(
                        CONTEXT, transactionId, transferId,
                        AbortReason.POSITION_RESERVATION_FAILURE, null, Direction.TO_PAYEE, null));

                throw new FspiopException(FspiopErrors.INTERNAL_SERVER_ERROR, e.getMessage());

            } catch (PositionLimitExceededException e) {

                LOGGER.error("Error:", e);

                this.abortTransferStep.execute(
                    new AbortTransferStep.Input(
                        CONTEXT, transactionId, transferId, AbortReason.POSITION_LIMIT_EXCEEDED,
                        null, Direction.TO_PAYEE, null));

                throw new FspiopException(FspiopErrors.PAYER_LIMIT_ERROR);

            } catch (Exception e) {

                LOGGER.error("Error:", e);
                throw e;
            }

            // 4. Update the Transfer as RESERVED
            try {

                this.reserveTransferStep.execute(
                    new ReserveTransferStep.Input(
                        CONTEXT, transactionId, transferId,
                        positionReservationId));

            } catch (Exception e) {

                LOGGER.error("Error:", e);

                throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, e.getMessage());
            }

            // 5. Forward the request to Payee.
            try {

                var payeeBaseUrl = payeeFsp.endpoints().get(EndpointType.TRANSFERS).baseUrl();

                this.forwardToDestinationStep.execute(
                    new ForwardToDestinationStep.Input(
                        CONTEXT, transactionId, payeeFspCode.value(), payeeBaseUrl,
                        input.request()));

            } catch (Exception e) {

                LOGGER.error("Error:", e);

                this.rollbackReservationStep.execute(
                    new RollbackReservationStep.Input(
                        CONTEXT, transactionId, reservePayerPositionOutput.positionReservationId(),
                        e.getMessage()));

                this.abortTransferStep.execute(new AbortTransferStep.Input(
                    CONTEXT, transactionId, transferId, AbortReason.UNABLE_TO_REACH_PAYEE, null,
                    Direction.TO_PAYEE, null));

                throw e;
            }

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            errorOccurred = e;

            if (payerFsp != null) {

                final var sendBackTo = new Payer(payerFspCode.value());
                final var baseUrl = payerFsp.endpoints().get(EndpointType.TRANSFERS).baseUrl();
                final var url = FspiopUrls.Transfers.putTransfersError(
                    baseUrl, udfTransferId.getId());

                try {

                    FspiopErrorResponder.toPayer(
                        new Payer(payerFspCode.value()), e,
                        (payer, error) -> this.respondTransfers.putTransfersError(
                            sendBackTo, url,
                            error));

                } catch (Exception e1) {
                    LOGGER.error("Error:", e1);
                }

            }

        } finally {

            if (transactionId != null && errorOccurred != null) {

                LOGGER.info(
                    "PostTransfersCommandHandler : error : ({})", errorOccurred.getMessage());

                this.closeTransactionPublisher.publish(
                    new CloseTransactionCommand.Input(transactionId, errorOccurred.getMessage()));

            }
        }

        var endAt = System.nanoTime();
        LOGGER.info(
            "PostTransfersCommandHandler : done : took {} ms", (endAt - startAt) / 1_000_000);

        MDC.remove("REQ_ID");

        return new Output();
    }

}
