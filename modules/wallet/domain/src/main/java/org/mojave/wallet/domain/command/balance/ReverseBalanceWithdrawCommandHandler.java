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

package org.mojave.wallet.domain.command.balance;

import org.mojave.common.datatype.identifier.wallet.BalanceUpdateId;
import org.mojave.common.datatype.identifier.wallet.BalanceId;
import org.mojave.component.misc.handy.Snowflake;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.wallet.contract.command.balance.ReverseBalanceWithdrawCommand;
import org.mojave.wallet.contract.engine.WalletEngine;
import org.mojave.wallet.contract.exception.balance.ReversalFailedInWalletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ReverseBalanceWithdrawCommandHandler implements ReverseBalanceWithdrawCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        ReverseBalanceWithdrawCommandHandler.class);

    private final WalletEngine walletEngine;

    public ReverseBalanceWithdrawCommandHandler(final WalletEngine walletEngine) {

        Objects.requireNonNull(walletEngine);

        this.walletEngine = walletEngine;
    }

    @Override
    public Output execute(final Input input) throws ReversalFailedInWalletException {

        LOGGER.info("ReverseWithdrawCommand : input: ({})", ObjectLogger.log(input));

        try {

            final var history = this.walletEngine.refundBalance(
                input.withdrawId(), new BalanceUpdateId(Snowflake.get().nextId()));

            final var balanceId = new BalanceId(history.walletId().getId());

            final var output = new Output(
                history.balanceUpdateId(), balanceId, history.action(),
                history.transactionId(), history.currency(), history.amount(), history.oldBalance(),
                history.newBalance(), history.transactionAt(), history.reversalId());

            LOGGER.info("ReverseWithdrawCommand : output: ({})", ObjectLogger.log(output));

            return output;

        } catch (WalletEngine.BalanceReversalFailedException e) {
            throw new ReversalFailedInWalletException(e.getReversalId());
        }
    }

}
