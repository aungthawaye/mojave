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
import org.mojave.core.transfer.contract.command.step.financial.RollbackReservationStep;
import org.mojave.core.wallet.contract.command.position.RollbackReservationCommand;
import org.mojave.core.wallet.producer.publisher.RollbackReservationPublisher;
import org.mojave.fspiop.component.error.FspiopErrors;
import org.mojave.fspiop.component.exception.FspiopException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RollbackReservationStepHandler implements RollbackReservationStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        RollbackReservationStepHandler.class);

    private final RollbackReservationPublisher rollbackReservationPublisher;

    public RollbackReservationStepHandler(RollbackReservationPublisher rollbackReservationPublisher) {

        assert rollbackReservationPublisher != null;

        this.rollbackReservationPublisher = rollbackReservationPublisher;
    }

    @Override
    public RollbackReservationStep.Output execute(RollbackReservationStep.Input input)
        throws FspiopException {

        var startAt = System.nanoTime();

        LOGGER.info("RollbackReservationStep : input : ({})", ObjectLogger.log(input));

        try {

            var rollbackReservationInput = new RollbackReservationCommand.Input(
                input.positionReservationId(), "Roll back due to : " + input.error());

            this.rollbackReservationPublisher.publish(rollbackReservationInput);

            var output = new RollbackReservationStep.Output(null);

            var endAt = System.nanoTime();
            LOGGER.info(
                "RollbackReservationStep : output : ({}) , took : {} ms", output,
                (endAt - startAt) / 1_000_000);

            return output;

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, e.getMessage());
        }
    }

}
