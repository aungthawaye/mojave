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
package org.mojave.core.accounting.admin.client.service;

import org.mojave.component.misc.query.PagedResult;
import org.mojave.core.accounting.contract.command.account.ActivateAccountCommand;
import org.mojave.core.accounting.contract.command.account.ChangeAccountPropertiesCommand;
import org.mojave.core.accounting.contract.command.account.CreateAccountCommand;
import org.mojave.core.accounting.contract.command.account.CreateAccountInBulkCommand;
import org.mojave.core.accounting.contract.command.account.DeactivateAccountCommand;
import org.mojave.core.accounting.contract.command.account.TerminateAccountCommand;
import org.mojave.core.accounting.contract.command.chart.ChangeChartEntryPropertiesCommand;
import org.mojave.core.accounting.contract.command.chart.ChangeChartNameCommand;
import org.mojave.core.accounting.contract.command.chart.CreateChartCommand;
import org.mojave.core.accounting.contract.command.chart.CreateChartEntryCommand;
import org.mojave.core.accounting.contract.command.definition.ActivateFlowDefinitionCommand;
import org.mojave.core.accounting.contract.command.definition.AddPostingDefinitionCommand;
import org.mojave.core.accounting.contract.command.definition.ChangeFlowDefinitionCurrencyCommand;
import org.mojave.core.accounting.contract.command.definition.ChangeFlowDefinitionPropertiesCommand;
import org.mojave.core.accounting.contract.command.definition.CreateFlowDefinitionCommand;
import org.mojave.core.accounting.contract.command.definition.DeactivateFlowDefinitionCommand;
import org.mojave.core.accounting.contract.command.definition.RemovePostingDefinitionCommand;
import org.mojave.core.accounting.contract.command.definition.TerminateFlowDefinitionCommand;
import org.mojave.core.accounting.contract.command.ledger.PostLedgerFlowCommand;
import org.mojave.core.accounting.contract.data.AccountData;
import org.mojave.core.accounting.contract.data.ChartData;
import org.mojave.core.accounting.contract.data.ChartEntryData;
import org.mojave.core.accounting.contract.data.FlowDefinitionData;
import org.mojave.core.common.datatype.enums.accounting.ChartEntryCategory;
import org.mojave.core.common.datatype.identifier.accounting.AccountId;
import org.mojave.core.common.datatype.identifier.accounting.AccountOwnerId;
import org.mojave.core.common.datatype.identifier.accounting.ChartEntryId;
import org.mojave.core.common.datatype.identifier.accounting.ChartId;
import org.mojave.core.common.datatype.identifier.accounting.FlowDefinitionId;
import org.mojave.core.common.datatype.type.accounting.AccountCode;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AccountingAdminService {

    interface AccountCommand {

        @POST("/accounts/activate")
        Call<ActivateAccountCommand.Output> activate(@Body ActivateAccountCommand.Input input);

        @POST("/accounts/change-properties")
        Call<ChangeAccountPropertiesCommand.Output> changeProperties(
            @Body ChangeAccountPropertiesCommand.Input input);

        @POST("/accounts/create")
        Call<CreateAccountCommand.Output> create(@Body CreateAccountCommand.Input input);

        @POST("/accounts/create-in-bulk")
        Call<CreateAccountInBulkCommand.Output> createInBulk(
            @Body CreateAccountInBulkCommand.Input input);

        @POST("/accounts/deactivate")
        Call<DeactivateAccountCommand.Output> deactivate(
            @Body DeactivateAccountCommand.Input input);

        @POST("/accounts/terminate")
        Call<TerminateAccountCommand.Output> terminate(@Body TerminateAccountCommand.Input input);

    }

    interface AccountQuery {

        @POST("/accounts/find-accounts")
        Call<PagedResult<AccountData>> find(
            @Body org.mojave.core.accounting.contract.query.AccountQuery.Criteria criteria);

        @GET("/accounts/get-all-accounts")
        Call<java.util.List<AccountData>> getAll();

        @GET("/accounts/get-by-account-code")
        Call<AccountData> getByAccountCode(@Query("accountCode") AccountCode accountCode);

        @GET("/accounts/get-by-account-id")
        Call<AccountData> getByAccountId(@Query("accountId") AccountId accountId);

        @GET("/accounts/get-by-owner-id")
        Call<java.util.List<AccountData>> getByOwnerId(@Query("ownerId") AccountOwnerId ownerId);

    }

    interface ChartCommand {

        @POST("/chart-entries/change-properties")
        Call<ChangeChartEntryPropertiesCommand.Output> changeEntryProperties(
            @Body ChangeChartEntryPropertiesCommand.Input input);

        @POST("/charts/change-name")
        Call<ChangeChartNameCommand.Output> changeName(@Body ChangeChartNameCommand.Input input);

        @POST("/charts/create")
        Call<CreateChartCommand.Output> create(@Body CreateChartCommand.Input input);

        @POST("/chart-entries/create")
        Call<CreateChartEntryCommand.Output> createEntry(@Body CreateChartEntryCommand.Input input);

    }

    interface ChartQuery {

        @GET("/chart-entries/get-all-chart-entries")
        Call<java.util.List<ChartEntryData>> getAllChartEntries();

        @GET("/charts/get-all-charts")
        Call<java.util.List<ChartData>> getAllCharts();

        // Chart entry queries
        @GET("/chart-entries/get-by-chart-entry-id")
        Call<ChartEntryData> getByChartEntryId(@Query("chartEntryId") ChartEntryId chartEntryId);

        // Chart queries
        @GET("/charts/get-by-chart-id")
        Call<ChartData> getByChartId(@Query("chartId") ChartId chartId);

        @GET("/chart-entries/get-by-name-contains")
        Call<java.util.List<ChartEntryData>> getChartEntriesByNameContains(
            @Query("name") String name);

        @GET("/charts/get-by-name-contains")
        Call<java.util.List<ChartData>> getChartsByNameContains(@Query("name") String name);

        @GET("/chart-entries/get-by-category")
        Call<java.util.List<ChartEntryData>> getEntriesByCategory(
            @Query("category") ChartEntryCategory category);

        @GET("/chart-entries/get-by-chart-id")
        Call<java.util.List<ChartEntryData>> getEntriesByChartId(@Query("chartId") ChartId chartId);

    }

    interface DefinitionCommand {

        @POST("/flow-definitions/activate")
        Call<ActivateFlowDefinitionCommand.Output> activate(
            @Body ActivateFlowDefinitionCommand.Input input);

        @POST("/flow-definitions/add-posting")
        Call<AddPostingDefinitionCommand.Output> addPosting(
            @Body AddPostingDefinitionCommand.Input input);

        @POST("/flow-definitions/change-currency")
        Call<ChangeFlowDefinitionCurrencyCommand.Output> changeCurrency(
            @Body ChangeFlowDefinitionCurrencyCommand.Input input);

        @POST("/flow-definitions/change-properties")
        Call<ChangeFlowDefinitionPropertiesCommand.Output> changeProperties(
            @Body ChangeFlowDefinitionPropertiesCommand.Input input);

        @POST("/flow-definitions/create")
        Call<CreateFlowDefinitionCommand.Output> create(
            @Body CreateFlowDefinitionCommand.Input input);

        @POST("/flow-definitions/deactivate")
        Call<DeactivateFlowDefinitionCommand.Output> deactivate(
            @Body DeactivateFlowDefinitionCommand.Input input);

        @POST("/flow-definitions/remove-posting")
        Call<RemovePostingDefinitionCommand.Output> removePosting(
            @Body RemovePostingDefinitionCommand.Input input);

        @POST("/flow-definitions/terminate")
        Call<TerminateFlowDefinitionCommand.Output> terminate(
            @Body TerminateFlowDefinitionCommand.Input input);

    }

    interface DefinitionQuery {

        @GET("/flow-definitions/get-all-flow-definitions")
        Call<java.util.List<FlowDefinitionData>> getAllFlowDefinitions();

        @GET("/flow-definitions/get-by-flow-definition-id")
        Call<FlowDefinitionData> getByFlowDefinitionId(
            @Query("flowDefinitionId") FlowDefinitionId flowDefinitionId);

        @GET("/flow-definitions/get-by-name-contains")
        Call<java.util.List<FlowDefinitionData>> getFlowDefinitionsByNameContains(
            @Query("name") String name);

    }

    interface LedgerCommand {

        @POST("/ledgers/post-ledger-flow")
        Call<PostLedgerFlowCommand.Output> postLedgerFlow(@Body PostLedgerFlowCommand.Input input);

    }

    record Settings(String baseUrl) { }

}
