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

import org.mojave.component.jpa.routing.annotation.Read;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.rail.fspiop.transfer.contract.command.step.fspiop.UnwrapResponseStep;
import org.mojave.rail.fspiop.component.error.FspiopErrors;
import org.mojave.rail.fspiop.component.exception.FspiopException;
import org.mojave.rail.fspiop.component.handy.FspiopDates;
import org.mojave.scheme.fspiop.core.TransferState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class UnwrapResponseStepHandler implements UnwrapResponseStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnwrapResponseStepHandler.class);

    public UnwrapResponseStepHandler() { }

    @Transactional
    @Read
    @Override
    public UnwrapResponseStep.Output execute(UnwrapResponseStep.Input input)
        throws FspiopException {

        MDC.put("REQ_ID", input.udfTransferId().getId());

        var startAt = System.nanoTime();
        LOGGER.info("UnwrapResponseStep : input : ({})", ObjectLogger.log(input));

        var response = input.response();
        var state = response.getTransferState();
        var completedTimestamp = response.getCompletedTimestamp();

        if (state == TransferState.RECEIVED) {

            LOGGER.info("Payee responded with invalid Transfer status : ({})", state);
            throw new FspiopException(
                FspiopErrors.GENERIC_VALIDATION_ERROR,
                "Payee responded with invalid Transfer status (RECEIVED). Must be one of: RESERVED, ABORTED or COMMITTED.");
        }

        var completedAt = Instant.now();

        try {
            completedAt = FspiopDates.fromRequestBody(completedTimestamp);
        } catch (Exception ignored) { }

        var output = new UnwrapResponseStep.Output(state, response.getFulfilment(), completedAt);

        var endAt = System.nanoTime();
        LOGGER.info(
            "UnwrapResponseStep : output : ({}), took {} ms", ObjectLogger.log(output),
            (endAt - startAt) / 1_000_000);

        MDC.remove("REQ_ID");

        return output;
    }

}
