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

package org.mojave.scheme.rule.exception;

import lombok.Getter;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.scheme.rule.type.TransactionType;

import java.util.HashMap;
import java.util.Map;

@Getter
public class TransactionTypeDefinitionNotFoundException extends UncheckedDomainException {

    public static final String CODE = "TRANSACTION_TYPE_DEFINITION_NOT_FOUND";

    private static final String TEMPLATE = "Transaction Type Definition cannot be found for Transaction Type ({0}).";

    private final TransactionType transactionType;

    public TransactionTypeDefinitionNotFoundException(final TransactionType transactionType) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{transactionType.name()}));

        this.transactionType = transactionType;
    }

    public static TransactionTypeDefinitionNotFoundException from(
        final Map<String, String> extras) {

        final var transactionType = TransactionType.valueOf(extras.get(Keys.TRANSACTION_TYPE));

        return new TransactionTypeDefinitionNotFoundException(transactionType);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.TRANSACTION_TYPE, this.transactionType.name());

        return extras;
    }

    public static class Keys {

        public static final String TRANSACTION_TYPE = "transactionType";

    }

}
