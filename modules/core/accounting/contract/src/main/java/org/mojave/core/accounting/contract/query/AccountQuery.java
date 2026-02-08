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

package org.mojave.core.accounting.contract.query;

import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.accounting.AccountId;
import org.mojave.common.datatype.identifier.accounting.AccountOwnerId;
import org.mojave.common.datatype.identifier.accounting.ChartEntryId;
import org.mojave.common.datatype.type.accounting.AccountCode;
import org.mojave.component.misc.query.PagedRequest;
import org.mojave.component.misc.query.PagedResult;
import org.mojave.component.misc.query.SortingMode;
import org.mojave.core.accounting.contract.data.AccountData;
import org.mojave.core.accounting.contract.exception.account.AccountCodeNotFoundException;
import org.mojave.core.accounting.contract.exception.account.AccountIdNotFoundException;

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

    record Criteria(Filter filter,
                    PagedRequest pagedRequest,
                    Sorting.Column sortingColumn,
                    SortingMode sortingMode) {

        public record Filter(AccountCode accountCode,
                             String name,
                             AccountOwnerId ownerId,
                             ChartEntryId chartEntryId,
                             Currency currency) { }

    }

}
