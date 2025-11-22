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

package io.mojaloop.core.participant.contract.data;

import io.mojaloop.core.common.datatype.identifier.participant.FxRatePairId;
import io.mojaloop.core.common.datatype.identifier.participant.FxpId;
import io.mojaloop.fspiop.spec.core.Currency;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

public record FxpData(FxpId fxpId,
                      String name,
                      String host,
                      Map<String, FxRatePairData> fxRatePairs) {

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof FxpData fxpData)) {
            return false;
        }
        return Objects.equals(fxpId, fxpData.fxpId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(fxpId);
    }

    public record FxRatePairData(FxRatePairId fxRatePairId,
                                 Currency sourceCurrency,
                                 Currency targetCurrency,
                                 BigDecimal sourceAmount,
                                 BigDecimal targetAmount) {

        @Override
        public boolean equals(Object o) {

            if (!(o instanceof FxRatePairData that)) {
                return false;
            }
            return Objects.equals(fxRatePairId, that.fxRatePairId);
        }

        @Override
        public int hashCode() {

            return Objects.hashCode(fxRatePairId);
        }

    }

}
