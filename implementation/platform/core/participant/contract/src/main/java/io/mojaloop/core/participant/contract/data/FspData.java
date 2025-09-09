/*-
 * ================================================================================
 * Mojaloop OSS
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

package io.mojaloop.core.participant.contract.data;

import io.mojaloop.core.common.datatype.enumeration.ActivationStatus;
import io.mojaloop.core.common.datatype.enumeration.TerminationStatus;
import io.mojaloop.core.common.datatype.enumeration.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.identifier.participant.FspEndpointId;
import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.common.datatype.identifier.participant.FspCurrencyId;
import io.mojaloop.core.common.datatype.type.fspiop.FspCode;
import io.mojaloop.fspiop.spec.core.Currency;

import java.util.Map;
import java.util.Objects;

public record FspData(FspId fspId,
                      FspCode fspCode,
                      String name,
                      FspCurrencyData[] currencies,
                      Map<EndpointType, EndpointData> endpoints,
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

    public record FspCurrencyData(FspCurrencyId fspCurrencyId, Currency currency, ActivationStatus activationStatus) {

        @Override
        public boolean equals(Object o) {

            if (!(o instanceof FspCurrencyData that)) {
                return false;
            }
            return Objects.equals(fspCurrencyId, that.fspCurrencyId);
        }

        @Override
        public int hashCode() {

            return Objects.hashCode(fspCurrencyId);
        }

    }

    public record EndpointData(FspEndpointId fspEndpointId, EndpointType type, String baseUrl) {

        @Override
        public boolean equals(Object o) {

            if (!(o instanceof EndpointData that)) {
                return false;
            }

            return Objects.equals(fspEndpointId, that.fspEndpointId);
        }

        @Override
        public int hashCode() {

            return Objects.hashCode(fspEndpointId);
        }

    }

}
