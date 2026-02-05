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
package org.mojave.core.accounting.intercom.client.service;

import org.mojave.component.misc.query.PagedResult;
import org.mojave.core.accounting.contract.command.ledger.PostLedgerFlowCommand;
import org.mojave.core.accounting.contract.data.AccountData;
import org.mojave.core.accounting.contract.data.ChartData;
import org.mojave.core.accounting.contract.data.ChartEntryData;
import org.mojave.core.accounting.contract.data.FlowDefinitionData;
import org.mojave.common.datatype.enums.accounting.ChartEntryCategory;
import org.mojave.common.datatype.identifier.accounting.AccountId;
import org.mojave.common.datatype.identifier.accounting.AccountOwnerId;
import org.mojave.common.datatype.identifier.accounting.ChartEntryId;
import org.mojave.common.datatype.identifier.accounting.ChartId;
import org.mojave.common.datatype.identifier.accounting.FlowDefinitionId;
import org.mojave.common.datatype.type.accounting.AccountCode;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AccountingIntercomService {

    String MODULE_PREFIX = "/accounting-intercom";

    interface AccountQuery {

        @POST(MODULE_PREFIX + "/accounts/find-accounts")
        Call<PagedResult<AccountData>> find(
            @Body org.mojave.core.accounting.contract.query.AccountQuery.Criteria criteria);

        @GET(MODULE_PREFIX + "/accounts/get-all-accounts")
        Call<java.util.List<AccountData>> getAll();

        @GET(MODULE_PREFIX + "/accounts/get-by-account-code")
        Call<AccountData> getByAccountCode(@Query("accountCode") AccountCode accountCode);

        @GET(MODULE_PREFIX + "/accounts/get-by-account-id")
        Call<AccountData> getByAccountId(@Query("accountId") AccountId accountId);

        @GET(MODULE_PREFIX + "/accounts/get-by-owner-id")
        Call<java.util.List<AccountData>> getByOwnerId(@Query("ownerId") AccountOwnerId ownerId);

    }

    interface ChartQuery {

        @GET(MODULE_PREFIX + "/chart-entries/get-all-chart-entries")
        Call<java.util.List<ChartEntryData>> getAllChartEntries();

        @GET(MODULE_PREFIX + "/charts/get-all-charts")
        Call<java.util.List<ChartData>> getAllCharts();

        // Chart entry queries
        @GET(MODULE_PREFIX + "/chart-entries/get-by-chart-entry-id")
        Call<ChartEntryData> getByChartEntryId(@Query("chartEntryId") ChartEntryId chartEntryId);

        // Chart queries
        @GET(MODULE_PREFIX + "/charts/get-by-chart-id")
        Call<ChartData> getByChartId(@Query("chartId") ChartId chartId);

        @GET(MODULE_PREFIX + "/chart-entries/get-by-name-contains")
        Call<java.util.List<ChartEntryData>> getChartEntriesByNameContains(
            @Query("name") String name);

        @GET(MODULE_PREFIX + "/charts/get-by-name-contains")
        Call<java.util.List<ChartData>> getChartsByNameContains(@Query("name") String name);

        @GET(MODULE_PREFIX + "/chart-entries/get-by-category")
        Call<java.util.List<ChartEntryData>> getEntriesByCategory(
            @Query("category") ChartEntryCategory category);

        @GET(MODULE_PREFIX + "/chart-entries/get-by-chart-id")
        Call<java.util.List<ChartEntryData>> getEntriesByChartId(@Query("chartId") ChartId chartId);

    }

    interface DefinitionQuery {

        @GET(MODULE_PREFIX + "/flow-definitions/get-all-flow-definitions")
        Call<java.util.List<FlowDefinitionData>> getAllFlowDefinitions();

        @GET(MODULE_PREFIX + "/flow-definitions/get-by-flow-definition-id")
        Call<FlowDefinitionData> getByFlowDefinitionId(
            @Query("flowDefinitionId") FlowDefinitionId flowDefinitionId);

        @GET(MODULE_PREFIX + "/flow-definitions/get-by-name-contains")
        Call<java.util.List<FlowDefinitionData>> getFlowDefinitionsByNameContains(
            @Query("name") String name);

    }

    interface LedgerCommand {

        @POST(MODULE_PREFIX + "/ledgers/post-ledger-flow")
        Call<PostLedgerFlowCommand.Output> postLedgerFlow(@Body PostLedgerFlowCommand.Input input);

    }

    record Settings(String baseUrl) { }

}
