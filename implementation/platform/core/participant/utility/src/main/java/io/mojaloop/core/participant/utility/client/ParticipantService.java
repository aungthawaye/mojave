package io.mojaloop.core.participant.utility.client;

import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.contract.data.OracleData;
import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

interface ParticipantService {

    @GET("/fsps")
    Call<List<FspData>> getFsps();

    @GET("/oracles")
    Call<List<OracleData>> getOracles();

}
