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

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.common.datatype.enums.Direction;
import org.mojave.core.common.datatype.enums.participant.EndpointType;
import org.mojave.core.common.datatype.enums.transfer.AbortReason;
import org.mojave.core.common.datatype.type.participant.FspCode;
import org.mojave.core.participant.contract.data.FspData;
import org.mojave.core.participant.store.ParticipantStore;
import org.mojave.rail.fspiop.transfer.contract.command.PutTransfersErrorCommand;
import org.mojave.rail.fspiop.transfer.contract.command.step.financial.RollbackReservationStep;
import org.mojave.rail.fspiop.transfer.contract.command.step.fspiop.ForwardToDestinationStep;
import org.mojave.rail.fspiop.transfer.contract.command.step.stateful.AbortTransferStep;
import org.mojave.rail.fspiop.transfer.contract.command.step.stateful.FetchTransferStep;
import org.mojave.rail.fspiop.transfer.domain.kafka.publisher.AbortTransferStepPublisher;
import org.mojave.rail.fspiop.transfer.domain.kafka.publisher.RollbackReservationStepPublisher;
import org.mojave.rail.fspiop.component.handy.FspiopErrorResponder;
import org.mojave.rail.fspiop.component.handy.FspiopUrls;
import org.mojave.rail.fspiop.component.type.Payer;
import org.mojave.rail.fspiop.bootstrap.api.transfers.RespondTransfers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PutTransfersErrorCommandHandler implements PutTransfersErrorCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        PutTransfersErrorCommandHandler.class);

    private final ParticipantStore participantStore;

    // Financial
    private final RollbackReservationStepPublisher rollbackReservationStepPublisher;

    // Stateful steps
    private final FetchTransferStep fetchTransferStep;

    private final AbortTransferStepPublisher abortTransferStepPublisher;

    // FSPIOP steps
    private final ForwardToDestinationStep forwardToDestinationStep;

    private final RespondTransfers respondTransfers;

    public PutTransfersErrorCommandHandler(ParticipantStore participantStore,
                                           RollbackReservationStepPublisher rollbackReservationStepPublisher,
                                           FetchTransferStep fetchTransferStep,
                                           AbortTransferStepPublisher abortTransferStepPublisher,
                                           ForwardToDestinationStep forwardToDestinationStep,
                                           RespondTransfers respondTransfers) {

        assert participantStore != null;
        assert rollbackReservationStepPublisher != null;
        assert fetchTransferStep != null;
        assert abortTransferStepPublisher != null;
        assert forwardToDestinationStep != null;
        assert respondTransfers != null;

        this.participantStore = participantStore;
        this.rollbackReservationStepPublisher = rollbackReservationStepPublisher;
        this.fetchTransferStep = fetchTransferStep;
        this.abortTransferStepPublisher = abortTransferStepPublisher;
        this.forwardToDestinationStep = forwardToDestinationStep;
        this.respondTransfers = respondTransfers;
    }

    @Override
    @Write
    public Output execute(Input input) {

        LOGGER.info("PutTransfersCommandHandler : input: ({})", ObjectLogger.log(input));

        var udfTransferId = input.udfTransferId();

        FspCode payerFspCode = null;
        FspData payerFsp = null;
        FspCode payeeFspCode = null;
        FspData payeeFsp = null;

        try {

            payerFspCode = new FspCode(input.request().payer().fspCode());
            payerFsp = this.participantStore.getFspData(payerFspCode);

            payeeFspCode = new FspCode(input.request().payee().fspCode());
            payeeFsp = this.participantStore.getFspData(payeeFspCode);

            var fetchTransferOutput = this.fetchTransferStep.execute(
                new FetchTransferStep.Input(udfTransferId, payerFsp.fspId(), payeeFsp.fspId()));

            var transferId = fetchTransferOutput.transferId();
            var transactionId = fetchTransferOutput.transactionId();
            var reservationId = fetchTransferOutput.reservationId();

            if (transferId == null) {

                // Surely non-existence Transfer.
                LOGGER.warn(
                    "Transfer not found for udfTransferId : ({}). Possible non-existence Transfer. Ignored it.",
                    udfTransferId.getId());

                return new Output();

            }

            this.rollbackReservationStepPublisher.publish(new RollbackReservationStep.Input(
                udfTransferId, transactionId, transferId, reservationId,
                "Payee responded with error."));

            this.abortTransferStepPublisher.publish(new AbortTransferStep.Input(
                udfTransferId, transactionId, transferId, AbortReason.PAYEE_RESPONDED_WITH_ERROR,
                Direction.FROM_PAYEE, input.error().getErrorInformation().getExtensionList()));

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
