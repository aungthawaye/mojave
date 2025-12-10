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

package org.mojave.core.wallet.contract.data;

import org.mojave.core.common.datatype.enums.wallet.BalanceAction;
import org.mojave.core.common.datatype.identifier.transaction.TransactionId;
import org.mojave.core.common.datatype.identifier.wallet.BalanceId;
import org.mojave.core.common.datatype.identifier.wallet.BalanceUpdateId;
import org.mojave.fspiop.spec.core.Currency;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public record BalanceUpdateData(BalanceUpdateId balanceUpdateId,
                                BalanceId balanceId,
                                BalanceAction action,
                                TransactionId transactionId,
                                Currency currency,
                                BigDecimal amount,
                                BigDecimal oldBalance,
                                BigDecimal newBalance,
                                String description,
                                Instant transactionAt,
                                Instant createdAt,
                                BalanceUpdateId reversedId) {

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof BalanceUpdateData balanceUpdateData)) {
            return false;
        }
        return Objects.equals(balanceUpdateId, balanceUpdateData.balanceUpdateId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(balanceUpdateId);
    }

}
