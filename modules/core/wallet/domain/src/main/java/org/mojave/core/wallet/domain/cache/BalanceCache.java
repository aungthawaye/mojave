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
package org.mojave.core.wallet.domain.cache;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import org.mojave.common.datatype.identifier.wallet.BalanceId;
import org.mojave.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.core.wallet.contract.data.BalanceData;
import org.mojave.common.datatype.enums.Currency;

import java.util.Set;

public interface BalanceCache {

    BalanceData get(BalanceId balanceId);

    BalanceData get(WalletOwnerId walletOwnerId, Currency currency);

    Set<BalanceData> get(WalletOwnerId walletOwnerId);

    class Updater {

        @PostRemove
        public void remove() {

        }

        @PostPersist
        public void save() {

        }

    }

}
