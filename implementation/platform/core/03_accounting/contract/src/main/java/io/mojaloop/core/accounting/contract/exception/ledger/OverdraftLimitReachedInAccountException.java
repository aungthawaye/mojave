/*-
 * ================================================================================
 * Mojaloop OSS
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
 * Mojaloop OSS
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

import io.mojaloop.component.misc.exception.DomainException;
import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.core.common.datatype.enums.accounting.Side;
import io.mojaloop.core.common.datatype.type.accounting.AccountCode;

import java.math.BigDecimal;

public class OverdraftLimitReachedInAccountException extends DomainException {

    private static final String TEMPLATE = "Account ({0}) has already reached the overdraft limit () to perform ({1}) operation for amount {2}. Current Dr : {3}, Current Cr : {4}";

    public OverdraftLimitReachedInAccountException(AccountCode accountCode,
                                                   Side side,
                                                   BigDecimal amount,
                                                   BigDecimal postedDebits,
                                                   BigDecimal postedCredits) {

        super(
            new ErrorTemplate("INSUFFICIENT_BALANCE_IN_ACCOUNT", TEMPLATE), accountCode.toString(), side.name(),
            amount.toPlainString(), postedDebits.toPlainString(), postedCredits.toPlainString());
    }

}
