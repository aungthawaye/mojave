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
import io.mojaloop.core.wallet.contract.command.position.ReservePositionCommand;
import io.mojaloop.core.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import io.mojaloop.core.wallet.contract.exception.position.PositionLimitExceededException;
import io.mojaloop.core.wallet.contract.exception.position.PositionNotExistException;
import io.mojaloop.core.wallet.domain.cache.PositionCache;
import io.mojaloop.core.wallet.domain.component.PositionUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ReservePositionCommandHandler implements ReservePositionCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        ReservePositionCommandHandler.class);

    private final PositionUpdater positionUpdater;

    private final PositionCache positionCache;

    public ReservePositionCommandHandler(final PositionUpdater positionUpdater,
                                         final PositionCache positionCache) {

        assert positionUpdater != null;
        assert positionCache != null;

        this.positionUpdater = positionUpdater;
        this.positionCache = positionCache;
    }

    @Override
    public Output execute(final Input input) throws
                                             PositionLimitExceededException,
                                             NoPositionUpdateForTransactionException,
                                             PositionNotExistException {

        LOGGER.info("Executing ReservePositionCommand with input: {}", input);

        var position = this.positionCache.get(input.walletOwnerId(), input.currency());

        if (position == null) {

            LOGGER.error(
                "Position does not exist for walletOwnerId: {} and currency: {}",
                input.walletOwnerId(), input.currency());
            throw new PositionNotExistException(input.walletOwnerId(), input.currency());
        }

        final var positionUpdateId = new PositionUpdateId(Snowflake.get().nextId());

        try {

            final var history = this.positionUpdater.reserve(
                input.transactionId(), input.transactionAt(), positionUpdateId,
                position.positionId(), input.amount(), input.description());

            final var output = new Output(
                history.positionUpdateId(), history.positionId(), history.action(),
                history.transactionId(), history.currency(), history.amount(),
                history.oldPosition(), history.newPosition(), history.oldReserved(),
                history.newReserved(), history.netDebitCap(), history.transactionAt());

            LOGGER.info("ReservePositionCommand executed successfully with output: {}", output);

            return output;

        } catch (final PositionUpdater.NoPositionUpdateException e) {

            LOGGER.error("No position update created for transaction: {}", input.transactionId());
            throw new NoPositionUpdateForTransactionException(e.getTransactionId());

        } catch (final PositionUpdater.LimitExceededException e) {

            LOGGER.error(
                "Position reservation exceeds limit for positionId: {} amount: {}",
                e.getPositionId(), e.getAmount());
            throw new PositionLimitExceededException(
                e.getPositionId(), e.getAmount(), e.getOldPosition(), e.getOldReserved(),
                e.getNetDebitCap(), e.getTransactionId());
        }
    }

}
