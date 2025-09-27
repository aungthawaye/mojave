/*-
 * ================================================================================
 * Mojaloop OSS
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

package io.mojaloop.core.accounting.domain.cache;

import io.mojaloop.core.accounting.contract.data.AccountData;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountId;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.accounting.OwnerId;
import io.mojaloop.core.common.datatype.type.accounting.AccountCode;
import io.mojaloop.fspiop.spec.core.Currency;

import java.util.Set;

public interface AccountCache {

    void clear();

    void delete(AccountId accountId);

    AccountData get(AccountCode accountCode);

    Set<AccountData> get(OwnerId ownerId);

    AccountData get(AccountId accountId);

    AccountData get(ChartEntryId chartEntryId, OwnerId ownerId, Currency currency);

    void save(AccountData account);

    class Qualifiers {

        public static final String REDIS = "redis";

        public static final String DEFAULT = REDIS;

    }

    class Keys {

        public static String forChart(ChartEntryId chartEntryId, OwnerId ownerId, Currency currency) {

            return chartEntryId.getId().toString() + ":" + ownerId.getId().toString() + ":" + currency.name();
        }

    }

    class Names {

        public static final String WITH_ID = "acc-account-with-id";

        public static final String WITH_CODE = "acc-account-with-fsp-code";

        public static final String WITH_OWNER_ID = "acc-account-with-owner-id";

        public static final String WITH_CHARTENTRYID_OWNERID_CURRENCY = "acc-account-with-chartentryid-ownerid-currency";

    }

}
