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

package org.mojave.core.transfer.domain.command.step.financial;

import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.common.datatype.enums.trasaction.StepPhase;
import org.mojave.core.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.core.transaction.contract.command.AddStepCommand;
import org.mojave.core.transaction.producer.publisher.AddStepPublisher;
import org.mojave.core.transfer.contract.command.step.financial.FulfilPositionsStep;
import org.mojave.core.wallet.contract.command.position.FulfilPositionsCommand;
import org.mojave.core.wallet.contract.exception.position.FailedToCommitReservationException;
import org.mojave.core.wallet.producer.publisher.FulfilPositionsPublisher;
import org.mojave.fspiop.component.error.FspiopErrors;
import org.mojave.fspiop.component.exception.FspiopException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FulfilPositionsStepHandler implements FulfilPositionsStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(FulfilPositionsStepHandler.class);

    private final FulfilPositionsCommand fulfilPositionsCommand;

    private final FulfilPositionsPublisher fulfilPositionsPublisher;

    private final AddStepPublisher addStepPublisher;

    public FulfilPositionsStepHandler(FulfilPositionsCommand fulfilPositionsCommand,
                                      FulfilPositionsPublisher fulfilPositionsPublisher,
                                      AddStepPublisher addStepPublisher) {

        assert fulfilPositionsCommand != null;
        assert fulfilPositionsPublisher != null;
        assert addStepPublisher != null;

        this.fulfilPositionsCommand = fulfilPositionsCommand;
        this.fulfilPositionsPublisher = fulfilPositionsPublisher;
        this.addStepPublisher = addStepPublisher;
    }

    @Override
    public FulfilPositionsStep.Output execute(FulfilPositionsStep.Input input)
        throws FailedToCommitReservationException, FspiopException {

        var startAt = System.nanoTime();

        LOGGER.info("FulfilPositionsStep : input : ({})", ObjectLogger.log(input));

        final var CONTEXT = input.context();
        final var STEP_NAME = "FulfilPositionsStep";

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

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    input.transactionId(), STEP_NAME, CONTEXT, e.getMessage(),
                    StepPhase.ERROR));

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, e.getMessage());
        }
    }

}
