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

package io.mojaloop.core.transfer.domain.command.step.fspiop;

import io.mojaloop.component.misc.logger.ObjectLogger;
import io.mojaloop.core.common.datatype.enums.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.enums.trasaction.StepPhase;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.transfer.UdfTransferId;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.transaction.contract.command.AddStepCommand;
import io.mojaloop.core.transaction.producer.publisher.AddStepPublisher;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Payee;
import io.mojaloop.fspiop.component.handy.FspiopDates;
import io.mojaloop.fspiop.component.handy.FspiopUrls;
import io.mojaloop.fspiop.service.api.transfers.RespondTransfers;
import io.mojaloop.fspiop.spec.core.Extension;
import io.mojaloop.fspiop.spec.core.ExtensionList;
import io.mojaloop.fspiop.spec.core.TransferState;
import io.mojaloop.fspiop.spec.core.TransfersIDPatchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

@Service
public class PatchTransferToPayee {

    private static final Logger LOGGER = LoggerFactory.getLogger(PatchTransferToPayee.class);

    private final RespondTransfers respondTransfers;

    private final AddStepPublisher addStepPublisher;

    public PatchTransferToPayee(RespondTransfers respondTransfers,
                                AddStepPublisher addStepPublisher) {

        assert respondTransfers != null;
        assert addStepPublisher != null;

        this.respondTransfers = respondTransfers;
        this.addStepPublisher = addStepPublisher;
    }

    public Output execute(Input input) throws FspiopException {

        var startAt = System.nanoTime();

        LOGGER.info("PatchTransferToPayee : input : ({})", ObjectLogger.log(input));

        var CONTEXT = input.context;
        var STEP_NAME = "PatchTransferToPayee";

        try {

            var patchResponse = new TransfersIDPatchResponse(
                FspiopDates.forRequestBody(new Date()), input.state);

            var extensions = new ArrayList<Extension>();
            input.extensions.forEach((k, v) -> extensions.add(new Extension(k, v)));

            var extensionList = new ExtensionList(extensions);
            patchResponse.setExtensionList(extensionList);

            var payeeBaseUrl = input.payeeFsp.endpoints().get(EndpointType.TRANSFERS).baseUrl();
            var url = FspiopUrls.Transfers.patchTransfers(
                payeeBaseUrl, input.udfTransferId.getId());

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    input.transactionId, STEP_NAME, CONTEXT,
                    ObjectLogger.log(patchResponse).toString(), StepPhase.BEFORE));

            this.respondTransfers.patchTransfers(
                new Payee(input.payeeFsp.fspCode().value()), url, patchResponse);

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    input.transactionId, STEP_NAME, CONTEXT, "-", StepPhase.AFTER));

            var endAt = System.nanoTime();
            LOGGER.info("PatchTransferToPayee : done , took {} ms", (endAt - startAt) / 1_000_000);

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    input.transactionId, STEP_NAME, CONTEXT, e.getMessage(),
                    StepPhase.ERROR));

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
