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

package io.mojaloop.core.accounting.contract.query;

import io.mojaloop.component.misc.query.PagedRequest;
import io.mojaloop.component.misc.query.PagedResult;
import io.mojaloop.component.misc.query.SortingMode;
import io.mojaloop.core.accounting.contract.data.AccountData;
import io.mojaloop.core.accounting.contract.exception.account.AccountCodeNotFoundException;
import io.mojaloop.core.accounting.contract.exception.account.AccountIdNotFoundException;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountId;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountOwnerId;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
import io.mojaloop.core.common.datatype.type.accounting.AccountCode;
import io.mojaloop.fspiop.spec.core.Currency;

import java.util.List;

public interface AccountQuery {

    PagedResult<AccountData> find(Criteria criteria);

    AccountData get(AccountCode accountCode) throws AccountCodeNotFoundException;

    List<AccountData> get(AccountOwnerId ownerId);

    AccountData get(AccountId accountId) throws AccountIdNotFoundException;

    List<AccountData> getAll();

    class Sorting {

        public enum Column {
            ID,
            ACCOUNT_CODE,
            NAME,
            OWNER_ID,
            CHART_ENTRY_ID,
            CURRENCY
        }

    }

    record Criteria(Filter filter, PagedRequest pagedRequest, Sorting.Column sortingColumn, SortingMode sortingMode) {

        public record Filter(AccountCode accountCode, String name, AccountOwnerId ownerId, ChartEntryId chartEntryId, Currency currency) { }

    }

}
