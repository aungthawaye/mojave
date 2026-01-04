/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
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
 * ===
 */

package org.mojave.rail.fspiop.transfer.domain.command;

import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.scheme.common.datatype.enums.Direction;
import org.mojave.scheme.common.datatype.enums.fspiop.EndpointType;
import org.mojave.scheme.common.datatype.enums.transfer.AbortReason;
import org.mojave.scheme.common.datatype.identifier.transaction.TransactionId;
import org.mojave.scheme.common.datatype.identifier.transfer.TransferId;
import org.mojave.scheme.common.datatype.identifier.transfer.UdfTransferId;
import org.mojave.scheme.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.scheme.common.datatype.type.participant.FspCode;
import org.mojave.core.participant.contract.data.FspData;
import org.mojave.core.participant.store.ParticipantStore;
import org.mojave.core.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import org.mojave.core.wallet.contract.exception.position.PositionLimitExceededException;
import org.mojave.rail.fspiop.bootstrap.api.transfers.RespondTransfers;
import org.mojave.rail.fspiop.component.error.FspiopErrors;
import org.mojave.rail.fspiop.component.exception.FspiopException;
import org.mojave.rail.fspiop.component.handy.FspiopErrorResponder;
import org.mojave.rail.fspiop.component.handy.FspiopUrls;
import org.mojave.rail.fspiop.component.type.Payer;
import org.mojave.rail.fspiop.transfer.contract.command.PostTransfersCommand;
import org.mojave.rail.fspiop.transfer.contract.command.step.financial.ReservePayerPositionStep;
import org.mojave.rail.fspiop.transfer.contract.command.step.financial.RollbackReservationStep;
import org.mojave.rail.fspiop.transfer.contract.command.step.fspiop.ForwardToDestinationStep;
import org.mojave.rail.fspiop.transfer.contract.command.step.fspiop.UnwrapRequestStep;
import org.mojave.rail.fspiop.transfer.contract.command.step.stateful.AbortTransferStep;
import org.mojave.rail.fspiop.transfer.contract.command.step.stateful.ReceiveTransferStep;
import org.mojave.rail.fspiop.transfer.contract.command.step.stateful.ReserveTransferStep;
import org.mojave.rail.fspiop.transfer.domain.kafka.publisher.AbortTransferStepPublisher;
import org.mojave.rail.fspiop.transfer.domain.kafka.publisher.RollbackReservationStepPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final AbortTransferStepPublisher abortTransferStepPublisher;

    // Financial steps
    private final ReservePayerPositionStep reservePayerPositionStep;

    private final RollbackReservationStepPublisher rollbackReservationStepPublisher;

    // FSPIOP steps
    private final UnwrapRequestStep unwrapRequestStep;

    private final ForwardToDestinationStep forwardToDestinationStep;

    private final RespondTransfers respondTransfers;

    public PostTransfersCommandHandler(ParticipantStore participantStore,
                                       ReceiveTransferStep receiveTransferStep,
                                       ReserveTransferStep reserveTransferStep,
                                       AbortTransferStepPublisher abortTransferStepPublisher,
                                       ReservePayerPositionStep reservePayerPositionStep,
                                       RollbackReservationStepPublisher rollbackReservationStepPublisher,
                                       UnwrapRequestStep unwrapRequestStep,
                                       ForwardToDestinationStep forwardToDestinationStep,
                                       RespondTransfers respondTransfers) {

        assert participantStore != null;
        assert receiveTransferStep != null;
        assert reserveTransferStep != null;
        assert abortTransferStepPublisher != null;
        assert reservePayerPositionStep != null;
        assert rollbackReservationStepPublisher != null;
        assert unwrapRequestStep != null;
        assert forwardToDestinationStep != null;
        assert respondTransfers != null;

        this.participantStore = participantStore;
        this.receiveTransferStep = receiveTransferStep;
        this.reserveTransferStep = reserveTransferStep;
        this.abortTransferStepPublisher = abortTransferStepPublisher;
        this.reservePayerPositionStep = reservePayerPositionStep;
        this.rollbackReservationStepPublisher = rollbackReservationStepPublisher;
        this.unwrapRequestStep = unwrapRequestStep;
        this.forwardToDestinationStep = forwardToDestinationStep;
        this.respondTransfers = respondTransfers;
    }

    @Override
    public Output execute(Input input) {

        final var udfTransferId = new UdfTransferId(input.transfersPostRequest().getTransferId());

        LOGGER.info("PostTransfersCommandHandler : input : ({})", ObjectLogger.log(input));

        TransactionId transactionId = null;
        Instant transactionAt = null;
        TransferId transferId;
        PositionUpdateId positionReservationId;

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
                new UnwrapRequestStep.Input(payerFsp, payeeFsp, udfTransferId, request));

            var ilpCondition = unwrapRequestOutput.ilpCondition();
            var ilpPacket = unwrapRequestOutput.ilpPacket();
            var agreement = unwrapRequestOutput.agreement();

            // 2. Open a transaction. And receive the Transfer.
            var receiveTransferOutput = this.receiveTransferStep.execute(
                new ReceiveTransferStep.Input(
                    payerFsp, payeeFsp, udfTransferId, request.getAmount(), ilpCondition, ilpPacket,
                    agreement, unwrapRequestOutput.requestExpiration(),
                    request.getExtensionList()));

            transactionId = receiveTransferOutput.transactionId();
            transactionAt = receiveTransferOutput.transactionAt();
            transferId = receiveTransferOutput.transferId();

            // 3. Reserve a position of Payer.
            ReservePayerPositionStep.Output reservePayerPositionOutput = null;

            try {

                reservePayerPositionOutput = this.reservePayerPositionStep.execute(
                    new ReservePayerPositionStep.Input(
                        udfTransferId, transactionId, transactionAt, payerFsp, payeeFsp,
                        agreement.transferAmount().getCurrency(),
                        new BigDecimal(agreement.transferAmount().getAmount())));

                positionReservationId = reservePayerPositionOutput.positionReservationId();

            } catch (NoPositionUpdateForTransactionException e) {

                LOGGER.error("Error:", e);

                this.abortTransferStepPublisher.publish(
                    new AbortTransferStep.Input(
                        udfTransferId, transactionId, transferId,
                        AbortReason.POSITION_RESERVATION_FAILURE, Direction.TO_PAYEE, null));

                throw new FspiopException(FspiopErrors.INTERNAL_SERVER_ERROR, e.getMessage());

            } catch (PositionLimitExceededException e) {

                LOGGER.error("Error:", e);

                this.abortTransferStepPublisher.publish(new AbortTransferStep.Input(
                    udfTransferId, transactionId, transferId, AbortReason.POSITION_LIMIT_EXCEEDED,
                    Direction.TO_PAYEE, null));

                throw new FspiopException(FspiopErrors.PAYER_LIMIT_ERROR);

            } catch (Exception e) {

                LOGGER.error("Error:", e);
                throw e;
            }

            // 4. Update the Transfer as RESERVED
            try {

                this.reserveTransferStep.execute(
                    new ReserveTransferStep.Input(
                        udfTransferId, transactionId, transferId, positionReservationId));

            } catch (Exception e) {

                LOGGER.error("Error:", e);

                this.rollbackReservationStepPublisher.publish(new RollbackReservationStep.Input(
                    udfTransferId, transactionId, transferId,
                    reservePayerPositionOutput.positionReservationId(),
                    "Failed to update the Transfer state to RESERVED."));

                this.abortTransferStepPublisher.publish(
                    new AbortTransferStep.Input(
                        udfTransferId, transactionId, transferId,
                        AbortReason.UNABLE_TO_RESERVE_TRANSFER, Direction.TO_PAYEE, null));

                throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, e.getMessage());
            }

            // 5. Forward the request to Payee.
            try {

                var payeeBaseUrl = payeeFsp.endpoints().get(EndpointType.TRANSFERS).baseUrl();

                this.forwardToDestinationStep.execute(new ForwardToDestinationStep.Input(
                    udfTransferId, transactionId, payeeFspCode.value(), payeeBaseUrl,
                    input.request()));

            } catch (Exception e) {

                LOGGER.error("Error:", e);

                this.rollbackReservationStepPublisher.publish(new RollbackReservationStep.Input(
                    udfTransferId, transactionId, transferId,
                    reservePayerPositionOutput.positionReservationId(), e.getMessage()));

                this.abortTransferStepPublisher.publish(new AbortTransferStep.Input(
                    udfTransferId, transactionId, transferId, AbortReason.UNABLE_TO_REACH_PAYEE,
                    Direction.TO_PAYEE, null));

                throw e;
            }

        } catch (Exception e) {

            LOGGER.error("Error:", e);

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

        }

        return new Output();
    }

}
