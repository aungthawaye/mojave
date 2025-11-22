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

package io.mojaloop.core.wallet.contract.exception.wallet;

import io.mojaloop.component.misc.exception.CheckedDomainException;
import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletId;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
public class InsufficientBalanceInWalletException extends CheckedDomainException {

    public static final String CODE = "INSUFFICIENT_BALANCE_IN_WALLET";

    private static final String TEMPLATE = "Insufficient Balance : wallet ({0}) | amount({1}) | current balance: ({2}) | transaction id: ({3}).";

    private final WalletId walletId;

    private final BigDecimal amount;

    private final BigDecimal oldBalance;

    private final TransactionId transactionId;

    public InsufficientBalanceInWalletException(final WalletId walletId, final BigDecimal amount, final BigDecimal oldBalance, final TransactionId transactionId) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{walletId.getId().toString(),
                                                             amount.stripTrailingZeros().toPlainString(),
                                                             oldBalance.stripTrailingZeros().toPlainString(),
                                                             transactionId.getId().toString()}));

        this.walletId = walletId;
        this.amount = amount;
        this.oldBalance = oldBalance;
        this.transactionId = transactionId;
    }

    public static InsufficientBalanceInWalletException from(final Map<String, String> extras) {

        final var walletId = new WalletId(Long.valueOf(extras.get(Keys.WALLET_ID)));
        final var amount = new BigDecimal(extras.get(Keys.AMOUNT));
        final var oldBalance = new BigDecimal(extras.get(Keys.OLD_BALANCE));
        final var transactionId = new TransactionId(Long.valueOf(extras.get(Keys.TRANSACTION_ID)));

        return new InsufficientBalanceInWalletException(walletId, amount, oldBalance, transactionId);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.WALLET_ID, this.walletId.getId().toString());
        extras.put(Keys.AMOUNT, this.amount.stripTrailingZeros().toPlainString());
        extras.put(Keys.OLD_BALANCE, this.oldBalance.stripTrailingZeros().toPlainString());
        extras.put(Keys.TRANSACTION_ID, this.transactionId.getId().toString());

        return extras;
    }

    public static class Keys {

        public static final String WALLET_ID = "walletId";

        public static final String AMOUNT = "amount";

        public static final String OLD_BALANCE = "oldBalance";

        public static final String TRANSACTION_ID = "transactionId";

    }

}
