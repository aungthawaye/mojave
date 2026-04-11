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

package org.mojave.wallet.contract.exception;

import lombok.Getter;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.scheme.rule.wallet.WalletPurpose;

import java.util.HashMap;
import java.util.Map;

@Getter
public class WalletNotFoundException extends UncheckedDomainException {

    public static final String CODE = "WALLET_NOT_FOUND";

    private static final String TEMPLATE =
        "Wallet does not exist : walletOwnerId ({0}) | currency ({1}) | purpose ({2}).";

    private final WalletOwnerId walletOwnerId;

    private final Currency currency;

    private final WalletPurpose purpose;

    public WalletNotFoundException(final WalletOwnerId walletOwnerId, final Currency currency,
                                   final WalletPurpose purpose) {

        super(new ErrorTemplate(
            CODE, TEMPLATE, new String[]{
            walletOwnerId.getId().toString(),
            currency.name(),
            purpose.name()}));

        this.walletOwnerId = walletOwnerId;
        this.currency = currency;
        this.purpose = purpose;
    }

    public static WalletNotFoundException from(final Map<String, String> extras) {

        final var walletOwnerId = new WalletOwnerId(Long.parseLong(extras.get(Keys.WALLET_OWNER_ID)));
        final var currency = Currency.valueOf(extras.get(Keys.CURRENCY));
        final var purpose = WalletPurpose.valueOf(extras.get(Keys.PURPOSE));

        return new WalletNotFoundException(walletOwnerId, currency, purpose);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.WALLET_OWNER_ID, this.walletOwnerId.getId().toString());
        extras.put(Keys.CURRENCY, this.currency.name());
        extras.put(Keys.PURPOSE, this.purpose.name());

        return extras;
    }

    public static class Keys {

        public static final String WALLET_OWNER_ID = "walletOwnerId";

        public static final String CURRENCY = "currency";

        public static final String PURPOSE = "purpose";

    }

}
