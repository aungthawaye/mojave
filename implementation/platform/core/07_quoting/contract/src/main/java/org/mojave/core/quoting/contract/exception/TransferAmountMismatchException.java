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
package org.mojave.core.quoting.contract.exception;

import lombok.Getter;
import org.mojave.component.misc.exception.CheckedDomainException;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.specification.fspiop.core.AmountType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
public class TransferAmountMismatchException extends CheckedDomainException {

    public static final String CODE = "TRANSFER_AMOUNT_MISMATCH";

    private static final String TEMPLATE = "Transfer Amount ({0}) must be same as the Amount ({1}) of Amount Type ({2}) .";

    private final BigDecimal transferAmount;

    private final BigDecimal amount;

    private final AmountType amountType;

    public TransferAmountMismatchException(final BigDecimal transferAmount,
                                           final BigDecimal amount,
                                           final AmountType amountType) {

        super(new ErrorTemplate(
            CODE, TEMPLATE, new String[]{
            transferAmount.stripTrailingZeros().toPlainString(),
            amount.stripTrailingZeros().toPlainString(),
            amountType.name()}));

        this.transferAmount = transferAmount;
        this.amount = amount;
        this.amountType = amountType;
    }

    public static TransferAmountMismatchException from(final Map<String, String> extras) {

        final var transferAmount = new BigDecimal(extras.get(Keys.TRANSFER_AMOUNT));
        final var amount = new BigDecimal(extras.get(Keys.AMOUNT));
        final var amountType = AmountType.valueOf(extras.get(Keys.AMOUNT_TYPE));

        return new TransferAmountMismatchException(transferAmount, amount, amountType);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.TRANSFER_AMOUNT, this.transferAmount.stripTrailingZeros().toPlainString());
        extras.put(Keys.AMOUNT, this.amount.stripTrailingZeros().toPlainString());
        extras.put(Keys.AMOUNT_TYPE, this.amountType.name());

        return extras;
    }

    public static class Keys {

        public static final String TRANSFER_AMOUNT = "transferAmount";

        public static final String AMOUNT = "amount";

        public static final String AMOUNT_TYPE = "amountType";

    }

}
