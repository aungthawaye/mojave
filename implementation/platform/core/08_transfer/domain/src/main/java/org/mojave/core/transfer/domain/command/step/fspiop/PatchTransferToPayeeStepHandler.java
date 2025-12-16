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

package org.mojave.core.transfer.domain.command.step.fspiop;

import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.common.datatype.enums.fspiop.EndpointType;
import org.mojave.core.common.datatype.enums.trasaction.StepPhase;
import org.mojave.core.common.datatype.identifier.transaction.TransactionId;
import org.mojave.core.common.datatype.identifier.transfer.UdfTransferId;
import org.mojave.core.participant.contract.data.FspData;
import org.mojave.core.transaction.contract.command.AddStepCommand;
import org.mojave.core.transaction.producer.publisher.AddStepPublisher;
import org.mojave.fspiop.component.error.FspiopErrors;
import org.mojave.fspiop.component.exception.FspiopException;
import org.mojave.fspiop.component.handy.FspiopDates;
import org.mojave.fspiop.component.handy.FspiopUrls;
import org.mojave.fspiop.component.type.Payee;
import org.mojave.fspiop.service.api.transfers.RespondTransfers;
import org.mojave.fspiop.spec.core.Extension;
import org.mojave.fspiop.spec.core.ExtensionList;
import org.mojave.fspiop.spec.core.TransferState;
import org.mojave.fspiop.spec.core.TransfersIDPatchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

@Service
public class PatchTransferToPayeeStepHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        PatchTransferToPayeeStepHandler.class);

    private final RespondTransfers respondTransfers;

    private final AddStepPublisher addStepPublisher;

    public PatchTransferToPayeeStepHandler(RespondTransfers respondTransfers,
                                           AddStepPublisher addStepPublisher) {

        assert respondTransfers != null;
        assert addStepPublisher != null;

        this.respondTransfers = respondTransfers;
        this.addStepPublisher = addStepPublisher;
    }

    public Output execute(Input input) throws FspiopException {

        var startAt = System.nanoTime();

        LOGGER.info("PatchTransferToPayeeStep : input : ({})", ObjectLogger.log(input));

        var CONTEXT = input.context;
        var STEP_NAME = "PatchTransferToPayeeStep";

        try {

            var patchResponse = new TransfersIDPatchResponse(
                FspiopDates.forRequestBody(new Date()), input.state);

            var extensions = new ArrayList<Extension>();
            input.extensions.forEach((k, v) -> extensions.add(new Extension(k, v)));

            var extensionList = new ExtensionList(extensions);

            if (!extensionList.getExtension().isEmpty()) {
                patchResponse.setExtensionList(extensionList);
            }

            var payeeBaseUrl = input.payeeFsp.endpoints().get(EndpointType.TRANSFERS).baseUrl();
            var url = FspiopUrls.Transfers.patchTransfers(
                payeeBaseUrl, input.udfTransferId.getId());

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    input.transactionId, STEP_NAME, CONTEXT,
                    ObjectLogger.log(patchResponse).toString(), StepPhase.BEFORE));

            this.respondTransfers.patchTransfers(
                new Payee(input.payeeFsp.code().value()), url, patchResponse);

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    input.transactionId, STEP_NAME, CONTEXT, "-",
                    StepPhase.AFTER));

            var endAt = System.nanoTime();
            LOGGER.info(
                "PatchTransferToPayeeStep : done , took {} ms", (endAt - startAt) / 1_000_000);

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    input.transactionId, STEP_NAME, CONTEXT, e.getMessage(), StepPhase.ERROR));

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, e.getMessage());
        }

        return new Output();
    }

    public record Input(String context,
                        TransactionId transactionId,
                        UdfTransferId udfTransferId,
                        FspData payeeFsp,
                        TransferState state,
                        Map<String, String> extensions) { }

    public record Output() { }

}
