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

package org.mojave.core.accounting.contract.data;

import org.mojave.scheme.common.datatype.enums.ActivationStatus;
import org.mojave.scheme.common.datatype.enums.TerminationStatus;
import org.mojave.scheme.common.datatype.enums.accounting.AccountType;
import org.mojave.scheme.common.datatype.identifier.accounting.AccountId;
import org.mojave.scheme.common.datatype.identifier.accounting.AccountOwnerId;
import org.mojave.scheme.common.datatype.identifier.accounting.ChartEntryId;
import org.mojave.scheme.common.datatype.type.accounting.AccountCode;
import org.mojave.scheme.fspiop.core.Currency;

import java.time.Instant;
import java.util.Objects;

public record AccountData(AccountId accountId,
                          AccountOwnerId ownerId,
                          AccountType type,
                          Currency currency,
                          AccountCode code,
                          String name,
                          String description,
                          Instant createdAt,
                          ActivationStatus activationStatus,
                          TerminationStatus terminationStatus,
                          ChartEntryId chartEntryId) {

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof AccountData accountData)) {
            return false;
        }
        return Objects.equals(accountId, accountData.accountId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(accountId);
    }

}
