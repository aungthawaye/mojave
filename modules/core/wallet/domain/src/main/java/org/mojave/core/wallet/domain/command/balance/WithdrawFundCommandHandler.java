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
package org.mojave.core.wallet.domain.command.balance;

import org.mojave.component.misc.handy.Snowflake;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.scheme.common.datatype.identifier.wallet.BalanceUpdateId;
import org.mojave.core.wallet.contract.command.balance.WithdrawFundCommand;
import org.mojave.core.wallet.contract.exception.balance.InsufficientBalanceException;
import org.mojave.core.wallet.contract.exception.balance.NoBalanceUpdateForTransactionException;
import org.mojave.core.wallet.domain.cache.BalanceCache;
import org.mojave.core.wallet.domain.component.BalanceUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WithdrawFundCommandHandler implements WithdrawFundCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(WithdrawFundCommandHandler.class);

    private final BalanceUpdater balanceUpdater;

    private final BalanceCache balanceCache;

    public WithdrawFundCommandHandler(BalanceUpdater balanceUpdater, BalanceCache balanceCache) {

        assert balanceUpdater != null;
        assert balanceCache != null;

        this.balanceUpdater = balanceUpdater;
        this.balanceCache = balanceCache;
    }

    @Override
    public Output execute(final Input input)
        throws NoBalanceUpdateForTransactionException, InsufficientBalanceException {

        LOGGER.info("WithdrawFundCommand : input: ({})", ObjectLogger.log(input));

        var wallet = this.balanceCache.get(input.walletOwnerId(), input.currency());

        if (wallet == null) {
            throw new RuntimeException(
                "Balance does not exist for walletOwnerId: " + input.walletOwnerId() +
                    " and currency: " + input.currency());
        }

        final var balanceUpdateId = new BalanceUpdateId(Snowflake.get().nextId());

        try {

            final var history = this.balanceUpdater.withdraw(
                input.transactionId(), input.transactionAt(), balanceUpdateId, wallet.balanceId(),
                input.amount(), "Withdraw funds");

            final var output = new Output(
                history.balanceUpdateId(), history.balanceId(), history.action(),
                history.transactionId(), history.currency(), history.amount(), history.oldBalance(),
                history.newBalance(), history.transactionAt());

            LOGGER.info("WithdrawFundCommand : output: ({})", ObjectLogger.log(output));

            return output;

        } catch (final BalanceUpdater.NoBalanceUpdateException e) {
            throw new NoBalanceUpdateForTransactionException(input.transactionId());

        } catch (final BalanceUpdater.InsufficientBalanceException e) {
            throw new InsufficientBalanceException(
                e.getBalanceId(), e.getAmount(), e.getOldBalance(), e.getTransactionId());
        }
    }

}
