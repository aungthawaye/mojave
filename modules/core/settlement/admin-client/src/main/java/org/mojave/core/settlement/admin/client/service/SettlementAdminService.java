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
import org.mojave.core.settlement.contract.command.definition.CreateSettlementDefinitionCommand;
import org.mojave.core.settlement.contract.command.definition.DeactivateSettlementDefinitionCommand;
import org.mojave.core.settlement.contract.command.definition.FindSettlementProviderCommand;
import org.mojave.core.settlement.contract.command.definition.RemoveSettlementDefinitionCommand;
import org.mojave.core.settlement.contract.command.definition.UpdateSettlementDefinitionCommand;
import org.mojave.core.settlement.contract.command.record.HandleSettlementCompletionCommand;
import org.mojave.core.settlement.contract.command.record.HandleSettlementPreparationCommand;
import org.mojave.core.settlement.contract.command.record.SendSettlementRequestCommand;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SettlementAdminService {

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

    interface RecordCommand {

        @POST(MODULE_PREFIX + "/settlement-records/complete-settlement")
        Call<HandleSettlementCompletionCommand.Output> complete(
            @Body HandleSettlementCompletionCommand.Input input);

        @POST(MODULE_PREFIX + "/settlement-records/initiate-settlement-process")
        Call<SendSettlementRequestCommand.Output> initiateProcess(
            @Body SendSettlementRequestCommand.Input input);

        @POST(MODULE_PREFIX + "/settlement-records/update-preparation-result")
        Call<HandleSettlementPreparationCommand.Output> updatePreparation(
            @Body HandleSettlementPreparationCommand.Input input);

    }

    record Settings(String baseUrl) { }

}
