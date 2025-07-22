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

import io.mojaloop.common.datatype.enumeration.ActivationStatus;
import io.mojaloop.common.datatype.enumeration.TerminationStatus;
import io.mojaloop.common.datatype.enumeration.fspiop.EndpointType;
import io.mojaloop.common.datatype.identifier.participant.EndpointId;
import io.mojaloop.common.datatype.identifier.participant.FspId;
import io.mojaloop.common.datatype.identifier.participant.SupportedCurrencyId;
import io.mojaloop.common.datatype.type.fspiop.FspCode;
import io.mojaloop.common.fspiop.model.core.Currency;

import java.util.Map;
import java.util.Objects;

public record FspData(FspId fspId,
                      FspCode fspCode,
                      String name,
                      SupportedCurrencyData[] supportedCurrencies,
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

    public record SupportedCurrencyData(SupportedCurrencyId supportedCurrencyId, Currency currency, ActivationStatus activationStatus) {

        @Override
        public boolean equals(Object o) {

            if (!(o instanceof SupportedCurrencyData that)) {
                return false;
            }
            return Objects.equals(supportedCurrencyId, that.supportedCurrencyId);
        }

        @Override
        public int hashCode() {

            return Objects.hashCode(supportedCurrencyId);
        }

    }

    public record EndpointData(EndpointId endpointId, EndpointType type, String host) {

        @Override
        public boolean equals(Object o) {

            if (!(o instanceof EndpointData that)) {
                return false;
            }
            return Objects.equals(endpointId, that.endpointId);
        }

        @Override
        public int hashCode() {

            return Objects.hashCode(endpointId);
        }

    }

}
