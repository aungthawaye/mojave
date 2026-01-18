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
package org.mojave.core.accounting.contract.exception.definition;

import lombok.Getter;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.core.common.datatype.enums.trasaction.TransactionType;
import org.mojave.core.common.datatype.enums.Currency;

import java.util.HashMap;
import java.util.Map;

@Getter
public class FlowDefinitionNotConfiguredException extends UncheckedDomainException {

    public static final String CODE = "FLOW_DEFINITION_NOT_CONFIGURED";

    private static final String TEMPLATE = "Flow Definition for Transaction Type ({0}) and Currency ({1}) is not yet configured.";

    private final TransactionType transactionType;

    private final Currency currency;

    public FlowDefinitionNotConfiguredException(final TransactionType transactionType,
                                                final Currency currency) {

        super(new ErrorTemplate(
            CODE, TEMPLATE, new String[]{
            transactionType.name(),
            currency.name()}));

        this.transactionType = transactionType;
        this.currency = currency;
    }

    public static FlowDefinitionNotConfiguredException from(final Map<String, String> extras) {

        final var type = TransactionType.valueOf(extras.get(Keys.TRANSACTION_TYPE));
        final var currency = Currency.valueOf(extras.get(Keys.CURRENCY));

        return new FlowDefinitionNotConfiguredException(type, currency);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.TRANSACTION_TYPE, this.transactionType.name());
        extras.put(Keys.CURRENCY, this.currency.name());

        return extras;
    }

    public static class Keys {

        public static final String TRANSACTION_TYPE = "transactionType";

        public static final String CURRENCY = "currency";

    }

}
