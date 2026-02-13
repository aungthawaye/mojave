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

package org.mojave.core.wallet.contract.exception.position;

import lombok.Getter;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.identifier.wallet.PositionId;
import org.mojave.component.misc.exception.CheckedDomainException;
import org.mojave.component.misc.exception.ErrorTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
public class PositionLimitExceededException extends CheckedDomainException {

    public static final String CODE = "POSITION_LIMIT_EXCEEDED";

    private static final String TEMPLATE = "Position limit exceeded : positionId ({0}) | amount({1}) | position: ({2}) | reserved: ({3}) | netDebitCap: ({4}) | transaction id: ({5}).";

    private final PositionId positionId;

    private final BigDecimal amount;

    private final BigDecimal position;

    private final BigDecimal reserved;

    private final BigDecimal netDebitCap;

    private final TransactionId transactionId;

    public PositionLimitExceededException(final PositionId positionId,
                                          final BigDecimal amount,
                                          final BigDecimal position,
                                          final BigDecimal reserved,
                                          final BigDecimal netDebitCap,
                                          final TransactionId transactionId) {

        super(new ErrorTemplate(
            CODE, TEMPLATE, new String[]{
            positionId.getId().toString(),
            amount.stripTrailingZeros().toPlainString(),
            position.stripTrailingZeros().toPlainString(),
            reserved.stripTrailingZeros().toPlainString(),
            netDebitCap.stripTrailingZeros().toPlainString(),
            transactionId.getId().toString()}));

        this.positionId = positionId;
        this.amount = amount;
        this.position = position;
        this.reserved = reserved;
        this.netDebitCap = netDebitCap;
        this.transactionId = transactionId;
    }

    public static PositionLimitExceededException from(final Map<String, String> extras) {

        final var positionId = new PositionId(Long.valueOf(extras.get(Keys.POSITION_ID)));
        final var amount = new BigDecimal(extras.get(Keys.AMOUNT));
        final var position = new BigDecimal(extras.get(Keys.POSITION));
        final var reserved = new BigDecimal(extras.get(Keys.RESERVED));
        final var netDebitCap = new BigDecimal(extras.get(Keys.NET_DEBIT_CAP));
        final var transactionId = new TransactionId(Long.valueOf(extras.get(Keys.TRANSACTION_ID)));

        return new PositionLimitExceededException(
            positionId, amount, position, reserved, netDebitCap, transactionId);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.POSITION_ID, this.positionId.getId().toString());
        extras.put(Keys.AMOUNT, this.amount.stripTrailingZeros().toPlainString());
        extras.put(Keys.POSITION, this.position.stripTrailingZeros().toPlainString());
        extras.put(Keys.RESERVED, this.reserved.stripTrailingZeros().toPlainString());
        extras.put(Keys.NET_DEBIT_CAP, this.netDebitCap.stripTrailingZeros().toPlainString());
        extras.put(Keys.TRANSACTION_ID, this.transactionId.getId().toString());

        return extras;
    }

    public static class Keys {

        public static final String POSITION_ID = "positionId";

        public static final String AMOUNT = "amount";

        public static final String POSITION = "position";

        public static final String RESERVED = "reserved";

        public static final String NET_DEBIT_CAP = "netDebitCap";

        public static final String TRANSACTION_ID = "transactionId";

    }

}
