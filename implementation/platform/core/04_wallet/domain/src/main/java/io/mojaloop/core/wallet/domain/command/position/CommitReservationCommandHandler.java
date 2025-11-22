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
import io.mojaloop.core.wallet.contract.command.position.CommitReservationCommand;
import io.mojaloop.core.wallet.contract.exception.position.FailedToCommitReservationException;
import io.mojaloop.core.wallet.domain.component.PositionUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CommitReservationCommandHandler implements CommitReservationCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        CommitReservationCommandHandler.class);

    private final PositionUpdater positionUpdater;

    public CommitReservationCommandHandler(final PositionUpdater positionUpdater) {

        assert positionUpdater != null;
        this.positionUpdater = positionUpdater;
    }

    @Override
    public Output execute(final Input input) throws FailedToCommitReservationException {

        LOGGER.info("Executing CommitReservationCommand with input: {}", input);

        try {

            final var history = this.positionUpdater.commit(
                input.reservationId(), new PositionUpdateId(Snowflake.get().nextId()));

            final var output = new Output(
                history.positionUpdateId(), history.positionId(), history.action(),
                history.transactionId(), history.currency(), history.amount(),
                history.oldPosition(), history.newPosition(), history.oldReserved(),
                history.newReserved(), history.netDebitCap(), history.transactionAt());

            LOGGER.info("CommitReservationCommand executed successfully with output: {}", output);

            return output;

        } catch (final PositionUpdater.CommitFailedException e) {

            LOGGER.error("Commit failed for reservationId: {}", e.getReservationId());
            throw new FailedToCommitReservationException(e.getReservationId());
        }
    }

}
