package org.mojave.core.settlement.domain.component.provider;

import org.mojave.component.misc.error.RestErrorResponse;
import org.mojave.component.retrofit.RetrofitService;
import org.mojave.core.settlement.contract.command.record.InitiateSettlementProcessCommand;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Objects;

@Component
public class SettlementProvider {

    private final SettlementService settlementService;

    private final ObjectMapper objectMapper;

    private final ErrorDecoder errorDecoder;

    public SettlementProvider(final SettlementService settlementService,
                              final ObjectMapper objectMapper) {

        Objects.requireNonNull(settlementService);
        Objects.requireNonNull(objectMapper);

        this.settlementService = settlementService;
        this.objectMapper = objectMapper;
        this.errorDecoder = new ErrorDecoder(objectMapper);
    }

    public void initiateSettlementProcess(final String apiUrl,
                                          final InitiateSettlementProcessCommand.Input input) {

        Objects.requireNonNull(apiUrl);
        Objects.requireNonNull(input);

        try {
            RetrofitService.invoke(
                this.settlementService.initiate(apiUrl, input), this.errorDecoder);
        } catch (RetrofitService.InvocationException e) {
            throw new RuntimeException(e);
        }
    }

    public interface SettlementService {

        @POST
        Call<Void> initiate(@Url String apiUrl, @Body InitiateSettlementProcessCommand.Input input);

    }

    public static class ErrorDecoder implements RetrofitService.ErrorDecoder<RestErrorResponse> {

        private final ObjectMapper objectMapper;

        public ErrorDecoder(ObjectMapper objectMapper) {

            Objects.requireNonNull(objectMapper);

            this.objectMapper = objectMapper;
        }

        @Override
        public RestErrorResponse decode(int status, String errorResponseBody) throws IOException {

            return RestErrorResponse.decode(errorResponseBody, this.objectMapper);
        }

    }

}
