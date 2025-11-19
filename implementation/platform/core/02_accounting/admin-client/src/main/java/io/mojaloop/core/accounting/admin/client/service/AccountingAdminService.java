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
/*-
 * ==============================================================================
 * Mojave
 * -----------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * -----------------------------------------------------------------------------
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
 * ==============================================================================
 */

package io.mojaloop.core.accounting.admin.client.service;

import io.mojaloop.component.misc.query.PagedResult;
import io.mojaloop.core.accounting.contract.command.account.ActivateAccountCommand;
import io.mojaloop.core.accounting.contract.command.account.ChangeAccountPropertiesCommand;
import io.mojaloop.core.accounting.contract.command.account.CreateAccountCommand;
import io.mojaloop.core.accounting.contract.command.account.DeactivateAccountCommand;
import io.mojaloop.core.accounting.contract.command.account.TerminateAccountCommand;
import io.mojaloop.core.accounting.contract.command.chart.ChangeChartEntryPropertiesCommand;
import io.mojaloop.core.accounting.contract.command.chart.ChangeChartNameCommand;
import io.mojaloop.core.accounting.contract.command.chart.CreateChartCommand;
import io.mojaloop.core.accounting.contract.command.chart.CreateChartEntryCommand;
import io.mojaloop.core.accounting.contract.command.definition.ActivateFlowDefinitionCommand;
import io.mojaloop.core.accounting.contract.command.definition.AddPostingDefinitionCommand;
import io.mojaloop.core.accounting.contract.command.definition.ChangeFlowDefinitionCurrencyCommand;
import io.mojaloop.core.accounting.contract.command.definition.ChangeFlowDefinitionPropertiesCommand;
import io.mojaloop.core.accounting.contract.command.definition.CreateFlowDefinitionCommand;
import io.mojaloop.core.accounting.contract.command.definition.DeactivateFlowDefinitionCommand;
import io.mojaloop.core.accounting.contract.command.definition.RemovePostingDefinitionCommand;
import io.mojaloop.core.accounting.contract.command.definition.TerminateFlowDefinitionCommand;
import io.mojaloop.core.accounting.contract.command.ledger.PostTransactionCommand;
import io.mojaloop.core.accounting.contract.data.AccountData;
import io.mojaloop.core.accounting.contract.data.ChartData;
import io.mojaloop.core.accounting.contract.data.ChartEntryData;
import io.mojaloop.core.accounting.contract.data.FlowDefinitionData;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountId;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountOwnerId;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartId;
import io.mojaloop.core.common.datatype.identifier.accounting.FlowDefinitionId;
import io.mojaloop.core.common.datatype.type.accounting.AccountCode;
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
        Call<ChangeAccountPropertiesCommand.Output> changeProperties(@Body ChangeAccountPropertiesCommand.Input input);

        @POST("/accounts")
        Call<CreateAccountCommand.Output> create(@Body CreateAccountCommand.Input input);

        @POST("/accounts/deactivate")
        Call<DeactivateAccountCommand.Output> deactivate(@Body DeactivateAccountCommand.Input input);

        @POST("/accounts/terminate")
        Call<TerminateAccountCommand.Output> terminate(@Body TerminateAccountCommand.Input input);

    }

    interface AccountQuery {

        @POST("/accounts/find-accounts")
        Call<PagedResult<AccountData>> find(@Body io.mojaloop.core.accounting.contract.query.AccountQuery.Criteria criteria);

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
        Call<ChangeChartEntryPropertiesCommand.Output> changeEntryProperties(@Body ChangeChartEntryPropertiesCommand.Input input);

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
        Call<java.util.List<ChartEntryData>> getChartEntriesByNameContains(@Query("name") String name);

        @GET("/charts/get-by-name-contains")
        Call<java.util.List<ChartData>> getChartsByNameContains(@Query("name") String name);

        @GET("/chart-entries/get-by-chart-id")
        Call<java.util.List<ChartEntryData>> getEntriesByChartId(@Query("chartId") ChartId chartId);

    }

    interface DefinitionCommand {

        @POST("/flow-definitions/activate")
        Call<ActivateFlowDefinitionCommand.Output> activate(@Body ActivateFlowDefinitionCommand.Input input);

        @POST("/flow-definitions/add-posting")
        Call<AddPostingDefinitionCommand.Output> addPosting(@Body AddPostingDefinitionCommand.Input input);

        @POST("/flow-definitions/change-currency")
        Call<ChangeFlowDefinitionCurrencyCommand.Output> changeCurrency(@Body ChangeFlowDefinitionCurrencyCommand.Input input);

        @POST("/flow-definitions/change-properties")
        Call<ChangeFlowDefinitionPropertiesCommand.Output> changeProperties(@Body ChangeFlowDefinitionPropertiesCommand.Input input);

        @POST("/flow-definitions/create")
        Call<CreateFlowDefinitionCommand.Output> create(@Body CreateFlowDefinitionCommand.Input input);

        @POST("/flow-definitions/deactivate")
        Call<DeactivateFlowDefinitionCommand.Output> deactivate(@Body DeactivateFlowDefinitionCommand.Input input);

        @POST("/flow-definitions/remove-posting")
        Call<RemovePostingDefinitionCommand.Output> removePosting(@Body RemovePostingDefinitionCommand.Input input);

        @POST("/flow-definitions/terminate")
        Call<TerminateFlowDefinitionCommand.Output> terminate(@Body TerminateFlowDefinitionCommand.Input input);

    }

    interface DefinitionQuery {

        @GET("/flow-definitions/get-all-flow-definitions")
        Call<java.util.List<FlowDefinitionData>> getAllFlowDefinitions();

        @GET("/flow-definitions/get-by-flow-definition-id")
        Call<FlowDefinitionData> getByFlowDefinitionId(@Query("flowDefinitionId") FlowDefinitionId flowDefinitionId);

        @GET("/flow-definitions/get-by-name-contains")
        Call<java.util.List<FlowDefinitionData>> getFlowDefinitionsByNameContains(@Query("name") String name);

    }
    
    interface LedgerCommand {
        
        @POST("/ledgers/post-transaction")
        Call<PostTransactionCommand.Output> postTransaction(@Body PostTransactionCommand.Input input);
        
    }

    record Settings(String baseUrl) { }

}
