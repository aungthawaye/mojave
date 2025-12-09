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
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class FspCurrencyNotSupportedByHubException extends UncheckedDomainException {

    public static final String CODE = "FSP_CURRENCY_NOT_SUPPORTED_BY_HUB";

    private static final String TEMPLATE = "Currency, {0}, being added by FSP, is not supported by Hub.";

    private final Currency currency;

    public FspCurrencyNotSupportedByHubException(final Currency currency) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{currency.name()}));

        this.currency = currency;

    }

    public static FspCurrencyNotSupportedByHubException from(final Map<String, String> extras) {

        final var currency = Currency.valueOf(extras.get(Keys.CURRENCY));

        return new FspCurrencyNotSupportedByHubException(currency);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.CURRENCY, this.currency.name());

        return extras;
    }

    public static class Keys {

        public static final String CURRENCY = "currency";

    }

}
