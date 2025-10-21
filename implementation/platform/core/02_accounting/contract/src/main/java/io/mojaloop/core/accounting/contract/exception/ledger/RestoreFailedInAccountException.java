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

import io.mojaloop.component.misc.exception.CheckedDomainException;
import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.core.common.datatype.enums.accounting.Side;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.type.accounting.AccountCode;

import java.math.BigDecimal;

public class RestoreFailedInAccountException extends CheckedDomainException {

    private static final String TEMPLATE = "Unable to restore Dr/Cr : account ({0}) | side ({1}) | amount({2}) | posted debits: ({3}) | posted credits: ({4}) | transaction id: ({5}).";

    public RestoreFailedInAccountException(AccountCode accountCode,
                                           Side side,
                                           BigDecimal amount,
                                           BigDecimal postedDebits,
                                           BigDecimal postedCredits,
                                           TransactionId transactionId) {

        super(new ErrorTemplate("RESTORE_FAILED_IN_ACCOUNT", TEMPLATE), accountCode.value(), side.name(), amount.toPlainString(),
              postedDebits.toPlainString(), postedCredits.toPlainString(), transactionId.getId().toString());
    }

}
