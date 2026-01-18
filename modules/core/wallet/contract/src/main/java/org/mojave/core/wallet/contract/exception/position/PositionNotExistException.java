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
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.core.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.core.common.datatype.enums.Currency;

import java.util.HashMap;
import java.util.Map;

@Getter
public class PositionNotExistException extends UncheckedDomainException {

    public static final String CODE = "POSITION_NOT_EXIST";

    private static final String TEMPLATE = "Position does not exist : walletOwnerId ({0}) | currency ({1}).";

    private final WalletOwnerId walletOwnerId;

    private final Currency currency;

    public PositionNotExistException(final WalletOwnerId walletOwnerId, final Currency currency) {

        super(new ErrorTemplate(
            CODE, TEMPLATE, new String[]{
            walletOwnerId.getId().toString(),
            currency.name()}));

        this.walletOwnerId = walletOwnerId;
        this.currency = currency;
    }

    public static PositionNotExistException from(final Map<String, String> extras) {

        final var walletOwnerId = new WalletOwnerId(Long.valueOf(extras.get(Keys.WALLET_OWNER_ID)));
        final var currency = Currency.valueOf(extras.get(Keys.CURRENCY));

        return new PositionNotExistException(walletOwnerId, currency);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.WALLET_OWNER_ID, this.walletOwnerId.getId().toString());
        extras.put(Keys.CURRENCY, this.currency.name());

        return extras;
    }

    public static class Keys {

        public static final String WALLET_OWNER_ID = "walletOwnerId";

        public static final String CURRENCY = "currency";

    }

}
