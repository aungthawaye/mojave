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

package io.mojaloop.core.participant.contract.exception.fsp;

import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.component.misc.exception.UncheckedDomainException;
import io.mojaloop.fspiop.spec.core.Currency;

public class FspCurrencyAlreadySupportedException extends UncheckedDomainException {

    private static final String TEMPLATE = "Currency, {0}, is already supported for FSP.";

    public FspCurrencyAlreadySupportedException(Currency currency) {

        super(new ErrorTemplate("FSP_CURRENCY_ALREADY_SUPPORTED", TEMPLATE), currency.name());

    }

}
