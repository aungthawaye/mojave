package io.mojaloop.core.participant.intercom.client.service;

import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.contract.data.OracleData;
import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

public interface ParticipantIntercomService {

    @GET("/fsps")
    Call<List<FspData>> getFsps();

    @GET("/oracles")
    Call<List<OracleData>> getOracles();

    record Settings(String baseUrl) { }
}
