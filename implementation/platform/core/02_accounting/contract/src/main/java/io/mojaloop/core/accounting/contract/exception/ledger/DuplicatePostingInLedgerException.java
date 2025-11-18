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

import io.mojaloop.component.misc.exception.CheckedDomainException;
import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.core.common.datatype.enums.accounting.Side;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.type.accounting.AccountCode;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class DuplicatePostingInLedgerException extends CheckedDomainException {

    public static final String CODE = "DUPLICATE_POSTING_IN_LEDGER";

    private static final String TEMPLATE = "Duplicate Posting in Ledger : account ({0}) | side ({1}) | transaction Id : ({2}).";

    private final AccountCode accountCode;

    private final Side side;

    private final TransactionId transactionId;

    public DuplicatePostingInLedgerException(final AccountCode accountCode, final Side side, final TransactionId transactionId) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{accountCode.value(), side.name(), transactionId.getId().toString()}));

        this.accountCode = accountCode;
        this.side = side;
        this.transactionId = transactionId;
    }

    public static DuplicatePostingInLedgerException from(final Map<String, String> extras) {

        final var accountCode = new AccountCode(extras.get(Keys.ACCOUNT_CODE));
        final var side = Side.valueOf(extras.get(Keys.SIDE));
        final var transactionId = new TransactionId(Long.valueOf(extras.get(Keys.TRANSACTION_ID)));

        return new DuplicatePostingInLedgerException(accountCode, side, transactionId);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.ACCOUNT_CODE, this.accountCode.value());
        extras.put(Keys.SIDE, this.side.name());
        extras.put(Keys.TRANSACTION_ID, this.transactionId.getId().toString());

        return extras;
    }

    public static class Keys {

        public static final String ACCOUNT_CODE = "accountCode";

        public static final String SIDE = "side";

        public static final String TRANSACTION_ID = "transactionId";

    }

}
