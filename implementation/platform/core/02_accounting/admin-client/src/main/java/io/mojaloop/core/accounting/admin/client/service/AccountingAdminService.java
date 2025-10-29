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

import io.mojaloop.core.accounting.contract.command.account.ActivateAccountCommand;
import io.mojaloop.core.accounting.contract.command.account.ChangeAccountPropertiesCommand;
import io.mojaloop.core.accounting.contract.command.account.CreateAccountCommand;
import io.mojaloop.core.accounting.contract.command.account.DeactivateAccountCommand;
import io.mojaloop.core.accounting.contract.command.account.TerminateAccountCommand;
import io.mojaloop.core.accounting.contract.command.chart.ChangeChartEntryPropertiesCommand;
import io.mojaloop.core.accounting.contract.command.chart.ChangeChartNameCommand;
import io.mojaloop.core.accounting.contract.command.chart.CreateChartCommand;
import io.mojaloop.core.accounting.contract.command.chart.CreateChartEntryCommand;
import io.mojaloop.core.accounting.contract.command.definition.AddPostingDefinitionCommand;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AccountingAdminService {

    interface AccountCommands {

        @POST("accounts/activate")
        Call<Void> activateAccount(@Body ActivateAccountCommand.Input input);

        @POST("accounts/change-properties")
        Call<Void> changeAccountProperties(@Body ChangeAccountPropertiesCommand.Input input);

        @POST("accounts")
        Call<CreateAccountCommand.Output> createAccount(@Body CreateAccountCommand.Input input);

        @POST("accounts/deactivate")
        Call<Void> deactivateAccount(@Body DeactivateAccountCommand.Input input);

        @POST("accounts/terminate")
        Call<Void> terminateAccount(@Body TerminateAccountCommand.Input input);

    }

    interface ChartCommands {

        @POST("charts/entries/change-properties")
        Call<Void> changeChartEntryProperties(@Body ChangeChartEntryPropertiesCommand.Input input);

        @POST("charts/change-name")
        Call<Void> changeChartName(@Body ChangeChartNameCommand.Input input);

        @POST("charts")
        Call<CreateChartCommand.Output> createChart(@Body CreateChartCommand.Input input);

        @POST("charts/entries")
        Call<CreateChartEntryCommand.Output> createChartEntry(@Body CreateChartEntryCommand.Input input);

    }

    interface DefinitionCommands {

        @POST("definitions/flows/activate")
        Call<Void> activateFlowDefinition(
            @Body io.mojaloop.core.accounting.contract.command.definition.ActivateFlowDefinitionCommand.Input input);

        @POST("definitions/flows/add-postings")
        Call<Void> addFlowDefinitionPostings(@Body AddPostingDefinitionCommand.Input input);

        @POST("definitions/flows/change-currency")
        Call<Void> changeFlowDefinitionCurrency(
            @Body io.mojaloop.core.accounting.contract.command.definition.ChangeFlowDefinitionCurrencyCommand.Input input);

        @POST("definitions/flows/change-properties")
        Call<Void> changeFlowDefinitionProperties(
            @Body io.mojaloop.core.accounting.contract.command.definition.ChangeFlowDefinitionPropertiesCommand.Input input);

        @POST("definitions/flows")
        Call<io.mojaloop.core.accounting.contract.command.definition.CreateFlowDefinitionCommand.Output> createFlowDefinition(
            @Body io.mojaloop.core.accounting.contract.command.definition.CreateFlowDefinitionCommand.Input input);

        @POST("definitions/flows/deactivate")
        Call<Void> deactivateFlowDefinition(
            @Body io.mojaloop.core.accounting.contract.command.definition.DeactivateFlowDefinitionCommand.Input input);

        @POST("definitions/flows/remove-posting")
        Call<Void> removeFlowDefinitionPosting(
            @Body io.mojaloop.core.accounting.contract.command.definition.RemoveFlowDefinitionPostingCommand.Input input);

        @POST("definitions/flows/terminate")
        Call<Void> terminateFlowDefinition(
            @Body io.mojaloop.core.accounting.contract.command.definition.TerminateFlowDefinitionCommand.Input input);

    }

    record Settings(String baseUrl) { }

}
