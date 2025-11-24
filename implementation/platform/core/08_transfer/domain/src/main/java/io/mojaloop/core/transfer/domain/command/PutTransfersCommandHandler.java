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
import io.mojaloop.component.misc.logger.ObjectLogger;
import io.mojaloop.core.common.datatype.enums.Direction;
import io.mojaloop.core.common.datatype.enums.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.enums.transfer.AbortStage;
import io.mojaloop.core.common.datatype.enums.transfer.DisputeType;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.transfer.TransferId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.store.ParticipantStore;
import io.mojaloop.core.transaction.contract.command.CloseTransactionCommand;
import io.mojaloop.core.transaction.producer.publisher.CloseTransactionPublisher;
import io.mojaloop.core.transfer.contract.command.PutTransfersCommand;
import io.mojaloop.core.transfer.domain.command.step.financial.FulfilPositions;
import io.mojaloop.core.transfer.domain.command.step.financial.RollbackReservation;
import io.mojaloop.core.transfer.domain.command.step.fspiop.CommitTransferToPayer;
import io.mojaloop.core.transfer.domain.command.step.fspiop.ForwardToDestination;
import io.mojaloop.core.transfer.domain.command.step.fspiop.PatchTransferToPayee;
import io.mojaloop.core.transfer.domain.command.step.fspiop.UnwrapResponse;
import io.mojaloop.core.transfer.domain.command.step.stateful.AbortTransfer;
import io.mojaloop.core.transfer.domain.command.step.stateful.CommitTransfer;
import io.mojaloop.core.transfer.domain.command.step.stateful.DisputeTransfer;
import io.mojaloop.core.transfer.domain.command.step.stateful.FetchTransfer;
import io.mojaloop.core.wallet.contract.exception.position.FailedToCommitReservationException;
import io.mojaloop.fspiop.common.type.Payer;
import io.mojaloop.fspiop.component.handy.FspiopErrorResponder;
import io.mojaloop.fspiop.component.handy.FspiopUrls;
import io.mojaloop.fspiop.service.api.transfers.RespondTransfers;
import io.mojaloop.fspiop.spec.core.Currency;
import io.mojaloop.fspiop.spec.core.TransferState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Service
public class PutTransfersCommandHandler implements PutTransfersCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostTransfersCommandHandler.class);

    private final ParticipantStore participantStore;

    // Stateful steps
    private final FetchTransfer fetchTransfer;

    private final AbortTransfer abortTransfer;

    private final CommitTransfer commitTransfer;

    private final DisputeTransfer disputeTransfer;

    // Financial steps
    private final FulfilPositions fulfilPositions;

    private final RollbackReservation rollbackReservation;

    // FSPIOP steps
    private final UnwrapResponse unwrapResponse;

    private final CommitTransferToPayer commitTransferToPayer;

    private final ForwardToDestination forwardToDestination;

    private final PatchTransferToPayee patchTransferToPayee;

    private final RespondTransfers respondTransfers;

    private final CloseTransactionPublisher closeTransactionPublisher;

    public PutTransfersCommandHandler(ParticipantStore participantStore,
                                      FetchTransfer fetchTransfer,
                                      AbortTransfer abortTransfer,
                                      CommitTransfer commitTransfer,
                                      DisputeTransfer disputeTransfer,
                                      FulfilPositions fulfilPositions,
                                      RollbackReservation rollbackReservation,
                                      UnwrapResponse unwrapResponse,
                                      CommitTransferToPayer commitTransferToPayer,
                                      ForwardToDestination forwardToDestination,
                                      PatchTransferToPayee patchTransferToPayee,
                                      RespondTransfers respondTransfers,
                                      CloseTransactionPublisher closeTransactionPublisher) {

        assert participantStore != null;
        assert fetchTransfer != null;
        assert abortTransfer != null;
        assert commitTransfer != null;
        assert disputeTransfer != null;
        assert fulfilPositions != null;
        assert rollbackReservation != null;
        assert unwrapResponse != null;
        assert commitTransferToPayer != null;
        assert forwardToDestination != null;
        assert patchTransferToPayee != null;
        assert respondTransfers != null;
        assert closeTransactionPublisher != null;

        this.participantStore = participantStore;
        this.fetchTransfer = fetchTransfer;
        this.abortTransfer = abortTransfer;
        this.commitTransfer = commitTransfer;
        this.disputeTransfer = disputeTransfer;
        this.fulfilPositions = fulfilPositions;
        this.rollbackReservation = rollbackReservation;
        this.unwrapResponse = unwrapResponse;
        this.commitTransferToPayer = commitTransferToPayer;
        this.forwardToDestination = forwardToDestination;
        this.patchTransferToPayee = patchTransferToPayee;
        this.respondTransfers = respondTransfers;
        this.closeTransactionPublisher = closeTransactionPublisher;
    }

    @Override
    @Write
    public Output execute(Input input) {

        LOGGER.info("PutTransfersCommandHandler : input: ({})", ObjectLogger.log(input));

        final var CONTEXT = "PutTransfers";

        var udfTransferId = input.udfTransferId();

        TransferId transferId = null;
        TransferState state = null;
        PositionUpdateId reservationId = null;
        Currency currency = null;
        BigDecimal transferAmount = null;
        TransactionId transactionId = null;
        Instant transactionAt = null;
        Exception errorOccurred = null;

        boolean payeeReserved = false;

        FspCode payerFspCode = null;
        FspData payerFsp = null;
        FspCode payeeFspCode = null;
        FspData payeeFsp = null;

        FetchTransfer.Output fetchTransferOutput = null;
        UnwrapResponse.Output unwrapResponseOutput = null;

        try {

            payerFspCode = new FspCode(input.request().payer().fspCode());
            payerFsp = this.participantStore.getFspData(payerFspCode);

            payeeFspCode = new FspCode(input.request().payee().fspCode());
            payeeFsp = this.participantStore.getFspData(payeeFspCode);

            var putTransfersResponse = input.transfersIDPutResponse();

            // 1. Fetch the transfer.
            fetchTransferOutput = this.fetchTransfer.execute(
                new FetchTransfer.Input(udfTransferId));

            transferId = fetchTransferOutput.transferId();
            state = fetchTransferOutput.state();
            reservationId = fetchTransferOutput.reservationId();
            currency = fetchTransferOutput.currency();
            transferAmount = fetchTransferOutput.transferAmount();
            transactionId = fetchTransferOutput.transactionId();
            transactionAt = fetchTransferOutput.transactionAt();

            if (transferId == null) {
                // Surely non-existence Transfer.
                LOGGER.warn(
                    "Transfer not found for udfTransferId : ({}). Possible non-existence Transfer. Ignored it.",
                    udfTransferId.getId());
                return new Output();

            } else if (state == TransferState.COMMITTED || state == TransferState.ABORTED) {
                // This transfer has already been processed. Just forward back to Payer. It seems Payer is retrieving again.
                LOGGER.info(
                    "Transfer already processed for udfTransferId : ({}). Ignored it.",
                    udfTransferId.getId());

                var stateFromRequest = putTransfersResponse.getTransferState();

                if (state == stateFromRequest) {

                    LOGGER.info(
                        "Possible that Payer is fetching Transfer. So, Payee responded the previous Transfer again. Forwarding back to Payer.");

                    var payerBaseUrl = payerFsp.endpoints().get(EndpointType.TRANSFERS).baseUrl();
                    LOGGER.info("Forwarding request to payer FSP (BaseUrl): ({})", payerBaseUrl);

                    try {

                        this.forwardToDestination.execute(
                            new ForwardToDestination.Input(
                                CONTEXT, transactionId, payeeFspCode.value(), payerBaseUrl,
                                input.request()));

                    } catch (Exception ignored) { }

                    LOGGER.info(
                        "Done forwarding request to payer FSP (BaseUrl): ({})", payerBaseUrl);

                } else {

                    LOGGER.warn(
                        "Payee responded the previous Transfer with a different state. Ignoring it.");
                }

                return new Output();
            }

            // 2. Unwrap the response. Proceed, according to the state.
            try {

                unwrapResponseOutput = this.unwrapResponse.execute(
                    new UnwrapResponse.Input(putTransfersResponse));

            } catch (Exception e) {

                LOGGER.error("Error:", e);
                // Payee responded with some validation error.
                RollbackReservation.Output rollbackReservationOutput = null;

                try {

                    // Roll back the Payer position reservation.
                    rollbackReservationOutput = this.rollbackReservation.execute(
                        new RollbackReservation.Input(
                            CONTEXT, transactionId, reservationId,
                            e.getMessage()));

                } catch (Exception e1) {

                    // Failed to roll back Payer's position. This is dispute. ‼️
                    this.disputeTransfer.execute(new DisputeTransfer.Input(
                        CONTEXT, transactionId, transferId, AbortStage.UNWRAP_TRANSFER_RESPONSE,
                        "Payee responded with an error.", DisputeType.PAYER,
                        "Failed to rollback reservation."));
                }

                if (rollbackReservationOutput != null) {

                    this.abortTransfer.execute(new AbortTransfer.Input(
                        CONTEXT, transactionId, transferId, AbortStage.UNWRAP_TRANSFER_RESPONSE,
                        "Payee responded with an error.", rollbackReservationOutput.rollbackId(),
                        Direction.FROM_PAYEE, putTransfersResponse.getExtensionList()));

                }

                // Although Payee responded with an error, we still need to inform back
                // to Payee the state of the transfer.
                this.patchTransferToPayee.execute(
                    new PatchTransferToPayee.Input(
                        CONTEXT, transactionId, udfTransferId, payeeFsp, TransferState.ABORTED,
                        Map.of()));

                throw e;
            }

            // 3. Verify ILP. Do we need ?
            // ? ? ?

            // 4. Handle ABORTED state and RESERVED state.
            if (unwrapResponseOutput.state() == TransferState.ABORTED) {

                // Oh, Payee aborted the transfer. We roll back the Payer position reservation.
                // Mark the Transfer as ABORTED. Then inform back to Payer that the transfer is ABORTED.
                LOGGER.info(
                    "Payee responded with the aborted transfer : udfTransferId : ({}).",
                    udfTransferId.getId());

                RollbackReservation.Output rollbackReservationOutput = null;

                try {

                    // Roll back the Payer position reservation.
                    rollbackReservationOutput = this.rollbackReservation.execute(
                        new RollbackReservation.Input(
                            CONTEXT, transactionId, reservationId,
                            "Payee aborted the transfer."));

                } catch (Exception e1) {

                    // Failed to roll back Payer's position. This is dispute. ‼️
                    this.disputeTransfer.execute(new DisputeTransfer.Input(
                        CONTEXT, transactionId, transferId, AbortStage.PAYEE_ABORTED_TRANSFER,
                        "Payee aborted the transfer.", DisputeType.PAYER,
                        "Failed to rollback Payer's position reservation."));
                }

                if (rollbackReservationOutput != null) {

                    this.abortTransfer.execute(new AbortTransfer.Input(
                        CONTEXT, transactionId, transferId, AbortStage.PAYEE_ABORTED_TRANSFER,
                        "Payee aborted the transfer.", rollbackReservationOutput.rollbackId(),
                        Direction.FROM_PAYEE, putTransfersResponse.getExtensionList()));

                }

                // Forward the Payee's response to Payer.
                var payerBaseUrl = payerFsp.endpoints().get(EndpointType.TRANSFERS).baseUrl();
                LOGGER.info("Forwarding request back to payer FSP (BaseUrl): ({})", payerBaseUrl);

                try {

                    this.forwardToDestination.execute(
                        new ForwardToDestination.Input(
                            CONTEXT, transactionId, payerFspCode.value(), payerBaseUrl,
                            input.request()));

                    LOGGER.info(
                        "Done forwarding request back to payer FSP (BaseUrl): ({})", payerBaseUrl);

                } catch (Exception e) {

                    LOGGER.error("(Ignored) Error:", e);
                    // 💡We don't worry because the Transfer is already aborted.
                }

            } else if (unwrapResponseOutput.state() == TransferState.RESERVED) {

                LOGGER.info(
                    "Payee responded with the reserved transfer : udfTransferId : ({}).",
                    udfTransferId.getId());

                // 💡Upon receiving the RESERVED transfer from Payee. Do these:
                // 1️⃣Fulfil all positions (commit the reservation and decrease Payee position)
                // 2️⃣Inform Payer that the transfer is COMMITTED.
                // 3️⃣Inform Payee that the transfer is COMMITTED.
                // 4️⃣Update Transfer state to COMMITTED.
                // ‼️At any of these stages, if something went wrong, do these:
                // 👉Inform Payee that the transfer is ABORTED.
                // 👉Dispute the transfer.
                // 👉Send the error respond to Payer.

                FulfilPositions.Output fulfilPositionsOutput = null;

                try {

                    fulfilPositionsOutput = this.fulfilPositions.execute(new FulfilPositions.Input(
                        CONTEXT, transactionId, transactionAt, payerFsp, payeeFsp, reservationId,
                        currency, transferAmount));

                } catch (Exception e) {

                    LOGGER.error("Error:", e);

                    // ‼️Problem occurred while fulfilling positions. 💰
                    // So, we inform the Payee to ABORT the transfer.
                    // And we return the error to the Payer.
                    try {

                        this.patchTransferToPayee.execute(new PatchTransferToPayee.Input(
                            CONTEXT, transactionId, udfTransferId, payeeFsp, TransferState.ABORTED,
                            Map.of()));

                    } catch (Exception ignored) {
                        // OMG. Payee is also unreachable. 🥲
                        // What is happening? 😭
                    }

                    if (e instanceof FailedToCommitReservationException ce) {

                        // The problem is only at Payer because the committing the reservation failed.
                        // Dispute for Payer ‼️
                        this.disputeTransfer.execute(new DisputeTransfer.Input(
                            CONTEXT, transactionId, transferId, AbortStage.FULFILLED_ALL_POSITIONS,
                            "Failed to fulfil all positions.", DisputeType.PAYER,
                            "Unable to commit Payer's position reservation. ReservationId : " +
                                ce.getReservationId()));
                    }

                    throw e;
                }

                try {

                    this.commitTransferToPayer.execute(new CommitTransferToPayer.Input(
                        CONTEXT, transactionId, udfTransferId, payerFsp,
                        putTransfersResponse.getFulfilment(),
                        putTransfersResponse.getCompletedTimestamp(),
                        putTransfersResponse.getExtensionList()));

                } catch (Exception e) {

                    LOGGER.error("Error:", e);
                    // We try to inform Payer that the transfer is COMMITTED but failed.
                    // So, we need to inform the Payee to ABORT at its side.
                    // For Payer, it is dispute.
                    try {

                        this.patchTransferToPayee.execute(new PatchTransferToPayee.Input(
                            CONTEXT, transactionId, udfTransferId, payeeFsp, TransferState.ABORTED,
                            Map.of()));

                    } catch (Exception ignored) {
                        // OMG. Both are unreachable. 🥲
                    }

                    this.disputeTransfer.execute(new DisputeTransfer.Input(
                        CONTEXT, transactionId, transferId, AbortStage.COMMIT_TRANSFER_TO_PAYER,
                        "Failed to inform Payer that the transfer is COMMITTED.", DisputeType.BOTH,
                        "All positions were fulfilled, but failed to inform Payer that the transfer is COMMITTED."));

                    throw e;
                }

                try {

                    // Now inform back to Payee that the transfer is COMMITTED.
                    this.patchTransferToPayee.execute(new PatchTransferToPayee.Input(
                        CONTEXT, transactionId, udfTransferId, payeeFsp, TransferState.COMMITTED,
                        Map.of()));

                } catch (Exception e) {

                    LOGGER.error("Error:", e);
                    // After we have informed the Payer that the Transfer is COMMITTED, we
                    // must inform the Payee too. But for some reason, it failed.
                    // This is DISPUTE.
                    this.disputeTransfer.execute(new DisputeTransfer.Input(
                        CONTEXT, transactionId, transferId, AbortStage.PATCH_TO_PAYEE,
                        "Failed to inform Payer that the transfer is COMMITTED.", DisputeType.BOTH,
                        "All positions were fulfilled, but failed to inform Payee that the transfer is COMMITTED."));

                    throw e;
                }

                try {

                    this.commitTransfer.execute(new CommitTransfer.Input(
                        CONTEXT, transactionId, transferId, unwrapResponseOutput.ilpFulfilment(),
                        fulfilPositionsOutput.payerCommitId(),
                        fulfilPositionsOutput.payeeCommitId(), unwrapResponseOutput.completedAt()));

                } catch (Exception e) {

                    LOGGER.error("Error:", e);

                    // Big problem! Positions were fulfilled, but CommitTransfer failed.
                    // So, we inform the Payee to ABORT the transfer.
                    // And we return the error to the Payer.
                    try {

                        this.patchTransferToPayee.execute(new PatchTransferToPayee.Input(
                            CONTEXT, transactionId, udfTransferId, payeeFsp, TransferState.ABORTED,
                            Map.of()));

                    } catch (Exception ignored) {
                        // OMG. Payee is also unreachable. 🥲
                        // What is happening? 😭
                    }

                    this.disputeTransfer.execute(new DisputeTransfer.Input(
                        CONTEXT, transactionId, transferId, AbortStage.COMMITTED_TRANSFER,
                        "Failed to commit transfer.", DisputeType.BOTH,
                        "All positions were fulfilled, but failed to update Transfer state to COMMITTED."));

                    throw e;
                }

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

                    errorOccurred = e1;
                }

            }

        } finally {

            if (transferId != null) {

                LOGGER.info("Closing transaction : udfTransferId : ({})", udfTransferId.getId());

                this.closeTransactionPublisher.publish(
                    new CloseTransactionCommand.Input(
                        transactionId,
                        errorOccurred != null ? errorOccurred.getMessage() : null));
            }
        }

        LOGGER.info("PutTransfersCommandHandler : done");

        return new Output();
    }

}
