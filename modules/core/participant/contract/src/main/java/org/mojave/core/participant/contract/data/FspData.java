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
package org.mojave.core.participant.contract.data;

import org.mojave.core.common.datatype.enums.ActivationStatus;
import org.mojave.core.common.datatype.enums.TerminationStatus;
import org.mojave.core.common.datatype.enums.participant.EndpointType;
import org.mojave.core.common.datatype.identifier.participant.FspId;
import org.mojave.core.common.datatype.type.participant.FspCode;

import java.util.Map;
import java.util.Objects;

public record FspData(FspId fspId,
                      FspCode code,
                      String name,
                      FspCurrencyData[] currencies,
                      Map<EndpointType, FspEndpointData> endpoints,
                      ActivationStatus activationStatus,
                      TerminationStatus terminationStatus) {

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof FspData fspData)) {
            return false;
        }
        return Objects.equals(fspId, fspData.fspId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(fspId);
    }

}
