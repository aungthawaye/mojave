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

package org.mojave.core.wallet.contract.exception.ndc;

import lombok.Getter;
import org.mojave.scheme.rule.identifier.transaction.TransactionId;
import org.mojave.scheme.rule.identifier.wallet.WalletId;
import org.mojave.component.misc.exception.CheckedDomainException;
import org.mojave.component.misc.exception.ErrorTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
public class PositionReservedExceedsNdcException extends CheckedDomainException {

    public static final String CODE = "POSITION_RESERVED_EXCEEDS_NDC";

    private static final String TEMPLATE =
        "Position plus reserved exceeds new NDC : walletId ({0}) | amount ({1}) | position ({2}) | reserved ({3}) | newNdc ({4}) | transactionId ({5}).";

    private final WalletId walletId;

    private final BigDecimal amount;

    private final BigDecimal position;

    private final BigDecimal reserved;

    private final BigDecimal newNdc;

    private final TransactionId transactionId;

    public PositionReservedExceedsNdcException(final WalletId walletId, final BigDecimal amount,
                                               final BigDecimal position,
                                               final BigDecimal reserved,
                                               final BigDecimal newNdc,
                                               final TransactionId transactionId) {

        super(new ErrorTemplate(
            CODE, TEMPLATE, new String[]{
            walletId.getId().toString(),
            amount.stripTrailingZeros().toPlainString(),
            position.stripTrailingZeros().toPlainString(),
            reserved.stripTrailingZeros().toPlainString(),
            newNdc.stripTrailingZeros().toPlainString(),
            transactionId.getId().toString()}));

        this.walletId = walletId;
        this.amount = amount;
        this.position = position;
        this.reserved = reserved;
        this.newNdc = newNdc;
        this.transactionId = transactionId;
    }

    public static PositionReservedExceedsNdcException from(final Map<String, String> extras) {

        final var walletId = new WalletId(Long.valueOf(extras.get(Keys.WALLET_ID)));
        final var amount = new BigDecimal(extras.get(Keys.AMOUNT));
        final var position = new BigDecimal(extras.get(Keys.POSITION));
        final var reserved = new BigDecimal(extras.get(Keys.RESERVED));
        final var newNdc = new BigDecimal(extras.get(Keys.NEW_NDC));
        final var transactionId = new TransactionId(Long.valueOf(extras.get(Keys.TRANSACTION_ID)));

        return new PositionReservedExceedsNdcException(
            walletId, amount, position, reserved, newNdc, transactionId);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.WALLET_ID, this.walletId.getId().toString());
        extras.put(Keys.AMOUNT, this.amount.stripTrailingZeros().toPlainString());
        extras.put(Keys.POSITION, this.position.stripTrailingZeros().toPlainString());
        extras.put(Keys.RESERVED, this.reserved.stripTrailingZeros().toPlainString());
        extras.put(Keys.NEW_NDC, this.newNdc.stripTrailingZeros().toPlainString());
        extras.put(Keys.TRANSACTION_ID, this.transactionId.getId().toString());

        return extras;
    }

    public static class Keys {

        public static final String WALLET_ID = "walletId";

        public static final String AMOUNT = "amount";

        public static final String POSITION = "position";

        public static final String RESERVED = "reserved";

        public static final String NEW_NDC = "newNdc";

        public static final String TRANSACTION_ID = "transactionId";

    }

}
