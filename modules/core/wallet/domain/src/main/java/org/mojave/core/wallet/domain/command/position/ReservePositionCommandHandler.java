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

import org.mojave.common.datatype.identifier.wallet.WalletId;
import org.mojave.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.component.misc.handy.Snowflake;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.wallet.contract.command.position.ReservePositionCommand;
import org.mojave.core.wallet.contract.engine.WalletEngine;
import org.mojave.core.wallet.contract.exception.WalletNotFoundException;
import org.mojave.core.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import org.mojave.core.wallet.contract.exception.position.PositionLimitExceededException;
import org.mojave.core.wallet.domain.cache.WalletCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ReservePositionCommandHandler implements ReservePositionCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        ReservePositionCommandHandler.class);

    private final WalletEngine walletEngine;

    private final WalletCache walletCache;

    public ReservePositionCommandHandler(final WalletEngine walletEngine,
                                         final WalletCache walletCache) {

        Objects.requireNonNull(walletEngine);
        Objects.requireNonNull(walletCache);

        this.walletEngine = walletEngine;
        this.walletCache = walletCache;
    }

    @Override
    public Output execute(final Input input) throws
                                             PositionLimitExceededException,
                                             NoPositionUpdateForTransactionException,
                                             WalletNotFoundException {

        LOGGER.info("ReservePositionCommand : input: ({})", ObjectLogger.log(input));

        final var wallet = this.walletCache.get(
            input.walletOwnerId(), input.currency(), input.purpose());

        if (wallet == null) {
            throw new WalletNotFoundException(
                input.walletOwnerId(), input.currency(), input.purpose());
        }

        final var walletId = new WalletId(wallet.walletId().getId());
        final var positionUpdateId = new PositionUpdateId(Snowflake.get().nextId());
        LOGGER.info(
            "ReservePositionCommand : positionUpdateId: ({}), transactionId : ({})",
            positionUpdateId, input.transactionId());

        try {

            final var history = this.walletEngine.reservePosition(
                positionUpdateId, input.transactionId(), input.transactionAt(), walletId,
                input.amount(), input.description());

            final var output = new Output(
                history.positionUpdateId(), walletId, history.action(),
                history.transactionId(), history.currency(), history.amount(),
                history.oldPosition(), history.newPosition(), history.oldReserved(),
                history.newReserved(), history.netDebitCap(), history.transactionAt());

            LOGGER.info("ReservePositionCommand : output: ({})", ObjectLogger.log(output));

            return output;

        } catch (final WalletEngine.NoPositionUpdateException e) {
            throw new NoPositionUpdateForTransactionException(e.getTransactionId());

        } catch (final WalletEngine.PositionLimitExceededException e) {
            throw new PositionLimitExceededException(
                walletId, e.getAmount(),
                e.getOldPosition(), e.getOldReserved(), e.getNetDebitCap(), e.getTransactionId());
        }
    }

}
