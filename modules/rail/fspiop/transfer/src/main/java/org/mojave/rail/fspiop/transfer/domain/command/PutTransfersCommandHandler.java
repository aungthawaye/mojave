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
import org.mojave.common.datatype.enums.Direction;
import org.mojave.common.datatype.enums.participant.EndpointType;
import org.mojave.common.datatype.enums.transfer.AbortReason;
import org.mojave.common.datatype.enums.transfer.TransferStatus;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.identifier.transfer.TransferId;
import org.mojave.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.common.datatype.type.participant.FspCode;
import org.mojave.core.participant.contract.data.FspData;
import org.mojave.core.participant.store.ParticipantStore;
import org.mojave.rail.fspiop.transfer.contract.command.PutTransfersCommand;
import org.mojave.rail.fspiop.transfer.contract.command.step.financial.FulfilPositionsStep;
import org.mojave.rail.fspiop.transfer.contract.command.step.financial.PostLedgerFlowStep;
import org.mojave.rail.fspiop.transfer.contract.command.step.financial.RollbackReservationStep;
import org.mojave.rail.fspiop.transfer.contract.command.step.fspiop.CommitTransferToPayerStep;
import org.mojave.rail.fspiop.transfer.contract.command.step.fspiop.ForwardToDestinationStep;
import org.mojave.rail.fspiop.transfer.contract.command.step.fspiop.PatchTransferToPayeeStep;
import org.mojave.rail.fspiop.transfer.contract.command.step.fspiop.UnwrapResponseStep;
import org.mojave.rail.fspiop.transfer.contract.command.step.stateful.AbortTransferStep;
import org.mojave.rail.fspiop.transfer.contract.command.step.stateful.FetchTransferStep;
import org.mojave.rail.fspiop.transfer.domain.command.step.fspiop.CommitTransferToPayerStepHandler;
import org.mojave.rail.fspiop.transfer.domain.command.step.fspiop.PatchTransferToPayeeStepHandler;
import org.mojave.rail.fspiop.transfer.domain.command.step.stateful.CommitTransferStepHandler;
import org.mojave.rail.fspiop.transfer.domain.kafka.publisher.AbortTransferStepPublisher;
import org.mojave.rail.fspiop.transfer.domain.kafka.publisher.CommitTransferStepPublisher;
import org.mojave.rail.fspiop.transfer.domain.kafka.publisher.DisputeTransferStepPublisher;
import org.mojave.rail.fspiop.transfer.domain.kafka.publisher.RollbackReservationStepPublisher;
import org.mojave.rail.fspiop.component.handy.FspiopErrorResponder;
import org.mojave.rail.fspiop.component.handy.FspiopUrls;
import org.mojave.rail.fspiop.component.type.Payer;
import org.mojave.rail.fspiop.bootstrap.api.transfers.RespondTransfers;
import org.mojave.scheme.fspiop.core.Currency;
import org.mojave.scheme.fspiop.core.TransferState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Service
public class PutTransfersCommandHandler implements PutTransfersCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(PutTransfersCommandHandler.class);

    private final ParticipantStore participantStore;

    // Stateful steps
    private final FetchTransferStep fetchTransferStep;

    private final AbortTransferStepPublisher abortTransferStepPublisher;

    private final CommitTransferStepPublisher commitTransferStepPublisher;

    private final DisputeTransferStepPublisher disputeTransferStepPublisher;

    // Financial steps
    private final FulfilPositionsStep fulfilPositionsStep;

    private final RollbackReservationStepPublisher rollbackReservationStepPublisher;

    private final PostLedgerFlowStep postLedgerFlowStep;

    // FSPIOP steps
    private final UnwrapResponseStep unwrapResponseStep;

    private final CommitTransferToPayerStep commitTransferToPayerStep;

    private final ForwardToDestinationStep forwardToDestinationStep;

    private final PatchTransferToPayeeStep patchTransferToPayeeStep;

    private final RespondTransfers respondTransfers;

    public PutTransfersCommandHandler(ParticipantStore participantStore,
                                      FetchTransferStep fetchTransferStep,
                                      AbortTransferStepPublisher abortTransferStepPublisher,
                                      CommitTransferStepPublisher commitTransferStepPublisher,
                                      DisputeTransferStepPublisher disputeTransferStepPublisher,
                                      FulfilPositionsStep fulfilPositionsStep,
                                      RollbackReservationStepPublisher rollbackReservationStepPublisher,
                                      PostLedgerFlowStep postLedgerFlowStep,
                                      UnwrapResponseStep unwrapResponseStep,
                                      CommitTransferToPayerStep commitTransferToPayerStep,
                                      ForwardToDestinationStep forwardToDestinationStep,
                                      PatchTransferToPayeeStep patchTransferToPayeeStep,
                                      RespondTransfers respondTransfers) {

        Objects.requireNonNull(participantStore);
        Objects.requireNonNull(fetchTransferStep);
        Objects.requireNonNull(abortTransferStepPublisher);
        Objects.requireNonNull(commitTransferStepPublisher);
        Objects.requireNonNull(disputeTransferStepPublisher);
        Objects.requireNonNull(fulfilPositionsStep);
        Objects.requireNonNull(rollbackReservationStepPublisher);
        Objects.requireNonNull(postLedgerFlowStep);
        Objects.requireNonNull(unwrapResponseStep);
        Objects.requireNonNull(commitTransferToPayerStep);
        Objects.requireNonNull(forwardToDestinationStep);
        Objects.requireNonNull(patchTransferToPayeeStep);
        Objects.requireNonNull(respondTransfers);

        this.participantStore = participantStore;
        this.fetchTransferStep = fetchTransferStep;
        this.abortTransferStepPublisher = abortTransferStepPublisher;
        this.commitTransferStepPublisher = commitTransferStepPublisher;
        this.disputeTransferStepPublisher = disputeTransferStepPublisher;
        this.fulfilPositionsStep = fulfilPositionsStep;
        this.rollbackReservationStepPublisher = rollbackReservationStepPublisher;
        this.postLedgerFlowStep = postLedgerFlowStep;
        this.unwrapResponseStep = unwrapResponseStep;
        this.commitTransferToPayerStep = commitTransferToPayerStep;
        this.forwardToDestinationStep = forwardToDestinationStep;
        this.patchTransferToPayeeStep = patchTransferToPayeeStep;
        this.respondTransfers = respondTransfers;
    }

    @Override
    public Output execute(Input input) {

        LOGGER.info("PutTransfersCommandHandler : input: ({})", ObjectLogger.log(input));

        var udfTransferId = input.udfTransferId();

        TransferId transferId = null;
        TransferStatus state = null;
        PositionUpdateId reservationId = null;
        Currency currency = null;
        BigDecimal transferAmount = null;
        TransactionId transactionId = null;
        Instant transactionAt = null;

        FspCode payerFspCode = null;
        FspData payerFsp = null;
        FspCode payeeFspCode = null;
        FspData payeeFsp = null;

        FetchTransferStep.Output fetchTransferOutput = null;
        UnwrapResponseStep.Output unwrapResponseOutput = null;

        var unreachablePayer = false;

        try {

            payerFspCode = new FspCode(input.request().payer().fspCode());
            payerFsp = this.participantStore.getFspData(payerFspCode);

            payeeFspCode = new FspCode(input.request().payee().fspCode());
            payeeFsp = this.participantStore.getFspData(payeeFspCode);

            var putTransfersResponse = input.transfersIDPutResponse();

            // 1. Fetch the transfer.
            fetchTransferOutput = this.fetchTransferStep.execute(
                new FetchTransferStep.Input(udfTransferId, payerFsp.fspId(), payeeFsp.fspId()));

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

            } else if (state == TransferStatus.COMMITTED || state == TransferStatus.ABORTED ||
                           state == TransferStatus.DISPUTED) {
                // This transfer has already been processed. Just forward back to Payer. It seems Payer is retrieving again.
                LOGGER.info(
                    "Transfer already processed for udfTransferId : ({}). Ignored it.",
                    udfTransferId.getId());

                var stateFromRequest = putTransfersResponse.getTransferState();

                if (stateFromRequest != null &&
                        (stateFromRequest.name().equals(TransferState.RECEIVED.name()) ||
                             stateFromRequest.name().equals(TransferState.RESERVED.name()))) {

                    LOGGER.warn(
                        "Payee responded the previous Transfer with a different status. Ignoring it.");

                } else {

                    LOGGER.info(
                        "Possible that Payer is fetching Transfer. So, Payee responded the previous Transfer again. Forwarding back to Payer.");

                    var payerBaseUrl = payerFsp.endpoints().get(EndpointType.TRANSFERS).baseUrl();
                    LOGGER.info("Forwarding request to payer FSP (BaseUrl): ({})", payerBaseUrl);

                    try {

                        this.forwardToDestinationStep.execute(new ForwardToDestinationStep.Input(
                            udfTransferId, transactionId, payeeFspCode.value(), payerBaseUrl,
                            input.request()));

                    } catch (Exception ignored) { }

                    LOGGER.info(
                        "Done forwarding the completed request to payer FSP (BaseUrl): ({})",
                        payerBaseUrl);

                }

                return new Output();

            }

            // 2. Unwrap the response. Proceed, according to the status.
            try {

                unwrapResponseOutput = this.unwrapResponseStep.execute(
                    new UnwrapResponseStep.Input(udfTransferId, putTransfersResponse));

            } catch (Exception e) {

                LOGGER.error("Error:", e);
                // Payee responded with some validation error.
                // Roll back the Payer position reservation.
                this.rollbackReservationStepPublisher.publish(
                    new RollbackReservationStep.Input(
                        udfTransferId, transactionId, transferId,
                        reservationId, e.getMessage()));

                this.abortTransferStepPublisher.publish(new AbortTransferStep.Input(
                    udfTransferId, transactionId, transferId,
                    AbortReason.VALIDATION_ERROR_IN_PAYEE_RESPONSE, Direction.FROM_PAYEE,
                    putTransfersResponse.getExtensionList()));

                // Although Payee responded with an error, we still need to inform back
                // to Payee the status of the transfer.
                this.patchTransferToPayeeStep.execute(new PatchTransferToPayeeStepHandler.Input(
                    udfTransferId, transactionId, payeeFsp, TransferState.ABORTED,
                    putTransfersResponse.getExtensionList()));

                throw e;
            }

            // 3. Verify ILP. Do we need ?
            // ? ? ?

            // 4. Handle ABORTED status and RESERVED status.

            if (unwrapResponseOutput.state() == TransferState.ABORTED) {

                // Oh, Payee aborted the transfer.
                // Inform back to Payer that the transfer is ABORTED.
                // We roll back the Payer position reservation.
                // Mark the Transfer as ABORTED.
                LOGGER.info(
                    "Payee responded with the aborted transfer : udfTransferId : ({}).",
                    udfTransferId.getId());

                // Forward the Payee's response to Payer.
                var payerBaseUrl = payerFsp.endpoints().get(EndpointType.TRANSFERS).baseUrl();
                LOGGER.info("Forwarding request back to payer FSP (BaseUrl): ({})", payerBaseUrl);

                try {

                    this.forwardToDestinationStep.execute(new ForwardToDestinationStep.Input(
                        udfTransferId, transactionId, payerFspCode.value(), payerBaseUrl,
                        input.request()));

                    LOGGER.info(
                        "Done forwarding request back to payer FSP (BaseUrl): ({})", payerBaseUrl);

                } catch (Exception e) {

                    // 💡We don't worry because the Transfer is already aborted.
                    LOGGER.error("(Ignored) Error:", e);
                    unreachablePayer = true;
                }

                // Roll back the Payer position reservation.
                this.rollbackReservationStepPublisher.publish(new RollbackReservationStep.Input(
                    udfTransferId, transactionId, transferId, reservationId,
                    "Payee aborted the transfer."));

                this.abortTransferStepPublisher.publish(new AbortTransferStep.Input(
                    udfTransferId, transactionId, transferId, AbortReason.PAYEE_ABORTED_TRANSFER,
                    Direction.FROM_PAYEE, putTransfersResponse.getExtensionList()));

            } else if (unwrapResponseOutput.state() == TransferState.RESERVED ||
                           unwrapResponseOutput.state() == TransferState.COMMITTED) {

                LOGGER.info(
                    "Payee responded with the reserved transfer : udfTransferId : ({}).",
                    udfTransferId.getId());

                // 💡Upon receiving the RESERVED transfer from Payee. Do these:
                // 1. Inform Payer that the transfer is COMMITTED.
                // 2. Inform Payee that the transfer is COMMITTED.
                // 3a. Both COMMITTED ->
                //      Fulfil all positions (commit the reservation and decrease Payee position)
                //      Update Transfer status to 'COMMITTED'.
                // 3b. Otherwise ->
                //      Roll back the Payer position reservation.
                //      Update Transfer status to 'ABORTED'.

                // ‼️At any of these stages, if something went wrong, do these:
                // 👉Inform Payee that the transfer is ABORTED.
                // 👉Dispute the transfer.
                // 👉Send the error response to Payer.

                TransferState finalTransferState = null;

                try {

                    this.commitTransferToPayerStep.execute(
                        new CommitTransferToPayerStepHandler.Input(
                            udfTransferId, transactionId, payerFsp,
                            putTransfersResponse.getFulfilment(),
                            putTransfersResponse.getCompletedTimestamp(),
                            putTransfersResponse.getExtensionList()));

                    finalTransferState = TransferState.COMMITTED;

                } catch (Exception e) {

                    LOGGER.error("Error:", e);

                    finalTransferState = TransferState.ABORTED;
                    unreachablePayer = true;
                }

                // We have informed the Payer that the Payee has reserved or committed the transfer.
                if (finalTransferState == TransferState.COMMITTED) {

                    // ‼️Any issue happens from this onward, it will be dispute.
                    try {

                        this.fulfilPositionsStep.execute(new FulfilPositionsStep.Input(
                            udfTransferId, transactionId, transactionAt, payerFsp, payeeFsp,
                            reservationId, currency, "-"));

                        this.postLedgerFlowStep.execute(new PostLedgerFlowStep.Input(
                            udfTransferId, transactionId, transactionAt, currency, payerFsp,
                            payeeFsp, transferAmount, BigDecimal.ZERO, BigDecimal.ZERO));

                        this.commitTransferStepPublisher.publish(
                            new CommitTransferStepHandler.Input(
                                udfTransferId, transactionId, transferId,
                                unwrapResponseOutput.ilpFulfilment(),
                                unwrapResponseOutput.completedAt(),
                                putTransfersResponse.getExtensionList()));

                    } catch (Exception e) {

                        LOGGER.error("Error:", e);
                        finalTransferState = TransferState.ABORTED;
                    }

                } else {

                    // If we cannot commit Transfer to Payer, it is ABORTED.
                    // So, we need to roll back the Payer position reservation.
                    try {


                        this.rollbackReservationStepPublisher.publish(
                            new RollbackReservationStep.Input(
                                udfTransferId, transactionId, transferId, reservationId,
                                "Failed to COMMIT transfer to Payer."));

                    } catch (Exception e) {

                        LOGGER.error("Error:", e);
                    }

                }

                try {

                    if (unwrapResponseOutput.state() == TransferState.RESERVED) {
                        this.patchTransferToPayeeStep.execute(
                            new PatchTransferToPayeeStepHandler.Input(
                                udfTransferId, transactionId, payeeFsp, finalTransferState,
                                putTransfersResponse.getExtensionList()));
                    }

                } catch (Exception e) {

                    LOGGER.error("Error:", e);
                }

            }

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            if (payerFsp != null && !unreachablePayer) {

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
