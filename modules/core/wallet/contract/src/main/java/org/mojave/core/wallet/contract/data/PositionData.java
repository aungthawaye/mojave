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

package org.mojave.core.wallet.contract.data;

import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.wallet.PositionId;
import org.mojave.common.datatype.identifier.wallet.WalletOwnerId;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public record PositionData(PositionId positionId,
                           WalletOwnerId walletOwnerId,
                           Currency currency,
                           String name,
                           BigDecimal position,
                           BigDecimal reserved,
                           BigDecimal netDebitCap,
                           Instant createdAt) {

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof PositionData positionData)) {
            return false;
        }
        return Objects.equals(positionId, positionData.positionId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(positionId);
    }

}
