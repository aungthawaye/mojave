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
import org.mojave.component.misc.exception.CheckedDomainException;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.core.common.datatype.identifier.accounting.AccountOwnerId;
import org.mojave.core.common.datatype.identifier.accounting.ChartEntryId;
import org.mojave.fspiop.spec.core.Currency;

import java.util.HashMap;
import java.util.Map;

@Getter
public class PostingAccountNotFoundException extends CheckedDomainException {

    public static final String CODE = "POSTING_ACCOUNT_NOT_FOUND";

    private static final String TEMPLATE = "Posting Account cannot be not found for Owner ID ({0}), Chart Entry ID ({1}) and Currency ({2}) combination.";

    private final AccountOwnerId ownerId;

    private final ChartEntryId chartEntryId;

    private final Currency currency;

    public PostingAccountNotFoundException(final AccountOwnerId ownerId,
                                           final ChartEntryId chartEntryId,
                                           final Currency currency) {

        super(new ErrorTemplate(
            CODE, TEMPLATE, new String[]{
            ownerId.getId().toString(),
            chartEntryId.getId().toString(),
            currency.name()}));

        this.ownerId = ownerId;
        this.chartEntryId = chartEntryId;
        this.currency = currency;
    }

    public static PostingAccountNotFoundException from(final Map<String, String> extras) {

        final var ownerId = new AccountOwnerId(Long.valueOf(extras.get(Keys.OWNER_ID)));
        final var chartEntryId = new ChartEntryId(Long.valueOf(extras.get(Keys.CHART_ENTRY_ID)));
        final var currency = Currency.valueOf(extras.get(Keys.CURRENCY));

        return new PostingAccountNotFoundException(ownerId, chartEntryId, currency);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.OWNER_ID, this.ownerId.getId().toString());
        extras.put(Keys.CHART_ENTRY_ID, this.chartEntryId.getId().toString());
        extras.put(Keys.CURRENCY, this.currency.name());

        return extras;
    }

    public static class Keys {

        public static final String OWNER_ID = "ownerId";

        public static final String CHART_ENTRY_ID = "chartEntryId";

        public static final String CURRENCY = "currency";

    }

}
