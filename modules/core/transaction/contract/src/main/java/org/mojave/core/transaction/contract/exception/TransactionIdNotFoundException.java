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
package org.mojave.core.transaction.contract.exception;

import lombok.Getter;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.scheme.common.datatype.identifier.transaction.TransactionId;

import java.util.HashMap;
import java.util.Map;

@Getter
public class TransactionIdNotFoundException extends UncheckedDomainException {

    public static final String CODE = "TRANSACTION_ID_NOT_FOUND";

    private static final String TEMPLATE = "Transaction ID ({0}) cannot be not found.";

    private final TransactionId transactionId;

    public TransactionIdNotFoundException(final TransactionId transactionId) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{transactionId.getId().toString()}));

        this.transactionId = transactionId;
    }

    public static TransactionIdNotFoundException from(final Map<String, String> extras) {

        final var id = new TransactionId(Long.valueOf(extras.get(Keys.TRANSACTION_ID)));

        return new TransactionIdNotFoundException(id);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.TRANSACTION_ID, this.transactionId.getId().toString());

        return extras;
    }

    public static class Keys {

        public static final String TRANSACTION_ID = "transactionId";

    }

}
