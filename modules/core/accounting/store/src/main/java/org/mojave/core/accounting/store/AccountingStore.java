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
import org.mojave.core.accounting.contract.data.CoaData;
import org.mojave.core.accounting.contract.data.CoaEntryData;
import org.mojave.core.accounting.contract.data.FlowDefinitionData;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.identifier.accounting.AccountId;
import org.mojave.scheme.rule.identifier.accounting.AccountOwnerId;
import org.mojave.scheme.rule.identifier.accounting.CoaEntryId;
import org.mojave.scheme.rule.identifier.accounting.CoaId;
import org.mojave.scheme.rule.identifier.accounting.FlowDefinitionId;
import org.mojave.scheme.rule.scenario.ScenarioType;
import org.mojave.scheme.rule.type.accounting.AccountCode;
import org.mojave.scheme.rule.type.accounting.CoaEntryCode;

import java.util.List;

public interface AccountingStore {

    AccountData getAccountData(AccountId accountId);

    AccountData getAccountData(AccountCode accountCode);

    List<AccountData> getAccountData(AccountOwnerId ownerId);

    AccountData getAccountData(CoaEntryId coaEntryId, AccountOwnerId ownerId, Currency currency);

    List<AccountData> getAccountData(CoaEntryId coaEntryId);

    CoaData getCoaData(CoaId coaId);

    CoaEntryData getCoaEntryData(CoaEntryId coaEntryId);

    CoaEntryData getCoaEntryData(CoaEntryCode coaEntryCode);

    List<CoaEntryData> getCoaEntryData(CoaId coaId);

    List<CoaEntryData> getCoaEntryData(String category);

    FlowDefinitionData getFlowDefinitionData(FlowDefinitionId flowDefinitionId);

    FlowDefinitionData getFlowDefinitionData(ScenarioType scenarioType, Currency currency);

}
