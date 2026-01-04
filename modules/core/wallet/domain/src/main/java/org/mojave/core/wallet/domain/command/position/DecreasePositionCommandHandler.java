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
package org.mojave.core.wallet.domain.command.position;

import org.mojave.component.misc.handy.Snowflake;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.scheme.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.core.wallet.contract.command.position.DecreasePositionCommand;
import org.mojave.core.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import org.mojave.core.wallet.contract.exception.position.PositionNotExistException;
import org.mojave.core.wallet.domain.cache.PositionCache;
import org.mojave.core.wallet.domain.component.PositionUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DecreasePositionCommandHandler implements DecreasePositionCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        DecreasePositionCommandHandler.class);

    private final PositionUpdater positionUpdater;

    private final PositionCache positionCache;

    public DecreasePositionCommandHandler(final PositionUpdater positionUpdater,
                                          final PositionCache positionCache) {

        assert positionUpdater != null;
        assert positionCache != null;

        this.positionUpdater = positionUpdater;
        this.positionCache = positionCache;
    }

    @Override
    public Output execute(final Input input) throws NoPositionUpdateForTransactionException {

        LOGGER.info("DecreasePositionCommand : input: ({})", ObjectLogger.log(input));

        var position = this.positionCache.get(input.walletOwnerId(), input.currency());

        if (position == null) {

            throw new PositionNotExistException(input.walletOwnerId(), input.currency());
        }

        final var positionUpdateId = new PositionUpdateId(Snowflake.get().nextId());

        try {
            final var history = this.positionUpdater.decrease(
                input.transactionId(), input.transactionAt(), positionUpdateId,
                position.positionId(), input.amount(), input.description());

            final var output = new Output(
                history.positionUpdateId(), history.positionId(), history.action(),
                history.transactionId(), history.currency(), history.amount(),
                history.oldPosition(), history.newPosition(), history.oldReserved(),
                history.newReserved(), history.netDebitCap(), history.transactionAt());

            LOGGER.info("DecreasePositionCommand : output: ({})", ObjectLogger.log(output));

            return output;

        } catch (final PositionUpdater.NoPositionUpdateException e) {

            LOGGER.error("Error:", e);
            throw new NoPositionUpdateForTransactionException(input.transactionId());
        }
    }

}
