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
package io.mojaloop.core.wallet.domain.model;

import io.mojaloop.core.common.datatype.identifier.wallet.WalletId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.wallet.domain.component.BalanceUpdater;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wallet {

    protected WalletId id;

    protected WalletOwnerId walletOwnerId;

    protected String currency;

    protected String name;

    protected BigDecimal balance;

    protected BigDecimal reserved;

    public BalanceHistory commit(WithdrawReservation withdrawReservation, BalanceUpdater balanceUpdater) {

        return null;
    }

    public BalanceHistory deposit(BigDecimal amount, BalanceUpdater balanceUpdater) {

        return null;
    }

    public WithdrawReservation reserve(BigDecimal amount, BalanceUpdater balanceUpdater) {

        return null;
    }

    public BalanceHistory rollback(WithdrawReservation withdrawReservation, BalanceUpdater balanceUpdater) {

        return null;
    }

    public BalanceHistory withdraw(BigDecimal amount, BalanceUpdater balanceUpdater) {

        return null;
    }

}
