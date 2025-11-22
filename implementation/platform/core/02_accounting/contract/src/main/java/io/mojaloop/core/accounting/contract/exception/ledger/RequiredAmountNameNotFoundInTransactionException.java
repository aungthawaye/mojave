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
/*-
 * ==============================================================================
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
 * ==============================================================================
 */

package io.mojaloop.core.accounting.contract.exception.ledger;

import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.component.misc.exception.UncheckedDomainException;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
public class RequiredAmountNameNotFoundInTransactionException extends UncheckedDomainException {

    public static final String CODE = "REQUIRED_AMOUNT_NAME_NOT_FOUND_IN_TRANSACTION";

    private static final String TEMPLATE = "Required Amount name ({0}) cannot be found in amounts ({1}) of Transaction Id ({2}).";

    private final String requiredAmountName;

    private final Set<String> amounts;

    private final TransactionId transactionId;

    public RequiredAmountNameNotFoundInTransactionException(final String requiredAmountName, final Set<String> amounts, final TransactionId transactionId) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{requiredAmountName, amounts.toString(), transactionId.getId().toString()}));

        this.requiredAmountName = requiredAmountName;
        this.amounts = amounts;
        this.transactionId = transactionId;
    }

    public static RequiredAmountNameNotFoundInTransactionException from(final Map<String, String> extras) {

        final var name = extras.get(Keys.REQUIRED_AMOUNT_NAME);
        final var amounts = Set.of(extras.get(Keys.AMOUNTS));
        final var transactionId = new TransactionId(Long.valueOf(extras.get(Keys.TRANSACTION_ID)));

        return new RequiredAmountNameNotFoundInTransactionException(name, amounts, transactionId);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.REQUIRED_AMOUNT_NAME, this.requiredAmountName);
        extras.put(Keys.AMOUNTS, this.amounts.toString());
        extras.put(Keys.TRANSACTION_ID, this.transactionId.getId().toString());

        return extras;
    }

    public static class Keys {

        public static final String REQUIRED_AMOUNT_NAME = "requiredAmountName";

        public static final String AMOUNTS = "amounts";

        public static final String TRANSACTION_ID = "transactionId";

    }

}
