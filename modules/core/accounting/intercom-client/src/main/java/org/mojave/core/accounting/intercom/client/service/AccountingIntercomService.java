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

import org.mojave.common.datatype.enums.accounting.ChartEntryCategory;
import org.mojave.common.datatype.identifier.accounting.AccountId;
import org.mojave.common.datatype.identifier.accounting.AccountOwnerId;
import org.mojave.common.datatype.identifier.accounting.CoaEntryId;
import org.mojave.common.datatype.identifier.accounting.CoaId;
import org.mojave.common.datatype.identifier.accounting.FlowDefinitionId;
import org.mojave.common.datatype.type.accounting.AccountCode;
import org.mojave.component.misc.query.PagedResult;
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

public interface AccountingIntercomService {

    String MODULE_PREFIX = "/accounting";

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
