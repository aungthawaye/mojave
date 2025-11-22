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
import io.mojaloop.core.common.datatype.enums.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.store.ParticipantStore;
import io.mojaloop.core.transaction.contract.command.CloseTransactionCommand;
import io.mojaloop.core.transaction.producer.publisher.CloseTransactionPublisher;
import io.mojaloop.core.transfer.contract.command.PutTransfersCommand;
import io.mojaloop.core.transfer.domain.command.step.financial.CommitReservation;
import io.mojaloop.core.transfer.domain.command.step.financial.DecreasePayeePosition;
import io.mojaloop.core.transfer.domain.command.step.financial.RollbackReservation;
import io.mojaloop.core.transfer.domain.command.step.fspiop.ForwardToDestination;
import io.mojaloop.core.transfer.domain.command.step.fspiop.PatchTransferToPayee;
import io.mojaloop.core.transfer.domain.command.step.fspiop.RespondTransferToPayer;
import io.mojaloop.core.transfer.domain.command.step.fspiop.UnwrapResponse;
import io.mojaloop.core.transfer.domain.command.step.stateful.AbortTransfer;
import io.mojaloop.core.transfer.domain.command.step.stateful.CommitTransfer;
import io.mojaloop.core.transfer.domain.command.step.stateful.FetchTransfer;
import io.mojaloop.core.transfer.domain.model.Transfer;
import io.mojaloop.fspiop.spec.core.TransferState;
import io.mojaloop.fspiop.spec.core.TransfersIDPutResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

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
    private final CommitReservation commitReservation;

    private final DecreasePayeePosition decreasePayeePosition;

    private final RollbackReservation rollbackReservation;

    // FSPIOP steps
    private final UnwrapResponse unwrapResponse;

    private final RespondTransferToPayer respondTransferToPayer;

    private final ForwardToDestination forwardToDestination;

    private final PatchTransferToPayee patchTransferToPayee;

    private final CloseTransactionPublisher closeTransactionPublisher;

    public PutTransfersCommandHandler(ParticipantStore participantStore,
                                      FetchTransfer fetchTransfer,
                                      AbortTransfer abortTransfer,
                                      CommitTransfer commitTransfer,
                                      CommitReservation commitReservation,
                                      DecreasePayeePosition decreasePayeePosition,
                                      RollbackReservation rollbackReservation,
                                      UnwrapResponse unwrapResponse,
                                      RespondTransferToPayer respondTransferToPayer,
                                      ForwardToDestination forwardToDestination,
                                      PatchTransferToPayee patchTransferToPayee,
                                      CloseTransactionPublisher closeTransactionPublisher) {

        assert participantStore != null;
        assert fetchTransfer != null;
        assert abortTransfer != null;
        assert commitTransfer != null;
        assert commitReservation != null;
        assert decreasePayeePosition != null;
        assert rollbackReservation != null;
        assert unwrapResponse != null;
        assert respondTransferToPayer != null;
        assert forwardToDestination != null;
        assert patchTransferToPayee != null;
        assert closeTransactionPublisher != null;

        this.participantStore = participantStore;
        this.fetchTransfer = fetchTransfer;
        this.abortTransfer = abortTransfer;
        this.commitTransfer = commitTransfer;
        this.commitReservation = commitReservation;
        this.decreasePayeePosition = decreasePayeePosition;
        this.rollbackReservation = rollbackReservation;
        this.unwrapResponse = unwrapResponse;
        this.respondTransferToPayer = respondTransferToPayer;
        this.forwardToDestination = forwardToDestination;
        this.patchTransferToPayee = patchTransferToPayee;
        this.closeTransactionPublisher = closeTransactionPublisher;
    }

    @Override
    @Write
    public Output execute(Input input) {

        final var CONTEXT = "put-transfers";

        var udfTransferId = input.udfTransferId();

        MDC.put("requestId", udfTransferId.getId());

        LOGGER.info(
            "({}) Executing PutTransfersCommandHandler with input: [{}]", udfTransferId.getId(),
            input);

        Transfer transfer = null;
        Exception errorOccurred = null;

        FspCode payerFspCode = null;
        FspData payerFsp = null;
        FspCode payeeFspCode = null;
        FspData payeeFsp = null;

        FetchTransfer.Output fetchTransferOutput = null;
        UnwrapResponse.Output unwrapResponseOutput = null;

        try {

            payerFspCode = new FspCode(input.request().payer().fspCode());
            payerFsp = this.participantStore.getFspData(payerFspCode);
            LOGGER.debug("({}) Found payer FSP: [{}]", udfTransferId.getId(), payerFsp);

            payeeFspCode = new FspCode(input.request().payee().fspCode());
            payeeFsp = this.participantStore.getFspData(payeeFspCode);
            LOGGER.debug("({}) Found payee FSP: [{}]", udfTransferId.getId(), payeeFsp);

            var putTransfersResponse = input.transfersIDPutResponse();

            // 1. Fetch the transfer.
            fetchTransferOutput = this.fetchTransfer.execute(
                new FetchTransfer.Input(udfTransferId));
            transfer = fetchTransferOutput.transfer();

            if (transfer == null) {
                // Surely non-existence Transfer.
                LOGGER.warn(
                    "Transfer not found for udfTransferId : [{}]. Possible non-existence Transfer. Ignored it.",
                    udfTransferId.getId());
                return new Output();

            } else if (transfer.getState() == TransferState.COMMITTED ||
                           transfer.getState() == TransferState.ABORTED) {
                // This transfer has already been processed. Just forward back to Payer. It seems Payer is retrieving again.
                LOGGER.info(
                    "Transfer already processed for udfTransferId : [{}]. Ignored it.",
                    udfTransferId.getId());

                var stateFromRequest = putTransfersResponse.getTransferState();

                if (transfer.getState() == stateFromRequest) {

                    LOGGER.info(
                        "Payee responded with the same state as the one in the request. Forwarding back to Payer.");
                    this.respondTransferToPayer.execute(
                        new RespondTransferToPayer.Input(
                            CONTEXT, transfer.getTransactionId(), udfTransferId, payerFsp,
                            putTransfersResponse));

                } else {

                    LOGGER.warn(
                        "Payee responded with a different state than the one in the request. Ignoring it.");
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
                this.rollbackReservation.execute(
                    new RollbackReservation.Input(
                        CONTEXT, transfer.getTransactionId(), transfer.getReservationId(),
                        e.getMessage()));

                throw e;
            }

            // 3. Verify ILP. Do we need ?
            // ? ? ?

            // 4. Handle ABORTED state and RESERVED state.
            if (unwrapResponseOutput.state() == TransferState.ABORTED) {

                LOGGER.info(
                    "Payee responded with the aborted transfer : udfTransferId : [{}].",
                    udfTransferId.getId());

                this.rollbackReservation.execute(
                    new RollbackReservation.Input(
                        CONTEXT, transfer.getTransactionId(), transfer.getReservationId(),
                        "Payee aborted transfer."));

                this.abortTransfer.execute(
                    new AbortTransfer.Input(
                        CONTEXT, transfer.getTransactionId(), transfer.getId(),
                        transfer.getReservationId(), "Payee aborted transfer."));

                // Forward the Payee's response back to Payer.
                var payerBaseUrl = payerFsp.endpoints().get(EndpointType.TRANSFERS).baseUrl();
                LOGGER.info("Forwarding request to payer FSP (BaseUrl): [{}]", payerBaseUrl);

                this.forwardToDestination.execute(
                    new ForwardToDestination.Input(
                        CONTEXT, transfer.getTransactionId(), payeeFspCode.value(), payerBaseUrl,
                        input.request()));

                return new Output();

            } else if (unwrapResponseOutput.state() == TransferState.RESERVED) {

                LOGGER.info(
                    "Payee responded with the reserved transfer : udfTransferId : [{}].",
                    udfTransferId.getId());
                // Upon receiving the reserved transfer from Payee, Hub will need to commit the payer position
                // reservation, then give money to the Payee by decreasing its position. Then hub responds to the Payer
                // with the committed transfer.
                var commitReservationOutput = this.commitReservation.execute(
                    new CommitReservation.Input(
                        CONTEXT, transfer.getTransactionId(),
                        transfer.getReservationId()));

                var decreasePayeePositionOutput = this.decreasePayeePosition.execute(
                    new DecreasePayeePosition.Input(
                        CONTEXT, transfer.getTransactionId(), transfer.getTransactionAt(), payerFsp,
                        payeeFsp, transfer.getCurrency(), transfer.getTransferAmount()));

                var commitTransferOutput = this.commitTransfer.execute(new CommitTransfer.Input(
                    CONTEXT, transfer.getTransactionId(), transfer.getId(),
                    unwrapResponseOutput.ilpFulfilment(), commitReservationOutput.payerCommitId(),
                    decreasePayeePositionOutput.payeeCommitId(),
                    unwrapResponseOutput.completedAt()));

                var committedResponse = new TransfersIDPutResponse();

                committedResponse.setTransferState(TransferState.COMMITTED);
                committedResponse.setCompletedTimestamp(
                    putTransfersResponse.getCompletedTimestamp());
                committedResponse.setFulfilment(putTransfersResponse.getFulfilment());
                committedResponse.setExtensionList(putTransfersResponse.getExtensionList());

                this.respondTransferToPayer.execute(
                    new RespondTransferToPayer.Input(
                        CONTEXT, transfer.getTransactionId(), udfTransferId, payerFsp,
                        committedResponse));

            }

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            errorOccurred = e;

            try {

                if (transfer != null) {
                    // Inform back to the Payee only if Transfer exists.
                    this.patchTransferToPayee.execute(
                        new PatchTransferToPayee.Input(
                            CONTEXT, transfer.getTransactionId(), udfTransferId, payeeFsp,
                            TransferState.ABORTED, Map.of()));
                }

            } catch (Exception e1) {

                LOGGER.error("Error:", e1);

                errorOccurred = e1;
            }

        } finally {

            if (transfer != null && errorOccurred != null) {

                LOGGER.info("Closing transaction : udfTransferId : [{}]", udfTransferId.getId());

                this.closeTransactionPublisher.publish(
                    new CloseTransactionCommand.Input(
                        transfer.getTransactionId(),
                        errorOccurred.getMessage()));
            }
        }

        LOGGER.info("Returning from PutTransfersCommandHandler successfully.");

        MDC.remove("requestId");

        return new Output();
    }

}
