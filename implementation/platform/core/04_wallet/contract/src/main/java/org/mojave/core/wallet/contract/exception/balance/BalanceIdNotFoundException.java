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
package org.mojave.core.wallet.contract.exception.balance;

import lombok.Getter;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.core.common.datatype.identifier.wallet.BalanceId;

import java.util.HashMap;
import java.util.Map;

@Getter
public class BalanceIdNotFoundException extends UncheckedDomainException {

    public static final String CODE = "BALANCE_ID_NOT_FOUND";

    private static final String TEMPLATE = "Balance Id ({0}) cannot be found.";

    private final BalanceId balanceId;

    public BalanceIdNotFoundException(final BalanceId balanceId) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{balanceId.getId().toString()}));

        this.balanceId = balanceId;
    }

    public static BalanceIdNotFoundException from(final Map<String, String> extras) {

        final var id = new BalanceId(Long.valueOf(extras.get(Keys.BALANCE_ID)));

        return new BalanceIdNotFoundException(id);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.BALANCE_ID, this.balanceId.getId().toString());

        return extras;
    }

    public static class Keys {

        public static final String BALANCE_ID = "balanceId";

    }

}
