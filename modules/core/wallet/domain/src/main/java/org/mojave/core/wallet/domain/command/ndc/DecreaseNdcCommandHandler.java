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

package org.mojave.core.wallet.domain.command.ndc;

import org.mojave.component.misc.handy.Snowflake;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.wallet.contract.command.ndc.DecreaseNdcCommand;
import org.mojave.core.wallet.contract.engine.WalletEngine;
import org.mojave.core.wallet.contract.exception.WalletNotFoundException;
import org.mojave.core.wallet.contract.exception.balance.NoBalanceUpdateForTransactionException;
import org.mojave.core.wallet.contract.exception.ndc.PositionReservedExceedsNdcException;
import org.mojave.core.wallet.domain.cache.WalletCache;
import org.mojave.scheme.rule.identifier.wallet.NdcUpdateId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class DecreaseNdcCommandHandler implements DecreaseNdcCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(DecreaseNdcCommandHandler.class);

    private final WalletEngine walletEngine;

    private final WalletCache walletCache;

    public DecreaseNdcCommandHandler(final WalletEngine walletEngine,
                                     final WalletCache walletCache) {

        Objects.requireNonNull(walletEngine);
        Objects.requireNonNull(walletCache);

        this.walletEngine = walletEngine;
        this.walletCache = walletCache;
    }

    @Override
    public Output execute(final Input input) throws
                                             NoBalanceUpdateForTransactionException,
                                             PositionReservedExceedsNdcException,
                                             WalletNotFoundException {

        LOGGER.info("DecreaseNdcCommand : input: ({})", ObjectLogger.log(input));

        final var wallet = this.walletCache.get(input.walletId());

        if (wallet == null) {
            throw new WalletNotFoundException(input.walletId());
        }

        final var walletId = wallet.walletId();
        final var ndcUpdateId = new NdcUpdateId(Snowflake.get().nextId());
        final var transactionId = input.transactionId();
        final var transactionAt = input.transactionAt();

        try {

            final var history = this.walletEngine.decreaseNdc(
                ndcUpdateId, transactionId, transactionAt, walletId, input.amount(),
                input.description());

            final var output = new Output(history.ndcUpdateId());

            LOGGER.info("DecreaseNdcCommand : output: ({})", ObjectLogger.log(output));

            return output;

        } catch (final WalletEngine.NoBalanceUpdateException e) {
            throw new NoBalanceUpdateForTransactionException(transactionId);

        } catch (final WalletEngine.PositionReservedExceedsNdcException e) {
            throw new PositionReservedExceedsNdcException(
                walletId, e.getAmount(), e.getPosition(),
                e.getReserved(), e.getNewNdc(), e.getTransactionId());
        }
    }

}
