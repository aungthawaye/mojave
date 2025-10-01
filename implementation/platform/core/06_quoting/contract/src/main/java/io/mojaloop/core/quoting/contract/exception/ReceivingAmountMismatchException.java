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

package io.mojaloop.core.quoting.contract.exception;

import io.mojaloop.component.misc.exception.DomainException;
import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.fspiop.spec.core.AmountType;

import java.math.BigDecimal;

public class ReceivingAmountMismatchException extends DomainException {

    private static final String TEMPLATE = "Payee Receiving Amount ({0}) must be same as the Amount ({1}) of Amount Type ({2}) .";

    public ReceivingAmountMismatchException(BigDecimal receivingAmount, BigDecimal amount, AmountType amountType) {

        super(new ErrorTemplate("RECEIVING_AMOUNT_MISMATCH", TEMPLATE),
              receivingAmount.stripTrailingZeros().toPlainString(),
              amount.stripTrailingZeros().toPlainString(),
              amountType.name());

    }

}
