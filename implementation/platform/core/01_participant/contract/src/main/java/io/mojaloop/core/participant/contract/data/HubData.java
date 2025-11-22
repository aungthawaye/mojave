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
/*-
 * ==============================================================================
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
 * ==============================================================================
 */

package io.mojaloop.core.participant.contract.data;

import io.mojaloop.core.common.datatype.enums.ActivationStatus;
import io.mojaloop.core.common.datatype.identifier.participant.HubCurrencyId;
import io.mojaloop.core.common.datatype.identifier.participant.HubId;
import io.mojaloop.fspiop.spec.core.Currency;

import java.util.Objects;

public record HubData(HubId hubId, String name, HubCurrencyData[] currencies) {

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof HubData hubData)) {
            return false;
        }
        return Objects.equals(hubId, hubData.hubId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(hubId);
    }

    public record HubCurrencyData(HubCurrencyId hubCurrencyId,
                                  Currency currency,
                                  ActivationStatus activationStatus) {

        @Override
        public boolean equals(Object o) {

            if (!(o instanceof HubCurrencyData that)) {
                return false;
            }

            return Objects.equals(this.hubCurrencyId, that.hubCurrencyId);
        }

        @Override
        public int hashCode() {

            return Objects.hashCode(this.hubCurrencyId);
        }

    }

}
