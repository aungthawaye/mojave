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

package io.mojaloop.core.wallet.contract.data;

import io.mojaloop.core.common.datatype.enums.wallet.PositionAction;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.fspiop.spec.core.Currency;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public record PositionUpdateData(PositionUpdateId positionUpdateId,
                                 PositionId positionId,
                                 PositionAction action,
                                 TransactionId transactionId,
                                 Currency currency,
                                 BigDecimal amount,
                                 BigDecimal oldPosition,
                                 BigDecimal newPosition,
                                 String description,
                                 Instant transactionAt,
                                 Instant createdAt,
                                 PositionUpdateId reversedId) {

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof PositionUpdateData positionUpdateData)) {
            return false;
        }
        return Objects.equals(positionUpdateId, positionUpdateData.positionUpdateId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(positionUpdateId);
    }

}
