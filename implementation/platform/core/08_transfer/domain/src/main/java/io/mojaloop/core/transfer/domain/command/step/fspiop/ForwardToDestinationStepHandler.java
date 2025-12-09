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
import io.mojaloop.core.common.datatype.enums.trasaction.StepPhase;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.transaction.contract.command.AddStepCommand;
import io.mojaloop.core.transaction.producer.publisher.AddStepPublisher;
import io.mojaloop.core.transfer.contract.command.step.fspiop.ForwardToDestinationStep;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.service.api.forwarder.ForwardRequest;
import io.mojaloop.fspiop.service.component.FspiopHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ForwardToDestinationStepHandler implements ForwardToDestinationStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        ForwardToDestinationStepHandler.class);

    private final ForwardRequest forwardRequest;

    private final AddStepPublisher addStepPublisher;

    public ForwardToDestinationStepHandler(ForwardRequest forwardRequest, AddStepPublisher addStepPublisher) {

        assert forwardRequest != null;
        assert addStepPublisher != null;

        this.forwardRequest = forwardRequest;
        this.addStepPublisher = addStepPublisher;
    }

    @Override
    public void execute(ForwardToDestinationStep.Input input) throws FspiopException {

        var startAt = System.nanoTime();

        LOGGER.info("ForwardToDestinationStep : input : ({})", ObjectLogger.log(input));

        final var CONTEXT = input.context();
        final var STEP_NAME = "ForwardToDestinationStep";

        try {

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    input.transactionId(), STEP_NAME, CONTEXT, "-", StepPhase.BEFORE));

            this.forwardRequest.forward(input.baseUrl(), input.request());

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    input.transactionId(), STEP_NAME, CONTEXT, "-", StepPhase.AFTER));

            var endAt = System.nanoTime();
            LOGGER.info("ForwardToDestinationStep : done , took {} ms", (endAt - startAt) / 1_000_000);

        } catch (FspiopException e) {

            LOGGER.error("Error:", e);

            throw e;

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    input.transactionId(), STEP_NAME, CONTEXT, e.getMessage(),
                    StepPhase.ERROR));

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, e.getMessage());
        }
    }

    

}
