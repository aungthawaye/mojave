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

package org.mojave.rail.fspiop.transfer.domain.command.step.fspiop;

import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.rail.fspiop.transfer.contract.command.step.fspiop.ForwardToDestinationStep;
import org.mojave.rail.fspiop.component.error.FspiopErrors;
import org.mojave.rail.fspiop.component.exception.FspiopException;
import org.mojave.rail.fspiop.bootstrap.api.forwarder.ForwardRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import java.util.Objects;

@Service
public class ForwardToDestinationStepHandler implements ForwardToDestinationStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        ForwardToDestinationStepHandler.class);

    private final ForwardRequest forwardRequest;

    public ForwardToDestinationStepHandler(ForwardRequest forwardRequest) {

        Objects.requireNonNull(forwardRequest);

        this.forwardRequest = forwardRequest;
    }

    @Override
    public void execute(ForwardToDestinationStep.Input input) throws FspiopException {

        MDC.put("REQ_ID", input.udfTransferId().getId());

        var startAt = System.nanoTime();

        LOGGER.info("ForwardToDestinationStep : input : ({})", ObjectLogger.log(input));

        try {

            this.forwardRequest.forward(input.baseUrl(), input.request());

            var endAt = System.nanoTime();
            LOGGER.info(
                "ForwardToDestinationStep : done , took {} ms", (endAt - startAt) / 1_000_000);

        } catch (FspiopException e) {

            LOGGER.error("Error:", e);

            throw e;

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, e.getMessage());

        } finally {
            MDC.remove("REQ_ID");
        }
    }

}
