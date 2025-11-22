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

package io.mojaloop.core.quoting.contract.exception;

import io.mojaloop.component.misc.exception.CheckedDomainException;
import io.mojaloop.component.misc.exception.ErrorTemplate;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
public class ReceivingAmountLargerException extends CheckedDomainException {

    public static final String CODE = "RECEIVING_AMOUNT_LARGER";

    private static final String TEMPLATE = "Payee Receiving Amount ({0}) must not be larger than the Transfer Amount ({1}) for Amount Type (RECEIVE) .";

    private final BigDecimal receivingAmount;

    private final BigDecimal transferAmount;

    public ReceivingAmountLargerException(final BigDecimal receivingAmount, final BigDecimal transferAmount) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{receivingAmount.stripTrailingZeros().toPlainString(), transferAmount.stripTrailingZeros().toPlainString()}));

        this.receivingAmount = receivingAmount;
        this.transferAmount = transferAmount;
    }

    public static ReceivingAmountLargerException from(final Map<String, String> extras) {

        final var receivingAmount = new BigDecimal(extras.get(Keys.RECEIVING_AMOUNT));
        final var transferAmount = new BigDecimal(extras.get(Keys.TRANSFER_AMOUNT));

        return new ReceivingAmountLargerException(receivingAmount, transferAmount);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.RECEIVING_AMOUNT, this.receivingAmount.stripTrailingZeros().toPlainString());
        extras.put(Keys.TRANSFER_AMOUNT, this.transferAmount.stripTrailingZeros().toPlainString());

        return extras;
    }

    public static class Keys {

        public static final String RECEIVING_AMOUNT = "receivingAmount";

        public static final String TRANSFER_AMOUNT = "transferAmount";

    }

}
