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
import org.mojave.scheme.fspiop.core.Currency;

import java.util.HashMap;
import java.util.Map;

@Getter
public class PositionAlreadyExistsException extends UncheckedDomainException {

    public static final String CODE = "POSITION_ALREADY_EXISTS";

    private static final String TEMPLATE = "Position already exists for Owner Id ({0}) and Currency ({1}).";

    private final WalletOwnerId ownerId;

    private final Currency currency;

    public PositionAlreadyExistsException(final WalletOwnerId ownerId, final Currency currency) {

        super(new ErrorTemplate(
            CODE, TEMPLATE, new String[]{
            ownerId.getId().toString(),
            currency.name()}));

        this.ownerId = ownerId;
        this.currency = currency;
    }

    public static PositionAlreadyExistsException from(final Map<String, String> extras) {

        final var ownerId = new WalletOwnerId(Long.valueOf(extras.get(Keys.OWNER_ID)));
        final var currency = Currency.valueOf(extras.get(Keys.CURRENCY));

        return new PositionAlreadyExistsException(ownerId, currency);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.OWNER_ID, this.ownerId.getId().toString());
        extras.put(Keys.CURRENCY, this.currency.name());

        return extras;
    }

    public static class Keys {

        public static final String OWNER_ID = "ownerId";

        public static final String CURRENCY = "currency";

    }

}
