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

import io.mojaloop.component.misc.logger.ObjectLogger;
import io.mojaloop.core.common.datatype.enums.trasaction.StepPhase;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.transaction.contract.command.AddStepCommand;
import io.mojaloop.core.transaction.producer.publisher.AddStepPublisher;
import io.mojaloop.core.wallet.contract.command.position.FulfilPositionsCommand;
import io.mojaloop.core.wallet.contract.exception.position.FailedToCommitReservationException;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.spec.core.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class FulfilPositions {

    private static final Logger LOGGER = LoggerFactory.getLogger(FulfilPositions.class);

    private final FulfilPositionsCommand fulfilPositionsCommand;

    private final AddStepPublisher addStepPublisher;

    public FulfilPositions(FulfilPositionsCommand fulfilPositionsCommand,
                           AddStepPublisher addStepPublisher) {

        assert fulfilPositionsCommand != null;
        assert addStepPublisher != null;

        this.fulfilPositionsCommand = fulfilPositionsCommand;
        this.addStepPublisher = addStepPublisher;
    }

    public FulfilPositions.Output execute(FulfilPositions.Input input)
        throws FailedToCommitReservationException, FspiopException {

        var startAt = System.nanoTime();

        LOGGER.info("FulfilPositions : input : ({})", ObjectLogger.log(input));

        final var CONTEXT = input.context();
        final var STEP_NAME = "FulfilPositions";

        try {

            var fulfilPositionsInput = new FulfilPositionsCommand.Input(
                input.positionReservationId(), new WalletOwnerId(input.payeeFsp().fspId().getId()),
                input.currency(), input.description());

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    input.transactionId(), STEP_NAME, CONTEXT,
                    ObjectLogger.log(fulfilPositionsInput).toString(), StepPhase.BEFORE));

            var fulfilPositionsOutput = this.fulfilPositionsCommand.execute(fulfilPositionsInput);

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    input.transactionId(), STEP_NAME, CONTEXT,
                    ObjectLogger.log(fulfilPositionsOutput).toString(), StepPhase.AFTER));

            var output = new FulfilPositions.Output(
                fulfilPositionsOutput.payerCommitId(),
                fulfilPositionsOutput.payeeCommitId());

            var endAt = System.nanoTime();
            LOGGER.info(
                "FulfilPositions : output : ({}) , took : {} ms", ObjectLogger.log(output),
                (endAt - startAt) / 1_000_000);

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

    public record Input(String context,
                        TransactionId transactionId,
                        Instant transactionAt,
                        FspData payerFsp,
                        FspData payeeFsp,
                        PositionUpdateId positionReservationId,
                        Currency currency,
                        String description) { }

    public record Output(PositionUpdateId payerCommitId, PositionUpdateId payeeCommitId) { }

}
