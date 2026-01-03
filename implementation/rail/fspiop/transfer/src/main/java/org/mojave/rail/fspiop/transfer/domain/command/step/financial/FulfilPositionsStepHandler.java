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

package org.mojave.rail.fspiop.transfer.domain.command.step.financial;

import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.rail.fspiop.transfer.contract.command.step.financial.FulfilPositionsStep;
import org.mojave.core.wallet.contract.command.position.FulfilPositionsCommand;
import org.mojave.core.wallet.contract.exception.position.FailedToCommitReservationException;
import org.mojave.core.wallet.producer.publisher.FulfilPositionsPublisher;
import org.mojave.rail.fspiop.component.error.FspiopErrors;
import org.mojave.rail.fspiop.component.exception.FspiopException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
public class FulfilPositionsStepHandler implements FulfilPositionsStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(FulfilPositionsStepHandler.class);

    private final FulfilPositionsPublisher fulfilPositionsPublisher;

    public FulfilPositionsStepHandler(FulfilPositionsPublisher fulfilPositionsPublisher) {

        assert fulfilPositionsPublisher != null;

        this.fulfilPositionsPublisher = fulfilPositionsPublisher;
    }

    @Override
    public FulfilPositionsStep.Output execute(FulfilPositionsStep.Input input)
        throws FailedToCommitReservationException, FspiopException {

        MDC.put("REQ_ID", input.udfTransferId().getId());
        var startAt = System.nanoTime();

        LOGGER.info("FulfilPositionsStep : input : ({})", ObjectLogger.log(input));

        try {

            var fulfilPositionsInput = new FulfilPositionsCommand.Input(
                input.positionReservationId(), new WalletOwnerId(input.payeeFsp().fspId().getId()),
                input.currency(), input.description());

            this.fulfilPositionsPublisher.publish(fulfilPositionsInput);

            var output = new FulfilPositionsStepHandler.Output(null, null);

            var endAt = System.nanoTime();
            LOGGER.info(
                "FulfilPositionsStep : output : ({}) , took : {} ms",
                ObjectLogger.log(output), (endAt - startAt) / 1_000_000);

            return output;

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, e.getMessage());

        } finally {
            MDC.remove("REQ_ID");
        }
    }

}
