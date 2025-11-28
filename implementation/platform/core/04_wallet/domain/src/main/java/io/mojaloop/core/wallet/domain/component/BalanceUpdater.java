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

package io.mojaloop.core.wallet.domain.component;

import io.mojaloop.core.common.datatype.enums.wallet.BalanceAction;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.wallet.BalanceUpdateId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletId;
import io.mojaloop.fspiop.spec.core.Currency;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

public interface BalanceUpdater {

    BalanceHistory deposit(TransactionId transactionId,
                           Instant transactionAt,
                           BalanceUpdateId balanceUpdateId,
                           WalletId walletId,
                           BigDecimal amount,
                           String description) throws BalanceUpdater.NoBalanceUpdateException;

    BalanceHistory reverse(BalanceUpdateId reversalId, BalanceUpdateId balanceUpdateId)
        throws BalanceUpdater.ReversalFailedException;

    BalanceHistory withdraw(TransactionId transactionId,
                            Instant transactionAt,
                            BalanceUpdateId balanceUpdateId,
                            WalletId walletId,
                            BigDecimal amount,
                            String description)
        throws BalanceUpdater.NoBalanceUpdateException, BalanceUpdater.InsufficientBalanceException;

    record BalanceHistory(BalanceUpdateId balanceUpdateId,
                          WalletId walletId,
                          BalanceAction action,
                          TransactionId transactionId,
                          Currency currency,
                          BigDecimal amount,
                          BigDecimal oldBalance,
                          BigDecimal newBalance,
                          Instant transactionAt,
                          BalanceUpdateId reversalId) { }

    @Getter
    class NoBalanceUpdateException extends Exception {

        private final TransactionId transactionId;

        public NoBalanceUpdateException(TransactionId transactionId) {

            super("No balance update found for transactionId: " + transactionId);

            this.transactionId = transactionId;
        }

    }

    @Getter
    class InsufficientBalanceException extends Exception {

        private final TransactionId transactionId;

        private final WalletId walletId;

        private final BigDecimal amount;

        private final BigDecimal oldBalance;

        public InsufficientBalanceException(TransactionId transactionId,
                                            WalletId walletId,
                                            BigDecimal amount,
                                            BigDecimal oldBalance) {

            super("Insufficient balance in walletId: " + walletId + " amount: " + amount +
                      " transactionId: " + transactionId + " oldBalance: " +
                      oldBalance.stripTrailingZeros().toPlainString());

            this.transactionId = transactionId;
            this.walletId = walletId;
            this.amount = amount;
            this.oldBalance = oldBalance;
        }

    }

    @Getter
    class ReversalFailedException extends Exception {

        private final BalanceUpdateId reversalId;

        public ReversalFailedException(BalanceUpdateId reversalId) {

            super("Reversal failed for balance update id: " + reversalId);

            this.reversalId = reversalId;
        }

    }

}
