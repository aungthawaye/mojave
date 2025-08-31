package io.mojaloop.core.participant.admin.client.service;

import io.mojaloop.core.participant.contract.command.fsp.ActivateEndpointCommand;
import io.mojaloop.core.participant.contract.command.fsp.ActivateFspCommand;
import io.mojaloop.core.participant.contract.command.fsp.ActivateSupportedCurrencyCommand;
import io.mojaloop.core.participant.contract.command.fsp.AddEndpointCommand;
import io.mojaloop.core.participant.contract.command.fsp.AddSupportedCurrencyCommand;
import io.mojaloop.core.participant.contract.command.fsp.ChangeEndpointCommand;
import io.mojaloop.core.participant.contract.command.fsp.ChangeFspNameCommand;
import io.mojaloop.core.participant.contract.command.fsp.CreateFspCommand;
import io.mojaloop.core.participant.contract.command.fsp.DeactivateEndpointCommand;
import io.mojaloop.core.participant.contract.command.fsp.DeactivateFspCommand;
import io.mojaloop.core.participant.contract.command.fsp.DeactivateSupportedCurrencyCommand;
import io.mojaloop.core.participant.contract.data.FspData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.List;

public interface ParticipantAdminService {

    interface FspCommands {

        @POST("fsps/activate-fsp")
        Call<ActivateFspCommand.Output> activateFsp(@Body ActivateFspCommand.Input input);

        @POST("fsps/activate-supported-currency")
        Call<ActivateSupportedCurrencyCommand.Output> activateSupportedCurrency(@Body ActivateSupportedCurrencyCommand.Input input);

        @POST("fsps/add-supported-currency")
        Call<AddSupportedCurrencyCommand.Output> addSupportedCurrency(@Body AddSupportedCurrencyCommand.Input input);

        @POST("fsps/change-fsp-name")
        Call<ChangeFspNameCommand.Output> changeFspName(@Body ChangeFspNameCommand.Input input);

        @POST("fsps/create-fsp")
        Call<CreateFspCommand.Output> createFsp(@Body CreateFspCommand.Input input);

        @POST("fsps/deactivate-fsp")
        Call<DeactivateFspCommand.Output> deactivateFsp(@Body DeactivateFspCommand.Input input);

        @POST("fsps/deactivate-supported-currency")
        Call<DeactivateSupportedCurrencyCommand.Output> deactivateSupportedCurrency(@Body DeactivateSupportedCurrencyCommand.Input input);

        @POST("fsps/add-endpoint")
        Call<AddEndpointCommand.Output> addEndpoint(@Body AddEndpointCommand.Input input);

        @POST("fsps/activate-endpoint")
        Call<ActivateEndpointCommand.Output> activateEndpoint(@Body ActivateEndpointCommand.Input input);

        @POST("fsps/change-endpoint")
        Call<ChangeEndpointCommand.Output> changeEndpoint(@Body ChangeEndpointCommand.Input input);

        @POST("fsps/deactivate-endpoint")
        Call<DeactivateEndpointCommand.Output> deactivateEndpoint(@Body DeactivateEndpointCommand.Input input);

        @GET("fsps/get-all-fsps")
        Call<List<FspData>> getAllFsps();

        @GET("fsps/get-fsp")
        Call<FspData> getFsp(@Query("fspId") Long fspId);

    }

    record Settings(String baseUrl) { }

}
