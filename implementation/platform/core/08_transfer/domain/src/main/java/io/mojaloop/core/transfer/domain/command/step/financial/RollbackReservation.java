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
package io.mojaloop.core.transfer.domain.command.step.financial;

import io.mojaloop.core.common.datatype.enums.trasaction.StepPhase;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.core.transaction.contract.command.AddStepCommand;
import io.mojaloop.core.transaction.producer.publisher.AddStepPublisher;
import io.mojaloop.core.wallet.contract.command.position.RollbackReservationCommand;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RollbackReservation {

    private static final Logger LOGGER = LoggerFactory.getLogger(RollbackReservation.class);

    private final RollbackReservationCommand rollbackReservationCommand;

    private final AddStepPublisher addStepPublisher;

    public RollbackReservation(RollbackReservationCommand rollbackReservationCommand, AddStepPublisher addStepPublisher) {

        assert rollbackReservationCommand != null;
        assert addStepPublisher != null;

        this.rollbackReservationCommand = rollbackReservationCommand;
        this.addStepPublisher = addStepPublisher;
    }

    public Output execute(Input input) throws FspiopException {

        final var CONTEXT = input.context;
        final var STEP_NAME = "rollback-reservation";

        try {

            LOGGER.info("Roll back reservation  : transactionId : [{}], reservationId : [{}]", input.transactionId.getId(), input.positionReservationId.getId());

            var before = new HashMap<String, String>();
            var after = new HashMap<String, String>();

            before.put("transactionId", input.transactionId().getId().toString());
            before.put("positionReservationId", input.positionReservationId().getId().toString());

            this.addStepPublisher.publish(new AddStepCommand.Input(input.transactionId(), STEP_NAME, CONTEXT, before, StepPhase.BEFORE));

            var rollbackReservationOutput = this.rollbackReservationCommand.execute(
                new RollbackReservationCommand.Input(input.positionReservationId(), "Roll back due to : " + input.error()));

            after.put("rollbackId", rollbackReservationOutput.positionUpdateId().getId().toString());

            this.addStepPublisher.publish(new AddStepCommand.Input(input.transactionId(), STEP_NAME, CONTEXT, after, StepPhase.AFTER));

            var output = new Output(rollbackReservationOutput.positionUpdateId());

            LOGGER.info("Rolled back reservation successfully  : output : [{}]", output);

            return output;

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            this.addStepPublisher.publish(new AddStepCommand.Input(input.transactionId(), STEP_NAME, CONTEXT, Map.of("error", e.getMessage()), StepPhase.ERROR));

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, e.getMessage());
        }
    }

    public record Input(String context, TransactionId transactionId, PositionUpdateId positionReservationId, String error) { }

    public record Output(PositionUpdateId rollbackId) { }

}
