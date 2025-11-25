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

package io.mojaloop.core.transfer.domain.command;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.component.misc.logger.ObjectLogger;
import io.mojaloop.core.common.datatype.enums.Direction;
import io.mojaloop.core.common.datatype.enums.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.enums.transfer.AbortStage;
import io.mojaloop.core.common.datatype.enums.transfer.DisputeType;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.transfer.TransferId;
import io.mojaloop.core.common.datatype.identifier.transfer.UdfTransferId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.store.ParticipantStore;
import io.mojaloop.core.transaction.contract.command.CloseTransactionCommand;
import io.mojaloop.core.transaction.producer.publisher.CloseTransactionPublisher;
import io.mojaloop.core.transfer.contract.command.PostTransfersCommand;
import io.mojaloop.core.transfer.domain.command.step.financial.ReservePayerPosition;
import io.mojaloop.core.transfer.domain.command.step.financial.RollbackReservation;
import io.mojaloop.core.transfer.domain.command.step.fspiop.ForwardToDestination;
import io.mojaloop.core.transfer.domain.command.step.fspiop.UnwrapRequest;
import io.mojaloop.core.transfer.domain.command.step.stateful.AbortTransfer;
import io.mojaloop.core.transfer.domain.command.step.stateful.DisputeTransfer;
import io.mojaloop.core.transfer.domain.command.step.stateful.ReceiveTransfer;
import io.mojaloop.core.transfer.domain.command.step.stateful.ReserveTransfer;
import io.mojaloop.core.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import io.mojaloop.core.wallet.contract.exception.position.PositionLimitExceededException;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Payer;
import io.mojaloop.fspiop.component.handy.FspiopErrorResponder;
import io.mojaloop.fspiop.component.handy.FspiopUrls;
import io.mojaloop.fspiop.service.api.transfers.RespondTransfers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PostTransfersCommandHandler implements PostTransfersCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostTransfersCommandHandler.class);

    private final ParticipantStore participantStore;

    // Stateful steps
    private final ReceiveTransfer receiveTransfer;

    private final ReserveTransfer reserveTransfer;

    private final AbortTransfer abortTransfer;

    private final DisputeTransfer disputeTransfer;

    // Financial steps
    private final ReservePayerPosition reservePayerPosition;

    private final RollbackReservation rollbackReservation;

    // FSPIOP steps
    private final UnwrapRequest unwrapRequest;

    private final ForwardToDestination forwardToDestination;

    private final RespondTransfers respondTransfers;

    private final CloseTransactionPublisher closeTransactionPublisher;

    public PostTransfersCommandHandler(ParticipantStore participantStore,
                                       ReceiveTransfer receiveTransfer,
                                       ReserveTransfer reserveTransfer,
                                       AbortTransfer abortTransfer,
                                       DisputeTransfer disputeTransfer,
                                       ReservePayerPosition reservePayerPosition,
                                       RollbackReservation rollbackReservation,
                                       UnwrapRequest unwrapRequest,
                                       ForwardToDestination forwardToDestination,
                                       RespondTransfers respondTransfers,
                                       CloseTransactionPublisher closeTransactionPublisher) {

        assert participantStore != null;
        assert receiveTransfer != null;
        assert reserveTransfer != null;
        assert abortTransfer != null;
        assert disputeTransfer != null;
        assert reservePayerPosition != null;
        assert rollbackReservation != null;
        assert unwrapRequest != null;
        assert forwardToDestination != null;
        assert respondTransfers != null;
        assert closeTransactionPublisher != null;

        this.participantStore = participantStore;
        this.receiveTransfer = receiveTransfer;
        this.reserveTransfer = reserveTransfer;
        this.abortTransfer = abortTransfer;
        this.disputeTransfer = disputeTransfer;
        this.reservePayerPosition = reservePayerPosition;
        this.rollbackReservation = rollbackReservation;
        this.unwrapRequest = unwrapRequest;
        this.forwardToDestination = forwardToDestination;
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
            var unwrapRequestOutput = this.unwrapRequest.execute(
                new UnwrapRequest.Input(CONTEXT, udfTransferId, payerFsp, payeeFsp, request));

            // 2. Open a transaction. And receive the Transfer.
            var receiveTransferOutput = this.receiveTransfer.execute(new ReceiveTransfer.Input(
                CONTEXT, udfTransferId, payerFsp, payeeFsp, unwrapRequestOutput.payerPartyIdInfo(),
                unwrapRequestOutput.payeePartyIdInfo(), unwrapRequestOutput.currency(),
                unwrapRequestOutput.transferAmount(), unwrapRequestOutput.ilpPacket(),
                unwrapRequestOutput.ilpCondition(), unwrapRequestOutput.requestExpiration(),
                request.getExtensionList()));

            transactionId = receiveTransferOutput.transactionId();
            transactionAt = receiveTransferOutput.transactionAt();
            transferId = receiveTransferOutput.transferId();

            // 3. Reserve a position of Payer.
            try {
                var reservePayerPositionOutput = this.reservePayerPosition.execute(
                    new ReservePayerPosition.Input(
                        CONTEXT, transactionId, transactionAt, payerFsp, payeeFsp,
                        unwrapRequestOutput.currency(), unwrapRequestOutput.transferAmount()));

                positionReservationId = reservePayerPositionOutput.positionReservationId();

            } catch (NoPositionUpdateForTransactionException e) {

                LOGGER.error("Error:", e);

                this.abortTransfer.execute(
                    new AbortTransfer.Input(
                        CONTEXT, transactionId, transferId, AbortStage.RESERVE_PAYER_POSITION,
                        e.getMessage(), null, Direction.TO_PAYEE, null));

                throw new FspiopException(FspiopErrors.INTERNAL_SERVER_ERROR, e.getMessage());

            } catch (PositionLimitExceededException e) {

                LOGGER.error("Error:", e);

                this.abortTransfer.execute(
                    new AbortTransfer.Input(
                        CONTEXT, transactionId, transferId, AbortStage.RESERVE_PAYER_POSITION,
                        e.getMessage(), null, Direction.TO_PAYEE, null));

                throw new FspiopException(FspiopErrors.PAYER_LIMIT_ERROR);
            }

            // 4. Update the Transfer as RESERVED
            try {

                this.reserveTransfer.execute(
                    new ReserveTransfer.Input(
                        CONTEXT, transactionId, transferId,
                        positionReservationId));

            } catch (Exception e) {

                LOGGER.error("Error:", e);

                RollbackReservation.Output rollbackReservationOutput;

                try {

                    // We cannot forward the request to Payee. So, we roll back the Reservation.
                    rollbackReservationOutput = this.rollbackReservation.execute(
                        new RollbackReservation.Input(
                            CONTEXT, transactionId, positionReservationId,
                            "Failed to forward request to payee."));

                } catch (Exception e1) {

                    LOGGER.error("Error:", e1);
                    // We faced a problem while rolling back the Reservation.
                    // So, we abort the transfer. And since unable to roll back the Reservation, we
                    // mark this as a dispute. ‼️
                    this.disputeTransfer.execute(new DisputeTransfer.Input(
                        CONTEXT, transactionId, transferId, AbortStage.RESERVE_TRANSFER,
                        "Unable to reserve Transfer.", DisputeType.PAYER,
                        "Unable to roll back Payer's position reservation"));

                    throw e1;
                }

                this.abortTransfer.execute(new AbortTransfer.Input(
                    CONTEXT, transactionId, transferId, AbortStage.RESERVE_TRANSFER,
                    "Failed to reserve the transfer.", rollbackReservationOutput.rollbackId(),
                    Direction.TO_PAYEE, null));

                throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, e.getMessage());
            }

            // 5. Forward the request to Payee.
            try {

                var payeeBaseUrl = payeeFsp.endpoints().get(EndpointType.TRANSFERS).baseUrl();

                this.forwardToDestination.execute(
                    new ForwardToDestination.Input(
                        CONTEXT, transactionId, payeeFspCode.value(), payeeBaseUrl,
                        input.request()));

            } catch (Exception e) {

                LOGGER.error("Error:", e);

                RollbackReservation.Output rollbackReservationOutput;

                try {

                    // We cannot forward the request to Payee. So, we roll back the Reservation.
                    rollbackReservationOutput = this.rollbackReservation.execute(
                        new RollbackReservation.Input(
                            CONTEXT, transactionId, positionReservationId,
                            "Failed to forward request to payee."));

                } catch (Exception e1) {

                    LOGGER.error("Error:", e1);
                    // We faced a problem while rolling back the Reservation.
                    // So, we abort the transfer. And since unable to roll back the Reservation, we
                    // mark this as a dispute. ‼️
                    this.disputeTransfer.execute(new DisputeTransfer.Input(
                        CONTEXT, transactionId, transferId, AbortStage.FORWARD_TO_PAYEE,
                        "Unable to forward Transfer to Payee.", DisputeType.PAYER,
                        "Unable to roll back Payer's position reservation"));

                    throw e1;
                }

                // And abort the transfer.
                this.abortTransfer.execute(new AbortTransfer.Input(
                    CONTEXT, transactionId, transferId, AbortStage.FORWARD_TO_PAYEE,
                    "Unable to forward request to Payee.", rollbackReservationOutput.rollbackId(),
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
        LOGGER.info("PostTransfersCommandHandler : done : took {} ms", (endAt - startAt) / 1_000_000);

        MDC.remove("REQ_ID");

        return new Output();
    }

}
