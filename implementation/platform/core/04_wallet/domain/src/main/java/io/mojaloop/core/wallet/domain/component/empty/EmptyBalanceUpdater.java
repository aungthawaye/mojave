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

package io.mojaloop.core.wallet.domain.component.empty;

import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.wallet.BalanceUpdateId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletId;
import io.mojaloop.core.wallet.domain.component.BalanceUpdater;

import java.math.BigDecimal;
import java.time.Instant;

public class EmptyBalanceUpdater implements BalanceUpdater {

    @Override
    public BalanceHistory deposit(TransactionId transactionId, Instant transactionAt, BalanceUpdateId balanceUpdateId, WalletId walletId, BigDecimal amount, String description) {

        throw new UnsupportedOperationException("Balance update operations are not supported in the empty implementation");
    }

    @Override
    public BalanceHistory reverse(BalanceUpdateId reversalId, BalanceUpdateId balanceUpdateId) {

        throw new UnsupportedOperationException("Balance reversal operations are not supported in the empty implementation");
    }

    @Override
    public BalanceHistory withdraw(TransactionId transactionId, Instant transactionAt, BalanceUpdateId balanceUpdateId, WalletId walletId, BigDecimal amount, String description) {

        throw new UnsupportedOperationException("Balance withdrawal operations are not supported in the empty implementation");
    }

}
