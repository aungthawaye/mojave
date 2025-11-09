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

package io.mojaloop.core.wallet.contract.exception.position;

import io.mojaloop.component.misc.exception.CheckedDomainException;
import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionId;

import java.math.BigDecimal;

public class PositionLimitExceededException extends CheckedDomainException {

    private static final String TEMPLATE = "Position limit exceeded : position ({0}) | amount({2}) | position: ({3}) | netDebitCap: ({4}) | transaction id: ({5}).";

    public PositionLimitExceededException(PositionId positionId, BigDecimal amount, BigDecimal position, BigDecimal netDebitCap, TransactionId transactionId) {

        super(
            new ErrorTemplate("POSITION_LIMIT_EXCEEDED", TEMPLATE), positionId.toString(), amount.stripTrailingZeros().toPlainString(),
            position.stripTrailingZeros().toPlainString(), netDebitCap.stripTrailingZeros().toPlainString(), transactionId.getId().toString());
    }

}
