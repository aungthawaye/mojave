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
package org.mojave.core.accounting.store;

import org.mojave.core.accounting.contract.data.AccountData;
import org.mojave.core.common.datatype.identifier.accounting.AccountId;
import org.mojave.core.common.datatype.identifier.accounting.AccountOwnerId;
import org.mojave.core.common.datatype.identifier.accounting.ChartEntryId;
import org.mojave.core.common.datatype.type.accounting.AccountCode;
import org.mojave.specification.fspiop.core.Currency;

import java.util.Set;

public interface AccountingStore {

    AccountData get(AccountCode accountCode);

    Set<AccountData> get(AccountOwnerId ownerId);

    AccountData get(AccountId accountId);

    AccountData get(ChartEntryId chartEntryId, AccountOwnerId ownerId, Currency currency);

    Set<AccountData> get(ChartEntryId chartEntryId);

}
