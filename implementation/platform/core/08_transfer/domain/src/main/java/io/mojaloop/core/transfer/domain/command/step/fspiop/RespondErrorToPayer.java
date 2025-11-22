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

import io.mojaloop.core.common.datatype.enums.trasaction.StepPhase;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.transaction.contract.command.AddStepCommand;
import io.mojaloop.core.transaction.producer.publisher.AddStepPublisher;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Payer;
import io.mojaloop.fspiop.component.handy.FspiopErrorResponder;
import io.mojaloop.fspiop.service.api.transfers.RespondTransfers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RespondErrorToPayer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RespondErrorToPayer.class);

    private final RespondTransfers respondTransfers;

    private final AddStepPublisher addStepPublisher;

    public RespondErrorToPayer(RespondTransfers respondTransfers, AddStepPublisher addStepPublisher) {

        assert respondTransfers != null;
        assert addStepPublisher != null;

        this.respondTransfers = respondTransfers;
        this.addStepPublisher = addStepPublisher;
    }

    public void execute(Input input) throws FspiopException {

        final var CONTEXT = input.context;
        final var STEP_NAME = "respond-error-to-payer";

        try {

            var errors = new HashMap<String, String>();

            errors.put("error", input.exception.getMessage());
            errors.put("sendBackTo", input.sendBackTo.fspCode());
            errors.put("url", input.url);

            this.addStepPublisher.publish(new AddStepCommand.Input(input.transactionId, STEP_NAME, CONTEXT, errors, StepPhase.BEFORE));

            FspiopErrorResponder.toPayer(input.sendBackTo, input.exception, (payer, error) -> this.respondTransfers.putTransfersError(input.sendBackTo, input.url, error));

            this.addStepPublisher.publish(new AddStepCommand.Input(input.transactionId, STEP_NAME, CONTEXT, Map.of("-", "-"), StepPhase.AFTER));

        } catch (FspiopException e) {

            LOGGER.error("Error:", e);

            throw e;

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            this.addStepPublisher.publish(new AddStepCommand.Input(input.transactionId, STEP_NAME, CONTEXT, Map.of("error", e.getMessage()), StepPhase.ERROR));

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, e.getMessage());
        }

    }

    public record Input(String context, TransactionId transactionId, Payer sendBackTo, String url, Exception exception) {

    }

}
