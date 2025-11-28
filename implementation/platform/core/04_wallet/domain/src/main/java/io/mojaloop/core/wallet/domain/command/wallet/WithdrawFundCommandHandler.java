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

package io.mojaloop.core.wallet.domain.command.wallet;

import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.common.datatype.identifier.wallet.BalanceUpdateId;
import io.mojaloop.core.wallet.contract.command.wallet.WithdrawFundCommand;
import io.mojaloop.core.wallet.contract.exception.wallet.InsufficientBalanceInWalletException;
import io.mojaloop.core.wallet.contract.exception.wallet.NoBalanceUpdateForTransactionException;
import io.mojaloop.core.wallet.domain.cache.WalletCache;
import io.mojaloop.core.wallet.domain.component.BalanceUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WithdrawFundCommandHandler implements WithdrawFundCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(WithdrawFundCommandHandler.class);

    private final BalanceUpdater balanceUpdater;

    private final WalletCache walletCache;

    public WithdrawFundCommandHandler(BalanceUpdater balanceUpdater, WalletCache walletCache) {

        assert balanceUpdater != null;
        assert walletCache != null;

        this.balanceUpdater = balanceUpdater;
        this.walletCache = walletCache;
    }

    @Override
    public Output execute(final Input input)
        throws NoBalanceUpdateForTransactionException, InsufficientBalanceInWalletException {

        LOGGER.info("Executing WithdrawFundCommand with input: {}", input);

        var wallet = this.walletCache.get(input.walletOwnerId(), input.currency());

        if (wallet == null) {

            LOGGER.error(
                "Wallet does not exist for walletOwnerId: {} and currency: {}",
                input.walletOwnerId(), input.currency());
            throw new RuntimeException(
                "Wallet does not exist for walletOwnerId: " + input.walletOwnerId() +
                    " and currency: " + input.currency());
        }

        final var balanceUpdateId = new BalanceUpdateId(Snowflake.get().nextId());

        try {

            final var history = this.balanceUpdater.withdraw(
                input.transactionId(), input.transactionAt(), balanceUpdateId, wallet.walletId(),
                input.amount(), "Withdraw funds");

            var output = new Output(
                history.balanceUpdateId(), history.walletId(), history.action(),
                history.transactionId(), history.currency(), history.amount(), history.oldBalance(),
                history.newBalance(), history.transactionAt());

            LOGGER.info("WithdrawFundCommand executed successfully with output: {}", output);

            return output;

        } catch (final BalanceUpdater.NoBalanceUpdateException e) {

            LOGGER.error("Failed to withdraw funds for transaction: {}", input.transactionId());
            throw new NoBalanceUpdateForTransactionException(input.transactionId());

        } catch (final BalanceUpdater.InsufficientBalanceException e) {

            LOGGER.error(
                "Failed to withdraw funds for transaction (insufficient balance): {}",
                input.transactionId());
            throw new InsufficientBalanceInWalletException(
                e.getWalletId(), e.getAmount(), e.getOldBalance(), e.getTransactionId());
        }
    }

}
