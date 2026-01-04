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
package org.mojave.core.accounting.contract.exception.account;

import lombok.Getter;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.scheme.common.datatype.identifier.accounting.AccountId;

import java.util.HashMap;
import java.util.Map;

@Getter
public class AccountIdNotFoundException extends UncheckedDomainException {

    public static final String CODE = "ACCOUNT_ID_NOT_FOUND";

    private static final String TEMPLATE = "Account ID ({0}) cannot be not found.";

    private final AccountId accountId;

    public AccountIdNotFoundException(final AccountId accountId) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{accountId.getId().toString()}));

        this.accountId = accountId;
    }

    public static AccountIdNotFoundException from(final Map<String, String> extras) {

        final var id = new AccountId(Long.valueOf(extras.get(Keys.ACCOUNT_ID)));

        return new AccountIdNotFoundException(id);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.ACCOUNT_ID, this.accountId.getId().toString());

        return extras;
    }

    public static class Keys {

        public static final String ACCOUNT_ID = "accountId";

    }

}
