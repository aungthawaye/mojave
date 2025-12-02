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
import io.mojaloop.core.wallet.contract.command.position.RollbackReservationCommand;
import io.mojaloop.core.wallet.contract.exception.position.FailedToRollbackReservationException;
import io.mojaloop.core.wallet.domain.component.PositionUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RollbackReservationCommandHandler implements RollbackReservationCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        RollbackReservationCommandHandler.class);

    private final PositionUpdater positionUpdater;

    public RollbackReservationCommandHandler(final PositionUpdater positionUpdater) {

        assert positionUpdater != null;
        this.positionUpdater = positionUpdater;
    }

    @Override
    public Output execute(final Input input) throws FailedToRollbackReservationException {

        LOGGER.info("RollbackReservationCommand : input: ({})", ObjectLogger.log(input));

        try {

            final var history = this.positionUpdater.rollback(
                input.reservationId(), new PositionUpdateId(Snowflake.get().nextId()));

            final var output = new Output(
                history.positionUpdateId(), history.positionId(), history.action(),
                history.transactionId(), history.currency(), history.amount(),
                history.oldPosition(), history.newPosition(), history.oldReserved(),
                history.newReserved(), history.netDebitCap(), history.transactionAt());

            LOGGER.info("RollbackReservationCommand : output: ({})", ObjectLogger.log(output));

            return output;

        } catch (final PositionUpdater.RollbackFailedException e) {
            throw new FailedToRollbackReservationException(e.getReservationId());
        }
    }

}
