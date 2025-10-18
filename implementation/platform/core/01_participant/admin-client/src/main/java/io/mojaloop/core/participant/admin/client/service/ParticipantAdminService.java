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

package io.mojaloop.core.participant.admin.client.service;

import io.mojaloop.core.participant.contract.command.fsp.ActivateEndpointCommand;
import io.mojaloop.core.participant.contract.command.fsp.ActivateFspCommand;
import io.mojaloop.core.participant.contract.command.fsp.ActivateFspCurrencyCommand;
import io.mojaloop.core.participant.contract.command.fsp.AddEndpointCommand;
import io.mojaloop.core.participant.contract.command.fsp.AddFspCurrencyCommand;
import io.mojaloop.core.participant.contract.command.fsp.ChangeEndpointCommand;
import io.mojaloop.core.participant.contract.command.fsp.ChangeFspNameCommand;
import io.mojaloop.core.participant.contract.command.fsp.CreateFspCommand;
import io.mojaloop.core.participant.contract.command.fsp.DeactivateEndpointCommand;
import io.mojaloop.core.participant.contract.command.fsp.DeactivateFspCommand;
import io.mojaloop.core.participant.contract.command.fsp.DeactivateFspCurrencyCommand;
import io.mojaloop.core.participant.contract.command.hub.ActivateHubCurrencyCommand;
import io.mojaloop.core.participant.contract.command.hub.AddHubCurrencyCommand;
import io.mojaloop.core.participant.contract.command.hub.ChangeHubNameCommand;
import io.mojaloop.core.participant.contract.command.hub.CreateHubCommand;
import io.mojaloop.core.participant.contract.command.hub.DeactivateHubCurrencyCommand;
import io.mojaloop.core.participant.contract.command.oracle.ActivateOracleCommand;
import io.mojaloop.core.participant.contract.command.oracle.ChangeOracleNameCommand;
import io.mojaloop.core.participant.contract.command.oracle.ChangeOracleTypeCommand;
import io.mojaloop.core.participant.contract.command.oracle.CreateOracleCommand;
import io.mojaloop.core.participant.contract.command.oracle.DeactivateOracleCommand;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.contract.data.HubData;
import io.mojaloop.core.participant.contract.data.OracleData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.List;

public interface ParticipantAdminService {

    interface FspCommands {

        @POST("fsps/activate-endpoint")
        Call<ActivateEndpointCommand.Output> activateEndpoint(@Body ActivateEndpointCommand.Input input);

        @POST("fsps/activate-fsp")
        Call<ActivateFspCommand.Output> activateFsp(@Body ActivateFspCommand.Input input);

        @POST("fsps/activate-supported-currency")
        Call<ActivateFspCurrencyCommand.Output> activateSupportedCurrency(@Body ActivateFspCurrencyCommand.Input input);

        @POST("fsps/add-endpoint")
        Call<AddEndpointCommand.Output> addEndpoint(@Body AddEndpointCommand.Input input);

        @POST("fsps/add-supported-currency")
        Call<AddFspCurrencyCommand.Output> addSupportedCurrency(@Body AddFspCurrencyCommand.Input input);

        @POST("fsps/change-endpoint")
        Call<ChangeEndpointCommand.Output> changeEndpoint(@Body ChangeEndpointCommand.Input input);

        @POST("fsps/change-fsp-name")
        Call<ChangeFspNameCommand.Output> changeFspName(@Body ChangeFspNameCommand.Input input);

        @POST("fsps/create-fsp")
        Call<CreateFspCommand.Output> createFsp(@Body CreateFspCommand.Input input);

        @POST("fsps/deactivate-endpoint")
        Call<DeactivateEndpointCommand.Output> deactivateEndpoint(@Body DeactivateEndpointCommand.Input input);

        @POST("fsps/deactivate-fsp")
        Call<DeactivateFspCommand.Output> deactivateFsp(@Body DeactivateFspCommand.Input input);

        @POST("fsps/deactivate-supported-currency")
        Call<DeactivateFspCurrencyCommand.Output> deactivateSupportedCurrency(@Body DeactivateFspCurrencyCommand.Input input);

        @GET("fsps/get-all-fsps")
        Call<List<FspData>> getAllFsps();

        @GET("fsps/get-fsp")
        Call<FspData> getFsp(@Query("fspId") Long fspId);

    }

    interface OracleCommands {

        @POST("oracles/activate-oracle")
        Call<ActivateOracleCommand.Output> activateOracle(@Body ActivateOracleCommand.Input input);

        @POST("oracles/change-name")
        Call<ChangeOracleNameCommand.Output> changeName(@Body ChangeOracleNameCommand.Input input);

        @POST("oracles/change-type")
        Call<ChangeOracleTypeCommand.Output> changeType(@Body ChangeOracleTypeCommand.Input input);

        @POST("oracles/create-oracle")
        Call<CreateOracleCommand.Output> createOracle(@Body CreateOracleCommand.Input input);

        @POST("oracles/deactivate-oracle")
        Call<DeactivateOracleCommand.Output> deactivateOracle(@Body DeactivateOracleCommand.Input input);

        @GET("oracles/get-all-oracles")
        Call<List<OracleData>> getAllOracles();

        @GET("oracles/get-oracle")
        Call<OracleData> getOracle(@Query("oracleId") Long oracleId);

    }

    interface HubCommands {

        @POST("hubs/activate-currency")
        Call<ActivateHubCurrencyCommand.Output> activateCurrency(@Body ActivateHubCurrencyCommand.Input input);

        @POST("hubs/add-currency")
        Call<AddHubCurrencyCommand.Output> addCurrency(@Body AddHubCurrencyCommand.Input input);

        @POST("hubs/change-name")
        Call<ChangeHubNameCommand.Output> changeName(@Body ChangeHubNameCommand.Input input);

        @POST("hubs/create-hub")
        Call<CreateHubCommand.Output> createHub(@Body CreateHubCommand.Input input);

        @POST("hubs/deactivate-currency")
        Call<DeactivateHubCurrencyCommand.Output> deactivateCurrency(@Body DeactivateHubCurrencyCommand.Input input);

        @GET("hubs/get-all-hubs")
        Call<List<HubData>> getAllHubs();

        @GET("hubs/get-hub")
        Call<HubData> getHub(@Query("hubId") Long hubId);

    }

    record Settings(String baseUrl) { }

}
