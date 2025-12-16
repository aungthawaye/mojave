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
import org.mojave.fspiop.component.type.Payer;
import org.mojave.fspiop.component.handy.FspiopUrls;
import org.mojave.fspiop.service.api.transfers.RespondTransfers;
import org.mojave.fspiop.spec.core.ExtensionList;
import org.mojave.fspiop.spec.core.TransferState;
import org.mojave.fspiop.spec.core.TransfersIDPutResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CommitTransferToPayerStepHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        CommitTransferToPayerStepHandler.class);

    private final RespondTransfers respondTransfers;

    private final AddStepPublisher addStepPublisher;

    public CommitTransferToPayerStepHandler(RespondTransfers respondTransfers,
                                            AddStepPublisher addStepPublisher) {

        assert respondTransfers != null;
        assert addStepPublisher != null;

        this.respondTransfers = respondTransfers;
        this.addStepPublisher = addStepPublisher;
    }

    public void execute(Input input) throws FspiopException {

        var startAt = System.nanoTime();

        LOGGER.info("CommitTransferToPayerStep : input : ({})", ObjectLogger.log(input));

        final var CONTEXT = input.context;
        final var STEP_NAME = "CommitTransferToPayerStep";

        var payerFsp = input.payerFsp;

        try {

            var response = new TransfersIDPutResponse()
                               .transferState(TransferState.COMMITTED)
                               .fulfilment(input.ilpFulfilment)
                               .completedTimestamp(input.completedTimestamp)
                               .extensionList(input.extensionList);

            this.addStepPublisher.publish(new AddStepCommand.Input(
                input.transactionId, STEP_NAME, CONTEXT, ObjectLogger.log(response).toString(),
                StepPhase.BEFORE));

            var sendBackTo = new Payer(input.payerFsp.code().value());
            var payerBaseUrl = payerFsp.endpoints().get(EndpointType.TRANSFERS).baseUrl();
            var url = FspiopUrls.Transfers.putTransfers(
                payerBaseUrl, input.udfTransferId().getId());

            this.respondTransfers.putTransfers(sendBackTo, url, response);

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    input.transactionId, STEP_NAME, CONTEXT, "-",
                    StepPhase.AFTER));

            LOGGER.info(
                "CommitTransferToPayerStep : done , took {} ms",
                (System.nanoTime() - startAt) / 1_000_000);

        } catch (FspiopException e) {

            LOGGER.error("Error:", e);

            throw e;

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    input.transactionId, STEP_NAME, CONTEXT, "-",
                    StepPhase.ERROR));

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, e.getMessage());
        }

    }

    public record Input(String context,
                        TransactionId transactionId,
                        UdfTransferId udfTransferId,
                        FspData payerFsp,
                        String ilpFulfilment,
                        String completedTimestamp,
                        ExtensionList extensionList) {

    }

}
