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
package io.mojaloop.fspiop.component.handy;

import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.spec.core.Money;

import java.math.BigDecimal;

public class FspiopMoney {

    public static void validate(Money money) throws FspiopException {

        if (money == null) {
            return;
        }

        var scale = FspiopCurrencies.get(money.getCurrency()).scale();
        var submittedScale = new BigDecimal(money.getAmount()).scale();

        if (submittedScale > scale) {

            throw new FspiopException(
                FspiopErrors.GENERIC_VALIDATION_ERROR,
                "Currency scale does not match. " + money.getCurrency() + " supports only " +
                    scale + " decimal places.");
        }

    }

}
