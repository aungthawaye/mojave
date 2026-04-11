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

package org.mojave.core.accounting.contract.exception.ledger;

import lombok.Getter;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.accounting.AccountOwnerId;
import org.mojave.common.datatype.identifier.accounting.CoaEntryId;
import org.mojave.component.misc.exception.CheckedDomainException;
import org.mojave.component.misc.exception.ErrorTemplate;

import java.util.HashMap;
import java.util.Map;

@Getter
public class PostingAccountNotFoundException extends CheckedDomainException {

    public static final String CODE = "POSTING_ACCOUNT_NOT_FOUND";

    private static final String TEMPLATE = "Posting Account cannot be found for Owner ID ({0}), CoA Entry ID ({1}) and Currency ({2}) combination.";

    private final AccountOwnerId ownerId;

    private final CoaEntryId coaEntryId;

    private final Currency currency;

    public PostingAccountNotFoundException(final AccountOwnerId ownerId,
                                           final CoaEntryId coaEntryId,
                                           final Currency currency) {

        super(new ErrorTemplate(
            CODE, TEMPLATE, new String[]{
            ownerId.getId().toString(),
            coaEntryId.getId().toString(),
            currency.name()}));

        this.ownerId = ownerId;
        this.coaEntryId = coaEntryId;
        this.currency = currency;
    }

    public static PostingAccountNotFoundException from(final Map<String, String> extras) {

        final var ownerId = new AccountOwnerId(Long.valueOf(extras.get(Keys.OWNER_ID)));
        final var coaEntryId = new CoaEntryId(Long.valueOf(extras.get(Keys.COA_ENTRY_ID)));
        final var currency = Currency.valueOf(extras.get(Keys.CURRENCY));

        return new PostingAccountNotFoundException(ownerId, coaEntryId, currency);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.OWNER_ID, this.ownerId.getId().toString());
        extras.put(Keys.COA_ENTRY_ID, this.coaEntryId.getId().toString());
        extras.put(Keys.CURRENCY, this.currency.name());

        return extras;
    }

    public static class Keys {

        public static final String OWNER_ID = "ownerId";

        public static final String COA_ENTRY_ID = "coaEntryId";

        public static final String CURRENCY = "currency";

    }

}
