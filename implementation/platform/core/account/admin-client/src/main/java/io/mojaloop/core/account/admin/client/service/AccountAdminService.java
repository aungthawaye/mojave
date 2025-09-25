/*-
 * ================================================================================
 * Mojaloop OSS
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
 * Mojaloop OSS
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

package io.mojaloop.core.account.admin.client.service;

import io.mojaloop.core.account.contract.command.account.ActivateAccountCommand;
import io.mojaloop.core.account.contract.command.account.ChangeAccountPropertiesCommand;
import io.mojaloop.core.account.contract.command.account.CreateAccountCommand;
import io.mojaloop.core.account.contract.command.account.DeactivateAccountCommand;
import io.mojaloop.core.account.contract.command.account.TerminateAccountCommand;
import io.mojaloop.core.account.contract.command.chart.ChangeChartEntryPropertiesCommand;
import io.mojaloop.core.account.contract.command.chart.ChangeChartNameCommand;
import io.mojaloop.core.account.contract.command.chart.CreateChartCommand;
import io.mojaloop.core.account.contract.command.chart.CreateChartEntryCommand;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AccountAdminService {

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

    record Settings(String baseUrl) { }

}
