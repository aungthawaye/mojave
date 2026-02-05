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

package org.mojave.core.settlement.admin.client.service;

import org.mojave.core.settlement.contract.command.definition.ActivateSettlementDefinitionCommand;
import org.mojave.core.settlement.contract.command.definition.AddFilterItemCommand;
import org.mojave.core.settlement.contract.command.definition.CreateFilterGroupCommand;
import org.mojave.core.settlement.contract.command.definition.CreateSettlementDefinitionCommand;
import org.mojave.core.settlement.contract.command.definition.DeactivateSettlementDefinitionCommand;
import org.mojave.core.settlement.contract.command.definition.FindSettlementProviderCommand;
import org.mojave.core.settlement.contract.command.definition.RemoveFilterGroupCommand;
import org.mojave.core.settlement.contract.command.definition.RemoveFilterItemCommand;
import org.mojave.core.settlement.contract.command.definition.RemoveSettlementDefinitionCommand;
import org.mojave.core.settlement.contract.command.definition.UpdateSettlementDefinitionCommand;
import org.mojave.core.settlement.contract.command.record.CompleteSettlementCommand;
import org.mojave.core.settlement.contract.command.record.InitiateSettlementProcessCommand;
import org.mojave.core.settlement.contract.command.record.RequestSettlementInitiationCommand;
import org.mojave.core.settlement.contract.command.record.UpdatePreparationResultCommand;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SettlementAdminService {

    String MODULE_PREFIX = "/settlement";

    interface DefinitionCommand {

        @POST(MODULE_PREFIX + "/settlement-definitions/activate")
        Call<ActivateSettlementDefinitionCommand.Output> activate(
            @Body ActivateSettlementDefinitionCommand.Input input);

        @POST(MODULE_PREFIX + "/settlement-definitions/create")
        Call<CreateSettlementDefinitionCommand.Output> create(
            @Body CreateSettlementDefinitionCommand.Input input);

        @POST(MODULE_PREFIX + "/settlement-definitions/deactivate")
        Call<DeactivateSettlementDefinitionCommand.Output> deactivate(
            @Body DeactivateSettlementDefinitionCommand.Input input);

        @POST(MODULE_PREFIX + "/settlement-definitions/find-provider")
        Call<FindSettlementProviderCommand.Output> findProvider(
            @Body FindSettlementProviderCommand.Input input);

        @POST(MODULE_PREFIX + "/settlement-definitions/remove")
        Call<RemoveSettlementDefinitionCommand.Output> remove(
            @Body RemoveSettlementDefinitionCommand.Input input);

        @POST(MODULE_PREFIX + "/settlement-definitions/update")
        Call<UpdateSettlementDefinitionCommand.Output> update(
            @Body UpdateSettlementDefinitionCommand.Input input);

    }

    interface FilterGroupCommand {

        @POST(MODULE_PREFIX + "/filter-groups/add-item")
        Call<AddFilterItemCommand.Output> addItem(@Body AddFilterItemCommand.Input input);

        @POST(MODULE_PREFIX + "/filter-groups/create")
        Call<CreateFilterGroupCommand.Output> create(@Body CreateFilterGroupCommand.Input input);

        @POST(MODULE_PREFIX + "/filter-groups/remove")
        Call<RemoveFilterGroupCommand.Output> remove(@Body RemoveFilterGroupCommand.Input input);

        @POST(MODULE_PREFIX + "/filter-groups/remove-item")
        Call<RemoveFilterItemCommand.Output> removeItem(@Body RemoveFilterItemCommand.Input input);

    }

    interface RecordCommand {

        @POST(MODULE_PREFIX + "/settlement-records/complete")
        Call<CompleteSettlementCommand.Output> complete(
            @Body CompleteSettlementCommand.Input input);

        @POST(MODULE_PREFIX + "/settlement-records/initiate-process")
        Call<InitiateSettlementProcessCommand.Output> initiateProcess(
            @Body InitiateSettlementProcessCommand.Input input);

        @POST(MODULE_PREFIX + "/settlement-records/request-initiation")
        Call<RequestSettlementInitiationCommand.Output> requestInitiation(
            @Body RequestSettlementInitiationCommand.Input input);

        @POST(MODULE_PREFIX + "/settlement-records/update-preparation")
        Call<UpdatePreparationResultCommand.Output> updatePreparation(
            @Body UpdatePreparationResultCommand.Input input);

    }

    record Settings(String baseUrl) { }

}
