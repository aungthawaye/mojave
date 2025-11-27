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

import io.mojaloop.component.jpa.routing.annotation.Read;
import io.mojaloop.component.misc.logger.ObjectLogger;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.component.handy.FspiopDates;
import io.mojaloop.fspiop.spec.core.TransferState;
import io.mojaloop.fspiop.spec.core.TransfersIDPutResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class UnwrapResponse {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnwrapResponse.class);

    public UnwrapResponse() {

    }

    @Transactional
    @Read
    public Output execute(Input input) throws FspiopException {

        var startAt = System.nanoTime();
        LOGGER.info("UnwrapResponse : input : ({})", ObjectLogger.log(input));

        var response = input.response();
        var state = response.getTransferState();
        var completedTimestamp = response.getCompletedTimestamp();

        if (state != TransferState.RESERVED && state != TransferState.ABORTED) {

            LOGGER.info("Payee responded with invalid Transfer status : ({})", state);
            throw new FspiopException(
                FspiopErrors.GENERIC_VALIDATION_ERROR,
                "Payee responded with invalid Transfer status.");
        }

        var completedAt = Instant.now();

        try {
            completedAt = FspiopDates.fromRequestBody(completedTimestamp);
        } catch (Exception ignored) { }

        var output = new Output(state, response.getFulfilment(), completedAt);

        var endAt = System.nanoTime();
        LOGGER.info(
            "UnwrapResponse : output : ({}), took {} ms", ObjectLogger.log(output),
            (endAt - startAt) / 1_000_000);

        return output;
    }

    public record Input(TransfersIDPutResponse response) { }

    public record Output(TransferState state, String ilpFulfilment, Instant completedAt) { }

}
