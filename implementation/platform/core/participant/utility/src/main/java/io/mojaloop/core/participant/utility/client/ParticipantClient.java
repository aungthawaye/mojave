package io.mojaloop.core.participant.utility.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.contract.data.OracleData;

import java.util.List;

public class ParticipantClient {

    private final ParticipantService participantService;

    public ParticipantClient(Settings settings, ObjectMapper objectMapper) {

        this.participantService = RetrofitService
                                      .newBuilder(ParticipantService.class, settings.baseUrl())
                                      .withDefaultFactories(objectMapper)
                                      .build();
    }

    public List<FspData> getFsps() throws RetrofitService.InvocationException {

        return RetrofitService.invoke(this.participantService.getFsps(), null).body();
    }

    public List<OracleData> getOracles() throws RetrofitService.InvocationException {

        return RetrofitService.invoke(this.participantService.getOracles(), null).body();
    }

    public record Settings(String baseUrl) { }

}
