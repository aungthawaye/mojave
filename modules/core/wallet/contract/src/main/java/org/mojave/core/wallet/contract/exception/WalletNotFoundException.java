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

package org.mojave.core.wallet.contract.exception;

import lombok.Getter;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.identifier.wallet.WalletId;
import org.mojave.scheme.rule.identifier.wallet.WalletOwnerId;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;

import java.util.HashMap;
import java.util.Map;

@Getter
public class WalletNotFoundException extends UncheckedDomainException {

    public static final String CODE = "WALLET_NOT_FOUND";

    private static final String BY_OWNER_TEMPLATE =
        "Wallet does not exist : walletOwnerId ({0}) | currency ({1}) | tag ({2}).";

    private static final String BY_ID_TEMPLATE =
        "Wallet does not exist : walletId ({0}).";

    private final WalletId walletId;

    private final WalletOwnerId walletOwnerId;

    private final Currency currency;

    private final String tag;

    public WalletNotFoundException(final WalletOwnerId walletOwnerId, final Currency currency,
                                   final String tag) {

        super(new ErrorTemplate(
            CODE, BY_OWNER_TEMPLATE, new String[]{
            walletOwnerId.getId().toString(),
            currency.name(),
            tag}));

        this.walletId = null;
        this.walletOwnerId = walletOwnerId;
        this.currency = currency;
        this.tag = tag;
    }

    public WalletNotFoundException(final WalletId walletId) {

        super(new ErrorTemplate(
            CODE, BY_ID_TEMPLATE, new String[]{
            walletId.getId().toString()}));

        this.walletId = walletId;
        this.walletOwnerId = null;
        this.currency = null;
        this.tag = null;
    }

    public static WalletNotFoundException from(final Map<String, String> extras) {

        if (extras.containsKey(Keys.WALLET_ID)) {
            final var walletId = new WalletId(Long.parseLong(extras.get(Keys.WALLET_ID)));
            return new WalletNotFoundException(walletId);
        }

        final var walletOwnerId = new WalletOwnerId(Long.parseLong(extras.get(Keys.WALLET_OWNER_ID)));
        final var currency = Currency.valueOf(extras.get(Keys.CURRENCY));
        final var tag = extras.get(Keys.TAG);

        return new WalletNotFoundException(walletOwnerId, currency, tag);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        if (this.walletId != null) {
            extras.put(Keys.WALLET_ID, this.walletId.getId().toString());
            return extras;
        }

        extras.put(Keys.WALLET_OWNER_ID, this.walletOwnerId.getId().toString());
        extras.put(Keys.CURRENCY, this.currency.name());
        extras.put(Keys.TAG, this.tag);

        return extras;
    }

    public static class Keys {

        public static final String WALLET_ID = "walletId";

        public static final String WALLET_OWNER_ID = "walletOwnerId";

        public static final String CURRENCY = "currency";

        public static final String TAG = "tag";

    }

}
