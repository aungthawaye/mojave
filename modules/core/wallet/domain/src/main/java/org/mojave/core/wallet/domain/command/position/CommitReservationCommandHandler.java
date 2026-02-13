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

import org.mojave.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.component.misc.handy.Snowflake;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.wallet.contract.command.position.CommitReservationCommand;
import org.mojave.core.wallet.contract.exception.position.FailedToCommitReservationException;
import org.mojave.core.wallet.domain.component.PositionUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CommitReservationCommandHandler implements CommitReservationCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        CommitReservationCommandHandler.class);

    private final PositionUpdater positionUpdater;

    public CommitReservationCommandHandler(final PositionUpdater positionUpdater) {

        Objects.requireNonNull(positionUpdater);
        this.positionUpdater = positionUpdater;
    }

    @Override
    public Output execute(final Input input) throws FailedToCommitReservationException {

        LOGGER.info("CommitReservationCommand : input: ({})", ObjectLogger.log(input));

        try {

            final var committed = this.positionUpdater.commit(
                input.reservationId(), new PositionUpdateId(Snowflake.get().nextId()));

            final var output = new Output(
                committed.positionUpdateId(), committed.positionId(), committed.action(),
                committed.transactionId(), committed.currency(), committed.amount(),
                committed.oldPosition(), committed.newPosition(), committed.oldReserved(),
                committed.newReserved(), committed.netDebitCap(), committed.transactionAt());

            LOGGER.info("CommitReservationCommand : output: ({})", ObjectLogger.log(output));

            return output;

        } catch (final PositionUpdater.CommitFailedException e) {

            LOGGER.error("Error:", e);

            throw new FailedToCommitReservationException(e.getReservationId());
        }
    }

}
