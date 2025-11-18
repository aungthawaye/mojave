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

package io.mojaloop.core.accounting.contract.exception.account;

import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.component.misc.exception.UncheckedDomainException;
import io.mojaloop.core.common.datatype.type.accounting.AccountCode;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class AccountNotActiveException extends UncheckedDomainException {

    public static final String CODE = "ACCOUNT_NOT_ACTIVE";

    private static final String TEMPLATE = "Account with Code ({0}) is not active.";

    private final AccountCode accountCode;

    public AccountNotActiveException(AccountCode accountCode) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{accountCode.value()}));

        this.accountCode = accountCode;
    }

    public static AccountNotActiveException from(final Map<String, String> extras) {

        final var code = new AccountCode(extras.get(Keys.ACCOUNT_CODE));

        return new AccountNotActiveException(code);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.ACCOUNT_CODE, this.accountCode.value());

        return extras;
    }

    public static class Keys {

        public static final String ACCOUNT_CODE = "accountCode";

    }

}
