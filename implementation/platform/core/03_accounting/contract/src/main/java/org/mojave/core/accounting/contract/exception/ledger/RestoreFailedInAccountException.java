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
package org.mojave.core.accounting.contract.exception.ledger;

import lombok.Getter;
import org.mojave.component.misc.exception.CheckedDomainException;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.core.common.datatype.enums.accounting.Side;
import org.mojave.core.common.datatype.identifier.transaction.TransactionId;
import org.mojave.core.common.datatype.type.accounting.AccountCode;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
public class RestoreFailedInAccountException extends CheckedDomainException {

    public static final String CODE = "RESTORE_FAILED_IN_ACCOUNT";

    private static final String TEMPLATE = "Unable to restore Dr/Cr : account ({0}) | side ({1}) | amount({2}) | posted debits: ({3}) | posted credits: ({4}) | transaction id: ({5}).";

    private final AccountCode accountCode;

    private final Side side;

    private final BigDecimal amount;

    private final BigDecimal postedDebits;

    private final BigDecimal postedCredits;

    private final TransactionId transactionId;

    public RestoreFailedInAccountException(final AccountCode accountCode,
                                           final Side side,
                                           final BigDecimal amount,
                                           final BigDecimal postedDebits,
                                           final BigDecimal postedCredits,
                                           final TransactionId transactionId) {

        super(new ErrorTemplate(
            CODE, TEMPLATE, new String[]{
            accountCode.value(),
            side.name(),
            amount.stripTrailingZeros().toPlainString(),
            postedDebits.stripTrailingZeros().toPlainString(),
            postedCredits.stripTrailingZeros().toPlainString(),
            transactionId.getId().toString()}));

        this.accountCode = accountCode;
        this.side = side;
        this.amount = amount;
        this.postedDebits = postedDebits;
        this.postedCredits = postedCredits;
        this.transactionId = transactionId;
    }

    public static RestoreFailedInAccountException from(final Map<String, String> extras) {

        final var accountCode = new AccountCode(extras.get(Keys.ACCOUNT_CODE));
        final var side = Side.valueOf(extras.get(Keys.SIDE));
        final var amount = new BigDecimal(extras.get(Keys.AMOUNT));
        final var postedDebits = new BigDecimal(extras.get(Keys.POSTED_DEBITS));
        final var postedCredits = new BigDecimal(extras.get(Keys.POSTED_CREDITS));
        final var transactionId = new TransactionId(Long.valueOf(extras.get(Keys.TRANSACTION_ID)));

        return new RestoreFailedInAccountException(
            accountCode, side, amount, postedDebits, postedCredits, transactionId);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.ACCOUNT_CODE, this.accountCode.value());
        extras.put(Keys.SIDE, this.side.name());
        extras.put(Keys.AMOUNT, this.amount.stripTrailingZeros().toPlainString());
        extras.put(Keys.POSTED_DEBITS, this.postedDebits.stripTrailingZeros().toPlainString());
        extras.put(Keys.POSTED_CREDITS, this.postedCredits.stripTrailingZeros().toPlainString());
        extras.put(Keys.TRANSACTION_ID, this.transactionId.getId().toString());

        return extras;
    }

    public static class Keys {

        public static final String ACCOUNT_CODE = "accountCode";

        public static final String SIDE = "side";

        public static final String AMOUNT = "amount";

        public static final String POSTED_DEBITS = "postedDebits";

        public static final String POSTED_CREDITS = "postedCredits";

        public static final String TRANSACTION_ID = "transactionId";

    }

}
