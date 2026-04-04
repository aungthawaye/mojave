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

package org.mojave.core.accounting.admin.client.service;

import org.mojave.common.datatype.enums.accounting.ChartEntryCategory;
import org.mojave.common.datatype.identifier.accounting.AccountId;
import org.mojave.common.datatype.identifier.accounting.AccountOwnerId;
import org.mojave.common.datatype.identifier.accounting.CoaEntryId;
import org.mojave.common.datatype.identifier.accounting.CoaId;
import org.mojave.common.datatype.identifier.accounting.FlowDefinitionId;
import org.mojave.common.datatype.type.accounting.AccountCode;
import org.mojave.component.misc.query.PagedResult;
import org.mojave.core.accounting.contract.command.account.ActivateAccountCommand;
import org.mojave.core.accounting.contract.command.account.ChangeAccountPropertiesCommand;
import org.mojave.core.accounting.contract.command.account.CreateAccountCommand;
import org.mojave.core.accounting.contract.command.account.CreateAccountByCategoryCommand;
import org.mojave.core.accounting.contract.command.account.DeactivateAccountCommand;
import org.mojave.core.accounting.contract.command.account.TerminateAccountCommand;
import org.mojave.core.accounting.contract.command.chart.ChangeCoaEntryPropertiesCommand;
import org.mojave.core.accounting.contract.command.chart.ChangeCoaNameCommand;
import org.mojave.core.accounting.contract.command.chart.CreateCoaCommand;
import org.mojave.core.accounting.contract.command.chart.CreateCoaEntryCommand;
import org.mojave.core.accounting.contract.command.definition.ActivateFlowDefinitionCommand;
import org.mojave.core.accounting.contract.command.definition.AddFlowLineCommand;
import org.mojave.core.accounting.contract.command.definition.ChangeFlowDefinitionCurrencyCommand;
import org.mojave.core.accounting.contract.command.definition.ChangeFlowDefinitionPropertiesCommand;
import org.mojave.core.accounting.contract.command.definition.CreateFlowDefinitionCommand;
import org.mojave.core.accounting.contract.command.definition.DeactivateFlowDefinitionCommand;
import org.mojave.core.accounting.contract.command.definition.RemoveFlowLineCommand;
import org.mojave.core.accounting.contract.command.definition.TerminateFlowDefinitionCommand;
import org.mojave.core.accounting.contract.command.ledger.PostLedgerFlowCommand;
import org.mojave.core.accounting.contract.data.AccountData;
import org.mojave.core.accounting.contract.data.CoaData;
import org.mojave.core.accounting.contract.data.CoaEntryData;
import org.mojave.core.accounting.contract.data.FlowDefinitionData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.List;

public interface AccountingAdminService {

    String MODULE_PREFIX = "/accounting";

    interface AccountCommand {

        @POST(MODULE_PREFIX + "/accounts/activate-account")
        Call<ActivateAccountCommand.Output> activate(@Body ActivateAccountCommand.Input input);

        @POST(MODULE_PREFIX + "/accounts/change-account-properties")
        Call<ChangeAccountPropertiesCommand.Output> changeProperties(
            @Body ChangeAccountPropertiesCommand.Input input);

        @POST(MODULE_PREFIX + "/accounts/create-account")
        Call<CreateAccountCommand.Output> create(@Body CreateAccountCommand.Input input);

        @POST(MODULE_PREFIX + "/accounts/create-account-by-category")
        Call<CreateAccountByCategoryCommand.Output> createByCategory(
            @Body CreateAccountByCategoryCommand.Input input);

        @POST(MODULE_PREFIX + "/accounts/deactivate-account")
        Call<DeactivateAccountCommand.Output> deactivate(
            @Body DeactivateAccountCommand.Input input);

        @POST(MODULE_PREFIX + "/accounts/terminate-account")
        Call<TerminateAccountCommand.Output> terminate(@Body TerminateAccountCommand.Input input);

    }

    interface AccountQuery {

        @POST(MODULE_PREFIX + "/accounts/find-accounts")
        Call<PagedResult<AccountData>> find(
            @Body org.mojave.core.accounting.contract.query.AccountQuery.Criteria criteria);

        @GET(MODULE_PREFIX + "/accounts/get-all")
        Call<List<AccountData>> getAll();

        @GET(MODULE_PREFIX + "/accounts/get-by-code")
        Call<AccountData> getByAccountCode(@Query("accountCode") AccountCode accountCode);

        @GET(MODULE_PREFIX + "/accounts/get-by-id")
        Call<AccountData> getByAccountId(@Query("accountId") AccountId accountId);

        @GET(MODULE_PREFIX + "/accounts/get-by-owner-id")
        Call<List<AccountData>> getByOwnerId(@Query("ownerId") AccountOwnerId ownerId);

    }

    interface CoaCommand {

        @POST(MODULE_PREFIX + "/coa-entries/change-coa-entry-properties")
        Call<ChangeCoaEntryPropertiesCommand.Output> changeEntryProperties(
            @Body ChangeCoaEntryPropertiesCommand.Input input);

        @POST(MODULE_PREFIX + "/coas/change-coa-name")
        Call<ChangeCoaNameCommand.Output> changeName(@Body ChangeCoaNameCommand.Input input);

        @POST(MODULE_PREFIX + "/coas/create-coa")
        Call<CreateCoaCommand.Output> create(@Body CreateCoaCommand.Input input);

        @POST(MODULE_PREFIX + "/coa-entries/create-coa-entry")
        Call<CreateCoaEntryCommand.Output> createEntry(@Body CreateCoaEntryCommand.Input input);

    }

    interface CoaQuery {

        @GET(MODULE_PREFIX + "/coa-entries/get-all")
        Call<List<CoaEntryData>> getAllCoaEntries();

        @GET(MODULE_PREFIX + "/coas/get-all")
        Call<List<CoaData>> getAllCoas();

        // CoA entry queries
        @GET(MODULE_PREFIX + "/coa-entries/get-by-id")
        Call<CoaEntryData> getByCoaEntryId(@Query("coaEntryId") CoaEntryId coaEntryId);

        // CoA queries
        @GET(MODULE_PREFIX + "/coas/get-by-id")
        Call<CoaData> getByCoaId(@Query("coaId") CoaId coaId);

        @GET(MODULE_PREFIX + "/coa-entries/get-by-name-contains")
        Call<List<CoaEntryData>> getCoaEntriesByNameContains(@Query("name") String name);

        @GET(MODULE_PREFIX + "/coas/get-by-name-contains")
        Call<List<CoaData>> getCoasByNameContains(@Query("name") String name);

        @GET(MODULE_PREFIX + "/coa-entries/get-by-category")
        Call<List<CoaEntryData>> getEntriesByCategory(
            @Query("category") ChartEntryCategory category);

        @GET(MODULE_PREFIX + "/coa-entries/get-by-coa-id")
        Call<List<CoaEntryData>> getEntriesByCoaId(@Query("coaId") CoaId coaId);

    }

    interface DefinitionCommand {

        @POST(MODULE_PREFIX + "/flow-definitions/activate-flow-definition")
        Call<ActivateFlowDefinitionCommand.Output> activate(
            @Body ActivateFlowDefinitionCommand.Input input);

        @POST(MODULE_PREFIX + "/flow-definitions/add-flow-line")
        Call<AddFlowLineCommand.Output> addFlowLine(
            @Body AddFlowLineCommand.Input input);

        @POST(MODULE_PREFIX + "/flow-definitions/change-flow-definition-currency")
        Call<ChangeFlowDefinitionCurrencyCommand.Output> changeCurrency(
            @Body ChangeFlowDefinitionCurrencyCommand.Input input);

        @POST(MODULE_PREFIX + "/flow-definitions/change-flow-definition-properties")
        Call<ChangeFlowDefinitionPropertiesCommand.Output> changeProperties(
            @Body ChangeFlowDefinitionPropertiesCommand.Input input);

        @POST(MODULE_PREFIX + "/flow-definitions/create-flow-definition")
        Call<CreateFlowDefinitionCommand.Output> create(
            @Body CreateFlowDefinitionCommand.Input input);

        @POST(MODULE_PREFIX + "/flow-definitions/deactivate-flow-definition")
        Call<DeactivateFlowDefinitionCommand.Output> deactivate(
            @Body DeactivateFlowDefinitionCommand.Input input);

        @POST(MODULE_PREFIX + "/flow-definitions/remove-flow-line")
        Call<RemoveFlowLineCommand.Output> removeFlowLine(
            @Body RemoveFlowLineCommand.Input input);

        @POST(MODULE_PREFIX + "/flow-definitions/terminate-flow-definition")
        Call<TerminateFlowDefinitionCommand.Output> terminate(
            @Body TerminateFlowDefinitionCommand.Input input);

    }

    interface DefinitionQuery {

        @GET(MODULE_PREFIX + "/flow-definitions/get-all")
        Call<List<FlowDefinitionData>> getAllFlowDefinitions();

        @GET(MODULE_PREFIX + "/flow-definitions/get-by-id")
        Call<FlowDefinitionData> getByFlowDefinitionId(
            @Query("flowDefinitionId") FlowDefinitionId flowDefinitionId);

        @GET(MODULE_PREFIX + "/flow-definitions/get-by-name-contains")
        Call<List<FlowDefinitionData>> getFlowDefinitionsByNameContains(@Query("name") String name);

    }

    interface LedgerCommand {

        @POST(MODULE_PREFIX + "/ledgers/post-ledger-flow")
        Call<PostLedgerFlowCommand.Output> postLedgerFlow(@Body PostLedgerFlowCommand.Input input);

    }

    record Settings(String baseUrl) { }

}
