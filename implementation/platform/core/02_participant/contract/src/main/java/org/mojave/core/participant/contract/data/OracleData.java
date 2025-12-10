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

package org.mojave.core.participant.contract.data;

import org.mojave.core.common.datatype.enums.ActivationStatus;
import org.mojave.core.common.datatype.enums.TerminationStatus;
import org.mojave.core.common.datatype.identifier.participant.OracleId;
import org.mojave.fspiop.spec.core.PartyIdType;

import java.util.Objects;

public record OracleData(OracleId oracleId,
                         PartyIdType type,
                         String name,
                         String host,
                         ActivationStatus activationStatus,
                         TerminationStatus terminationStatus) {

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof OracleData that)) {
            return false;
        }
        return Objects.equals(oracleId, that.oracleId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(oracleId);
    }

}
