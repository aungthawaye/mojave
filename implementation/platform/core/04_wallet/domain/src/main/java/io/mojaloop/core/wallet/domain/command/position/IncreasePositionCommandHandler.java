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
package io.mojaloop.core.wallet.domain.command.position;

import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.core.wallet.contract.command.position.IncreasePositionCommand;
import io.mojaloop.core.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import io.mojaloop.core.wallet.contract.exception.position.PositionLimitExceededException;
import io.mojaloop.core.wallet.domain.component.PositionUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class IncreasePositionCommandHandler implements IncreasePositionCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(IncreasePositionCommandHandler.class);

    private final PositionUpdater positionUpdater;

    public IncreasePositionCommandHandler(final PositionUpdater positionUpdater) {

        assert positionUpdater != null;
        this.positionUpdater = positionUpdater;
    }

    @Override
    public Output execute(final Input input) throws NoPositionUpdateForTransactionException, PositionLimitExceededException {

        LOGGER.info("Executing IncreasePositionCommand with input: {}", input);

        final var positionUpdateId = new PositionUpdateId(Snowflake.get().nextId());

        try {

            final var history = this.positionUpdater.increase(
                input.transactionId(), input.transactionAt(), positionUpdateId, input.positionId(), input.amount(),
                input.description());

            final var output = new Output(
                history.positionUpdateId(), history.positionId(), history.action(), history.transactionId(), history.currency(), history.amount(), history.oldPosition(),
                history.newPosition(), history.oldReserved(), history.newReserved(), history.netDebitCap(), history.transactionAt());

            LOGGER.info("IncreasePositionCommand executed successfully with output: {}", output);

            return output;

        } catch (final PositionUpdater.NoPositionUpdateException e) {

            LOGGER.error("No position update created for transaction: {}", input.transactionId());
            throw new NoPositionUpdateForTransactionException(e.getTransactionId());

        } catch (final PositionUpdater.LimitExceededException e) {

            LOGGER.error("Position limit exceeded for positionId: {} amount: {}", e.getPositionId(), e.getAmount());
            throw new PositionLimitExceededException(e.getPositionId(), e.getAmount(), e.getOldPosition(), e.getNetDebitCap(), e.getTransactionId());
        }
    }

}
