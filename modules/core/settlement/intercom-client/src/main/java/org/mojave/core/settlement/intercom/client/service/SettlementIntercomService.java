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

package org.mojave.core.settlement.intercom.client.service;

import org.mojave.common.datatype.identifier.settlement.SettlementDefinitionId;
import org.mojave.common.datatype.identifier.settlement.SettlementRecordId;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.core.settlement.contract.command.definition.ActivateSettlementDefinitionCommand;
import org.mojave.core.settlement.contract.command.definition.CreateSettlementDefinitionCommand;
import org.mojave.core.settlement.contract.command.definition.DeactivateSettlementDefinitionCommand;
import org.mojave.core.settlement.contract.command.definition.FindSettlementProviderCommand;
import org.mojave.core.settlement.contract.command.definition.RemoveSettlementDefinitionCommand;
import org.mojave.core.settlement.contract.command.definition.UpdateSettlementDefinitionCommand;
import org.mojave.core.settlement.contract.command.record.HandleSettlementCompletionCommand;
import org.mojave.core.settlement.contract.command.record.HandleSettlementPreparationCommand;
import org.mojave.core.settlement.contract.command.record.InitiateSettlementProcessCommand;
import org.mojave.core.settlement.contract.data.SettlementDefinitionData;
import org.mojave.core.settlement.contract.data.SettlementRecordData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.List;

public interface SettlementIntercomService {

    String MODULE_PREFIX = "/settlement";

    interface DefinitionCommand {

        @POST(MODULE_PREFIX + "/settlement-definitions/activate-settlement-definition")
        Call<ActivateSettlementDefinitionCommand.Output> activate(
            @Body ActivateSettlementDefinitionCommand.Input input);

        @POST(MODULE_PREFIX + "/settlement-definitions/create-settlement-definition")
        Call<CreateSettlementDefinitionCommand.Output> create(
            @Body CreateSettlementDefinitionCommand.Input input);

        @POST(MODULE_PREFIX + "/settlement-definitions/deactivate-settlement-definition")
        Call<DeactivateSettlementDefinitionCommand.Output> deactivate(
            @Body DeactivateSettlementDefinitionCommand.Input input);

        @POST(MODULE_PREFIX + "/settlement-definitions/find-settlement-provider")
        Call<FindSettlementProviderCommand.Output> findProvider(
            @Body FindSettlementProviderCommand.Input input);

        @POST(MODULE_PREFIX + "/settlement-definitions/remove-settlement-definition")
        Call<RemoveSettlementDefinitionCommand.Output> remove(
            @Body RemoveSettlementDefinitionCommand.Input input);

        @POST(MODULE_PREFIX + "/settlement-definitions/update-settlement-definition")
        Call<UpdateSettlementDefinitionCommand.Output> update(
            @Body UpdateSettlementDefinitionCommand.Input input);

    }

    interface DefinitionQuery {

        @GET(MODULE_PREFIX + "/settlement-definitions/get-all")
        Call<List<SettlementDefinitionData>> getAllSettlementDefinitions();

        @GET(MODULE_PREFIX + "/settlement-definitions/get-by-id")
        Call<SettlementDefinitionData> getBySettlementDefinitionId(
            @Query("settlementDefinitionId") SettlementDefinitionId settlementDefinitionId);

    }

    interface RecordCommand {

        @POST(MODULE_PREFIX + "/settlement-records/complete-settlement")
        Call<HandleSettlementCompletionCommand.Output> complete(
            @Body HandleSettlementCompletionCommand.Input input);

        @POST(MODULE_PREFIX + "/settlement-records/initiate-settlement-process")
        Call<InitiateSettlementProcessCommand.Output> initiateProcess(
            @Body InitiateSettlementProcessCommand.Input input);

        @POST(MODULE_PREFIX + "/settlement-records/update-preparation-result")
        Call<HandleSettlementPreparationCommand.Output> updatePreparation(
            @Body HandleSettlementPreparationCommand.Input input);

    }

    interface RecordQuery {

        @GET(MODULE_PREFIX + "/settlement-records/get-all")
        Call<List<SettlementRecordData>> getAllSettlementRecords();

        @GET(MODULE_PREFIX + "/settlement-records/get-by-id")
        Call<SettlementRecordData> getBySettlementRecordId(
            @Query("settlementRecordId") SettlementRecordId settlementRecordId);

        @GET(MODULE_PREFIX + "/settlement-records/get-by-transaction-id")
        Call<List<SettlementRecordData>> getByTransactionId(
            @Query("transactionId") TransactionId transactionId);

    }

    record Settings(String baseUrl) { }

}
