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
package org.mojave.core.wallet.contract.exception.balance;

import lombok.Getter;
import org.mojave.component.misc.exception.CheckedDomainException;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.identifier.wallet.BalanceId;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
public class InsufficientBalanceException extends CheckedDomainException {

    public static final String CODE = "INSUFFICIENT_BALANCE";

    private static final String TEMPLATE = "Insufficient Balance : balanceId ({0}) | amount({1}) | current balance: ({2}) | transaction id: ({3}).";

    private final BalanceId balanceId;

    private final BigDecimal amount;

    private final BigDecimal oldBalance;

    private final TransactionId transactionId;

    public InsufficientBalanceException(final BalanceId balanceId,
                                        final BigDecimal amount,
                                        final BigDecimal oldBalance,
                                        final TransactionId transactionId) {

        super(new ErrorTemplate(
            CODE, TEMPLATE, new String[]{
            balanceId.getId().toString(),
            amount.stripTrailingZeros().toPlainString(),
            oldBalance.stripTrailingZeros().toPlainString(),
            transactionId.getId().toString()}));

        this.balanceId = balanceId;
        this.amount = amount;
        this.oldBalance = oldBalance;
        this.transactionId = transactionId;
    }

    public static InsufficientBalanceException from(final Map<String, String> extras) {

        final var walletId = new BalanceId(Long.valueOf(extras.get(Keys.balance_id)));
        final var amount = new BigDecimal(extras.get(Keys.AMOUNT));
        final var oldBalance = new BigDecimal(extras.get(Keys.OLD_BALANCE));
        final var transactionId = new TransactionId(Long.valueOf(extras.get(Keys.TRANSACTION_ID)));

        return new InsufficientBalanceException(walletId, amount, oldBalance, transactionId);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.balance_id, this.balanceId.getId().toString());
        extras.put(Keys.AMOUNT, this.amount.stripTrailingZeros().toPlainString());
        extras.put(Keys.OLD_BALANCE, this.oldBalance.stripTrailingZeros().toPlainString());
        extras.put(Keys.TRANSACTION_ID, this.transactionId.getId().toString());

        return extras;
    }

    public static class Keys {

        public static final String balance_id = "balanceId";

        public static final String AMOUNT = "amount";

        public static final String OLD_BALANCE = "oldBalance";

        public static final String TRANSACTION_ID = "transactionId";

    }

}
