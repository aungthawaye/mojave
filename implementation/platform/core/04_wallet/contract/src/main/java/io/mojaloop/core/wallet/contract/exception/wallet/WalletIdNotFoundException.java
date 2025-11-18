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

package io.mojaloop.core.wallet.contract.exception.wallet;

import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.component.misc.exception.UncheckedDomainException;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletId;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class WalletIdNotFoundException extends UncheckedDomainException {

    public static final String CODE = "WALLET_ID_NOT_FOUND";

    private static final String TEMPLATE = "Wallet Id ({0}) cannot be found.";

    private final WalletId walletId;

    public WalletIdNotFoundException(final WalletId walletId) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{walletId.getId().toString()}));

        this.walletId = walletId;
    }

    public static WalletIdNotFoundException from(final Map<String, String> extras) {

        final var id = new WalletId(Long.valueOf(extras.get(Keys.WALLET_ID)));

        return new WalletIdNotFoundException(id);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.WALLET_ID, this.walletId.getId().toString());

        return extras;
    }

    public static class Keys {

        public static final String WALLET_ID = "walletId";

    }

}
