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

import org.mojave.common.datatype.enums.participant.EndpointType;
import org.mojave.common.datatype.enums.transfer.DisputeReason;
import org.mojave.common.datatype.type.participant.FspCode;
import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.participant.contract.data.FspData;
import org.mojave.core.participant.store.ParticipantStore;
import org.mojave.rail.fspiop.service.api.transfers.RespondTransfers;
import org.mojave.rail.fspiop.component.error.FspiopErrors;
import org.mojave.rail.fspiop.component.handy.FspiopErrorResponder;
import org.mojave.rail.fspiop.component.handy.FspiopUrls;
import org.mojave.rail.fspiop.component.type.Payer;
import org.mojave.rail.fspiop.transfer.contract.command.PatchTransfersErrorCommand;
import org.mojave.rail.fspiop.transfer.contract.command.step.stateful.DisputeTransferStep;
import org.mojave.rail.fspiop.transfer.contract.command.step.stateful.FetchTransferStep;
import org.mojave.rail.fspiop.spec.ErrorInformation;
import org.mojave.rail.fspiop.spec.ErrorInformationObject;
import org.mojave.rail.fspiop.transfer.domain.async.producer.DisputeTransferStepProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class PatchTransfersErrorCommandHandler implements PatchTransfersErrorCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        PatchTransfersErrorCommandHandler.class);

    private final ParticipantStore participantStore;

    // Stateful steps
    private final FetchTransferStep fetchTransferStep;

    private final DisputeTransferStepProducer disputeTransferStepProducer;

    // FSPIOP steps
    private final RespondTransfers respondTransfers;

    public PatchTransfersErrorCommandHandler(ParticipantStore participantStore,
                                             FetchTransferStep fetchTransferStep,
                                             DisputeTransferStepProducer disputeTransferStepProducer,
                                             RespondTransfers respondTransfers) {

        Objects.requireNonNull(participantStore);
        Objects.requireNonNull(fetchTransferStep);
        Objects.requireNonNull(disputeTransferStepProducer);
        Objects.requireNonNull(respondTransfers);

        this.participantStore = participantStore;
        this.fetchTransferStep = fetchTransferStep;
        this.disputeTransferStepProducer = disputeTransferStepProducer;
        this.respondTransfers = respondTransfers;
    }

    @Override
    @Write
    public Output execute(Input input) {

        LOGGER.info("PatchTransfersErrorCommandHandler : input: ({})", ObjectLogger.log(input));

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

            if (transferId == null) {

                // Surely non-existence Transfer.
                LOGGER.warn(
                    "Transfer not found for udfTransferId : ({}). Possible non-existence Transfer. Ignored it.",
                    udfTransferId.getId());

                return new Output();

            }

            this.disputeTransferStepProducer.publish(
                new DisputeTransferStep.Input(
                    udfTransferId, transactionId, transferId, DisputeReason.PATCHING_TO_PAYEE));
            LOGGER.info("DisputeTransferStep published for udfTransferId : ({})", udfTransferId);

            var errorInformationObject = new ErrorInformationObject().errorInformation(
                new ErrorInformation(
                    FspiopErrors.GENERIC_PAYEE_ERROR.errorType().getCode(),
                    DisputeReason.PATCHING_TO_PAYEE.getDescription()));

            final var sendBackTo = new Payer(payerFspCode.value());
            final var baseUrl = payerFsp.endpoints().get(EndpointType.TRANSFERS).baseUrl();
            final var url = FspiopUrls.Transfers.patchTransfersError(
                baseUrl, udfTransferId.getId());
            LOGGER.info("Responding PATCH error to payer FSP (url): ({})", url);

            try {

                this.respondTransfers.patchTransfersError(sendBackTo, url, errorInformationObject);

                LOGGER.info("Done responding the dispute Transfer to payer FSP (url): ({})", url);

            } catch (Exception e) {
                LOGGER.error(
                    "Problem occurred while responding the dispute Transfer to payer FSP (url): ({}) (ignored):",
                    url, e);
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
                        (payer, error) -> this.respondTransfers.patchTransfersError(
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
