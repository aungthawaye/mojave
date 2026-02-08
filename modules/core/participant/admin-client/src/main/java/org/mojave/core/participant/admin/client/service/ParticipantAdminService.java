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

package org.mojave.core.participant.admin.client.service;

import org.mojave.core.participant.contract.command.fsp.ActivateEndpointCommand;
import org.mojave.core.participant.contract.command.fsp.ActivateFspCommand;
import org.mojave.core.participant.contract.command.fsp.ActivateFspCurrencyCommand;
import org.mojave.core.participant.contract.command.fsp.AddEndpointCommand;
import org.mojave.core.participant.contract.command.fsp.AddFspCurrencyCommand;
import org.mojave.core.participant.contract.command.fsp.ChangeFspEndpointCommand;
import org.mojave.core.participant.contract.command.fsp.ChangeFspNameCommand;
import org.mojave.core.participant.contract.command.fsp.CreateFspCommand;
import org.mojave.core.participant.contract.command.fsp.CreateFspGroupCommand;
import org.mojave.core.participant.contract.command.fsp.DeactivateEndpointCommand;
import org.mojave.core.participant.contract.command.fsp.DeactivateFspCommand;
import org.mojave.core.participant.contract.command.fsp.DeactivateFspCurrencyCommand;
import org.mojave.core.participant.contract.command.fsp.JoinFspGroupCommand;
import org.mojave.core.participant.contract.command.fsp.LeaveFspGroupCommand;
import org.mojave.core.participant.contract.command.fsp.RemoveFspGroupCommand;
import org.mojave.core.participant.contract.command.hub.ActivateHubCurrencyCommand;
import org.mojave.core.participant.contract.command.hub.AddHubCurrencyCommand;
import org.mojave.core.participant.contract.command.hub.ChangeHubNameCommand;
import org.mojave.core.participant.contract.command.hub.CreateHubCommand;
import org.mojave.core.participant.contract.command.hub.DeactivateHubCurrencyCommand;
import org.mojave.core.participant.contract.command.oracle.ActivateOracleCommand;
import org.mojave.core.participant.contract.command.oracle.ChangeOracleNameCommand;
import org.mojave.core.participant.contract.command.oracle.ChangeOracleTypeCommand;
import org.mojave.core.participant.contract.command.oracle.CreateOracleCommand;
import org.mojave.core.participant.contract.command.oracle.DeactivateOracleCommand;
import org.mojave.core.participant.contract.command.ssp.ActivateSspCommand;
import org.mojave.core.participant.contract.command.ssp.ActivateSspCurrencyCommand;
import org.mojave.core.participant.contract.command.ssp.AddSspCurrencyCommand;
import org.mojave.core.participant.contract.command.ssp.ChangeSspEndpointCommand;
import org.mojave.core.participant.contract.command.ssp.ChangeSspNameCommand;
import org.mojave.core.participant.contract.command.ssp.CreateSspCommand;
import org.mojave.core.participant.contract.command.ssp.DeactivateSspCommand;
import org.mojave.core.participant.contract.command.ssp.DeactivateSspCurrencyCommand;
import org.mojave.core.participant.contract.command.ssp.TerminateSspCommand;
import org.mojave.core.participant.contract.data.FspData;
import org.mojave.core.participant.contract.data.FspGroupData;
import org.mojave.core.participant.contract.data.HubData;
import org.mojave.core.participant.contract.data.OracleData;
import org.mojave.core.participant.contract.data.SspData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.List;

public interface ParticipantAdminService {

    String MODULE_PREFIX = "/participant";

    interface FspGroupCommand {

        @POST(MODULE_PREFIX + "/fsp-groups/create-fsp-group")
        Call<CreateFspGroupCommand.Output> createFspGroup(@Body CreateFspGroupCommand.Input input);

    }

    interface FspCommand {

        @POST(MODULE_PREFIX + "/fsps/activate-endpoint")
        Call<ActivateEndpointCommand.Output> activateEndpoint(
            @Body ActivateEndpointCommand.Input input);

        @POST(MODULE_PREFIX + "/fsps/activate-fsp")
        Call<ActivateFspCommand.Output> activateFsp(@Body ActivateFspCommand.Input input);

        @POST(MODULE_PREFIX + "/fsps/activate-currency")
        Call<ActivateFspCurrencyCommand.Output> activateFspCurrency(
            @Body ActivateFspCurrencyCommand.Input input);

        @POST(MODULE_PREFIX + "/fsps/add-endpoint")
        Call<AddEndpointCommand.Output> addEndpoint(@Body AddEndpointCommand.Input input);

        @POST(MODULE_PREFIX + "/fsps/add-currency")
        Call<AddFspCurrencyCommand.Output> addFspCurrency(@Body AddFspCurrencyCommand.Input input);

        @POST(MODULE_PREFIX + "/fsps/change-endpoint")
        Call<ChangeFspEndpointCommand.Output> changeEndpoint(
            @Body ChangeFspEndpointCommand.Input input);

        @POST(MODULE_PREFIX + "/fsps/change-name")
        Call<ChangeFspNameCommand.Output> changeFspName(@Body ChangeFspNameCommand.Input input);

        @POST(MODULE_PREFIX + "/fsps/create-fsp")
        Call<CreateFspCommand.Output> createFsp(@Body CreateFspCommand.Input input);

        @POST(MODULE_PREFIX + "/fsps/deactivate-endpoint")
        Call<DeactivateEndpointCommand.Output> deactivateEndpoint(
            @Body DeactivateEndpointCommand.Input input);

        @POST(MODULE_PREFIX + "/fsps/deactivate-fsp")
        Call<DeactivateFspCommand.Output> deactivateFsp(@Body DeactivateFspCommand.Input input);

        @POST(MODULE_PREFIX + "/fsps/deactivate-currency")
        Call<DeactivateFspCurrencyCommand.Output> deactivateFspCurrency(
            @Body DeactivateFspCurrencyCommand.Input input);

        @POST(MODULE_PREFIX + "/fsps/join-fsp-group")
        Call<JoinFspGroupCommand.Output> joinFspGroup(@Body JoinFspGroupCommand.Input input);

        @POST(MODULE_PREFIX + "/fsps/leave-fsp-group")
        Call<LeaveFspGroupCommand.Output> leaveFspGroup(@Body LeaveFspGroupCommand.Input input);

        @POST(MODULE_PREFIX + "/fsps/remove-fsp-group")
        Call<RemoveFspGroupCommand.Output> removeFspGroup(@Body RemoveFspGroupCommand.Input input);

    }

    interface SspCommand {

        @POST(MODULE_PREFIX + "/ssps/activate-ssp")
        Call<ActivateSspCommand.Output> activateSsp(@Body ActivateSspCommand.Input input);

        @POST(MODULE_PREFIX + "/ssps/activate-currency")
        Call<ActivateSspCurrencyCommand.Output> activateSspCurrency(
            @Body ActivateSspCurrencyCommand.Input input);

        @POST(MODULE_PREFIX + "/ssps/add-currency")
        Call<AddSspCurrencyCommand.Output> addSspCurrency(@Body AddSspCurrencyCommand.Input input);

        @POST(MODULE_PREFIX + "/ssps/change-endpoint")
        Call<ChangeSspEndpointCommand.Output> changeSspEndpoint(
            @Body ChangeSspEndpointCommand.Input input);

        @POST(MODULE_PREFIX + "/ssps/change-name")
        Call<ChangeSspNameCommand.Output> changeSspName(@Body ChangeSspNameCommand.Input input);

        @POST(MODULE_PREFIX + "/ssps/create-ssp")
        Call<CreateSspCommand.Output> createSsp(@Body CreateSspCommand.Input input);

        @POST(MODULE_PREFIX + "/ssps/deactivate-ssp")
        Call<DeactivateSspCommand.Output> deactivateSsp(@Body DeactivateSspCommand.Input input);

        @POST(MODULE_PREFIX + "/ssps/deactivate-currency")
        Call<DeactivateSspCurrencyCommand.Output> deactivateSspCurrency(
            @Body DeactivateSspCurrencyCommand.Input input);

        @POST(MODULE_PREFIX + "/ssps/terminate-ssp")
        Call<TerminateSspCommand.Output> terminateSsp(@Body TerminateSspCommand.Input input);

    }

    interface OracleQuery {

        @GET(MODULE_PREFIX + "/oracles/get-all")
        Call<List<OracleData>> getAllOracles();

        @GET(MODULE_PREFIX + "/oracles/get-by-id")
        Call<OracleData> getByOracleId(@Query("oracleId") String oracleId);

        @GET(MODULE_PREFIX + "/oracles/get-by-party-id-type")
        Call<OracleData> getByPartyIdType(@Query("partyIdType") String partyIdType);

    }

    interface FspQuery {

        @GET(MODULE_PREFIX + "/fsps/get-all")
        Call<List<FspData>> getAllFsps();

        @GET(MODULE_PREFIX + "/fsps/get-by-code")
        Call<FspData> getByFspCode(@Query("code") String fspCode);

        @GET(MODULE_PREFIX + "/fsps/get-by-id")
        Call<FspData> getByFspId(@Query("fspId") String fspId);

    }

    interface FspGroupQuery {

        @GET(MODULE_PREFIX + "/fsp-groups/get-all")
        Call<List<FspGroupData>> getAllFspGroups();

        @GET(MODULE_PREFIX + "/fsp-groups/get-by-id")
        Call<FspGroupData> getByFspGroupId(@Query("fspGroupId") String fspGroupId);

    }

    interface SspQuery {

        @GET(MODULE_PREFIX + "/ssps/get-all")
        Call<List<SspData>> getAllSsps();

        @GET(MODULE_PREFIX + "/ssps/get-by-code")
        Call<SspData> getBySspCode(@Query("code") String sspCode);

        @GET(MODULE_PREFIX + "/ssps/get-by-id")
        Call<SspData> getBySspId(@Query("sspId") String sspId);

    }

    interface OracleCommands {

        @POST(MODULE_PREFIX + "/oracles/activate-oracle")
        Call<ActivateOracleCommand.Output> activateOracle(@Body ActivateOracleCommand.Input input);

        @POST(MODULE_PREFIX + "/oracles/change-name")
        Call<ChangeOracleNameCommand.Output> changeOracleName(
            @Body ChangeOracleNameCommand.Input input);

        @POST(MODULE_PREFIX + "/oracles/change-type")
        Call<ChangeOracleTypeCommand.Output> changeOracleType(
            @Body ChangeOracleTypeCommand.Input input);

        @POST(MODULE_PREFIX + "/oracles/create-oracle")
        Call<CreateOracleCommand.Output> createOracle(@Body CreateOracleCommand.Input input);

        @POST(MODULE_PREFIX + "/oracles/deactivate-oracle")
        Call<DeactivateOracleCommand.Output> deactivateOracle(
            @Body DeactivateOracleCommand.Input input);

    }

    interface HubCommands {

        @POST(MODULE_PREFIX + "/hubs/activate-currency")
        Call<ActivateHubCurrencyCommand.Output> activateHubCurrency(
            @Body ActivateHubCurrencyCommand.Input input);

        @POST(MODULE_PREFIX + "/hubs/add-currency")
        Call<AddHubCurrencyCommand.Output> addHubCurrency(@Body AddHubCurrencyCommand.Input input);

        @POST(MODULE_PREFIX + "/hubs/change-name")
        Call<ChangeHubNameCommand.Output> changeHubName(@Body ChangeHubNameCommand.Input input);

        @POST(MODULE_PREFIX + "/hubs/create-hub")
        Call<CreateHubCommand.Output> createHub(@Body CreateHubCommand.Input input);

        @POST(MODULE_PREFIX + "/hubs/deactivate-currency")
        Call<DeactivateHubCurrencyCommand.Output> deactivateHubCurrency(
            @Body DeactivateHubCurrencyCommand.Input input);

    }

    interface HubQuery {

        @GET(MODULE_PREFIX + "/hubs/count")
        Call<Long> count();

        @GET(MODULE_PREFIX + "/hubs/get")
        Call<HubData> get();

        @GET(MODULE_PREFIX + "/hubs/get-all")
        Call<List<HubData>> getAll();

    }

    record Settings(String baseUrl) { }

}
