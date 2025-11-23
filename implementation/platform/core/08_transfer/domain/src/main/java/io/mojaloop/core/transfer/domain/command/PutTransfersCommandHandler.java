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
import io.mojaloop.core.common.datatype.enums.fspiop.EndpointType;
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
import io.mojaloop.core.transfer.domain.command.step.stateful.FetchTransfer;
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
                // Roll back the Payer position reservation.
                var rollbackReservationOutput = this.rollbackReservation.execute(
                    new RollbackReservation.Input(
                        CONTEXT, transactionId, reservationId,
                        e.getMessage()));

                this.abortTransfer.execute(new AbortTransfer.Input(
                    CONTEXT, transactionId, transferId, rollbackReservationOutput.rollbackId(),
                    "Payee responded with an error."));

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

                var rollbackReservationOutput = this.rollbackReservation.execute(
                    new RollbackReservation.Input(
                        CONTEXT, transactionId, reservationId,
                        "Payee aborted transfer."));

                this.abortTransfer.execute(new AbortTransfer.Input(
                    CONTEXT, transactionId, transferId, rollbackReservationOutput.rollbackId(),
                    "Payee aborted transfer."));

                // Forward the Payee's response back to Payer.
                var payerBaseUrl = payerFsp.endpoints().get(EndpointType.TRANSFERS).baseUrl();
                LOGGER.info("Forwarding request to payer FSP (BaseUrl): ({})", payerBaseUrl);

                this.forwardToDestination.execute(
                    new ForwardToDestination.Input(
                        CONTEXT, transactionId, payeeFspCode.value(), payerBaseUrl,
                        input.request()));

                LOGGER.info("Done forwarding request to payer FSP (BaseUrl): ({})", payerBaseUrl);

            } else if (unwrapResponseOutput.state() == TransferState.RESERVED) {

                LOGGER.info(
                    "Payee responded with the reserved transfer : udfTransferId : ({}).",
                    udfTransferId.getId());
                // Upon receiving the reserved transfer from Payee, Hub must commit the payer position
                // reservation, then give money to the Payee by decreasing its position.
                // Then hub responds to the Payer with the committed transfer.
                // Finally, send the committed transfer to Payee.
                payeeReserved = true;

                var fulfilPositionsOutput = this.fulfilPositions.execute(new FulfilPositions.Input(
                    CONTEXT, transactionId, transactionAt, payerFsp, payeeFsp, reservationId,
                    currency, transferAmount));

                this.commitTransfer.execute(new CommitTransfer.Input(
                    CONTEXT, transactionId, transferId, unwrapResponseOutput.ilpFulfilment(),
                    fulfilPositionsOutput.payerCommitId(), fulfilPositionsOutput.payeeCommitId(),
                    unwrapResponseOutput.completedAt()));

                this.commitTransferToPayer.execute(new CommitTransferToPayer.Input(
                    CONTEXT, transactionId, udfTransferId, payerFsp,
                    putTransfersResponse.getFulfilment(),
                    putTransfersResponse.getCompletedTimestamp(),
                    putTransfersResponse.getExtensionList()));

                this.patchTransferToPayee.execute(
                    new PatchTransferToPayee.Input(
                        CONTEXT, transactionId, udfTransferId, payeeFsp, TransferState.COMMITTED,
                        Map.of()));

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

            try {

                if (payeeReserved) {
                    // Inform back to the Payee only if Transfer exists
                    // and Payee's transfer state is not ABORTED.
                    this.patchTransferToPayee.execute(
                        new PatchTransferToPayee.Input(
                            CONTEXT, transactionId, udfTransferId, payeeFsp, TransferState.ABORTED,
                            Map.of()));
                }

            } catch (Exception e1) {

                LOGGER.error("Error:", e1);

                errorOccurred = e1;
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
