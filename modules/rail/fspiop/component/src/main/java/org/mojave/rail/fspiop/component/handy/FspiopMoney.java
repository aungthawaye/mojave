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

package org.mojave.rail.fspiop.component.handy;

import com.google.common.primitives.UnsignedLong;
import org.mojave.component.misc.handy.MoneyUtil;
import org.mojave.rail.fspiop.component.error.FspiopErrors;
import org.mojave.rail.fspiop.component.exception.FspiopException;
import org.mojave.scheme.fspiop.core.Money;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class FspiopMoney {

    private static final BigInteger UINT64_MAX = new BigInteger(UnsignedLong.MAX_VALUE.toString());

    public static BigDecimal deserialize(UnsignedLong amount, int scale) {

        return MoneyUtil.deserialize(amount, scale);
    }

    public static UnsignedLong serialize(BigDecimal amount, int scale, RoundingMode roundingMode) {

        return MoneyUtil.serialize(amount, scale, roundingMode);
    }

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
