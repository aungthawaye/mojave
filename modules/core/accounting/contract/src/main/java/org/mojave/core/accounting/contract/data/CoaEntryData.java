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

import org.mojave.common.datatype.enums.accounting.AccountType;
import org.mojave.common.datatype.identifier.accounting.CoaEntryId;
import org.mojave.common.datatype.identifier.accounting.CoaId;
import org.mojave.common.datatype.type.accounting.CoaEntryCode;

import java.time.Instant;
import java.util.Objects;

public record CoaEntryData(CoaEntryId coaEntryId,
                           String category,
                           CoaEntryCode code,
                           String name,
                           String description,
                           AccountType accountType,
                           Instant createdAt,
                           CoaId coaId) {

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof CoaEntryData that)) {
            return false;
        }
        return Objects.equals(coaEntryId, that.coaEntryId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(coaEntryId);
    }

}
