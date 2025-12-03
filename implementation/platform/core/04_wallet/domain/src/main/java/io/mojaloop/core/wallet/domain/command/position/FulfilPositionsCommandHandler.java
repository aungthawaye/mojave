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
import io.mojaloop.component.misc.logger.ObjectLogger;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.core.wallet.contract.command.position.FulfilPositionsCommand;
import io.mojaloop.core.wallet.contract.exception.position.FailedToFulfilPositionsException;
import io.mojaloop.core.wallet.contract.exception.position.PositionNotExistException;
import io.mojaloop.core.wallet.domain.cache.PositionCache;
import io.mojaloop.core.wallet.domain.component.PositionUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FulfilPositionsCommandHandler implements FulfilPositionsCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        FulfilPositionsCommandHandler.class);

    private final PositionUpdater positionUpdater;

    private final PositionCache positionCache;

    public FulfilPositionsCommandHandler(final PositionUpdater positionUpdater,
                                         final PositionCache positionCache) {

        assert positionUpdater != null;
        assert positionCache != null;

        this.positionUpdater = positionUpdater;
        this.positionCache = positionCache;
    }

    @Override
    public Output execute(Input input)
        throws PositionNotExistException, FailedToFulfilPositionsException {

        LOGGER.info("FulfilPositionsCommand : input: ({})", ObjectLogger.log(input));

        var payeePosition = this.positionCache.get(input.payeeWalletOwnerId(), input.currency());

        if (payeePosition == null) {

            throw new PositionNotExistException(input.payeeWalletOwnerId(), input.currency());
        }

        final var reservationCommitId = new PositionUpdateId(Snowflake.get().nextId());
        final var payeePositionCommitId = new PositionUpdateId(Snowflake.get().nextId());

        try {
            final var result = this.positionUpdater.fulfil(
                input.reservationId(), reservationCommitId, payeePositionCommitId,
                payeePosition.positionId(), input.description());

            final var output = new FulfilPositionsCommand.Output(
                result.payerCommitmentId(), result.payeeCommitmentId());

            LOGGER.info("FulfilPositionsCommand : output: ({})", ObjectLogger.log(output));

            return output;

        } catch (final PositionUpdater.NoPositionFulfilmentException e) {

            LOGGER.error("Error:", e);
            throw new FailedToFulfilPositionsException(e.getReservationId());
        }
    }

}
