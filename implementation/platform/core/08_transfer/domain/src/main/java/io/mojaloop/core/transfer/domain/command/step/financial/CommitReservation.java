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
import io.mojaloop.core.wallet.contract.command.position.CommitReservationCommand;
import io.mojaloop.core.wallet.contract.exception.position.FailedToCommitReservationException;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CommitReservation {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommitReservation.class);

    private final CommitReservationCommand commitReservationCommand;

    private final AddStepPublisher addStepPublisher;

    public CommitReservation(CommitReservationCommand commitReservationCommand,
                             AddStepPublisher addStepPublisher) {

        assert commitReservationCommand != null;
        assert addStepPublisher != null;

        this.commitReservationCommand = commitReservationCommand;
        this.addStepPublisher = addStepPublisher;
    }

    public Output execute(Input input) throws FailedToCommitReservationException, FspiopException {

        final var CONTEXT = input.context;
        final var STEP_NAME = "commit-reservation";

        try {

            LOGGER.info("Commit reservation  : input : [{}]", input);

            var before = new HashMap<String, String>();
            var after = new HashMap<String, String>();

            before.put("transactionId", input.transactionId().getId().toString());
            before.put("positionReservationId", input.positionReservationId().getId().toString());

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    input.transactionId(), STEP_NAME, CONTEXT, before,
                    StepPhase.BEFORE));

            var commitReservationOutput = this.commitReservationCommand.execute(
                new CommitReservationCommand.Input(
                    input.positionReservationId(),
                    "Commit Payer's position reservation "));

            after.put(
                "payerCommitId", commitReservationOutput.positionUpdateId().getId().toString());

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    input.transactionId(), STEP_NAME, CONTEXT, after,
                    StepPhase.AFTER));

            var output = new Output(commitReservationOutput.positionUpdateId());

            LOGGER.info("Committed reservation successfully  : output : [{}]", output);

            return output;

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    input.transactionId(), STEP_NAME, CONTEXT, Map.of("error", e.getMessage()),
                    StepPhase.ERROR));

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, e.getMessage());
        }
    }

    public record Input(String context,
                        TransactionId transactionId,
                        PositionUpdateId positionReservationId) { }

    public record Output(PositionUpdateId payerCommitId) { }

}
