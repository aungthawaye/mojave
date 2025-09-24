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

package io.mojaloop.core.account.contract.query;

import io.mojaloop.component.misc.query.PagedRequest;
import io.mojaloop.component.misc.query.PagedResult;
import io.mojaloop.component.misc.query.SortingMode;
import io.mojaloop.core.account.contract.data.AccountData;
import io.mojaloop.core.account.contract.exception.account.AccountCodeNotFoundException;
import io.mojaloop.core.account.contract.exception.account.AccountIdNotFoundException;
import io.mojaloop.core.common.datatype.identifier.account.AccountId;
import io.mojaloop.core.common.datatype.identifier.account.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.account.OwnerId;
import io.mojaloop.core.common.datatype.type.account.AccountCode;
import io.mojaloop.fspiop.spec.core.Currency;

import java.util.List;
import java.util.Set;

public interface AccountQuery {

    PagedResult<AccountData> find(AccountCode accountCode,
                                  String name,
                                  OwnerId ownerId,
                                  ChartEntryId chartEntryId,
                                  Currency currency,
                                  PagedRequest pagedRequest,
                                  Sorting.Column sortingColumn,
                                  SortingMode sortingMode);

    AccountData get(AccountCode accountCode) throws AccountCodeNotFoundException;

    List<AccountData> get(OwnerId ownerId);

    AccountData get(AccountId accountId) throws AccountIdNotFoundException;

    List<AccountData> getAll();

    class Sorting {

        public enum Column {
            ACCOUNT_CODE,
            NAME,
            OWNER_ID,
            CHART_ENTRY_ID,
            CURRENCY
        }

    }

}
