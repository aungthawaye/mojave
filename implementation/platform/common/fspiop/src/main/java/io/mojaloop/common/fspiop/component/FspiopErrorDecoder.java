package io.mojaloop.common.fspiop.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.common.component.retrofit.RetrofitService;
import io.mojaloop.common.fspiop.core.model.ErrorInformationResponse;
import okhttp3.ResponseBody;

public class FspiopErrorDecoder implements RetrofitService.ErrorDecoder<ErrorInformationResponse> {

    private final ObjectMapper objectMapper;

    public FspiopErrorDecoder(ObjectMapper objectMapper) {

        assert objectMapper != null;

        this.objectMapper = objectMapper;
    }

    @Override
    public ErrorInformationResponse decode(int status, ResponseBody errorResponseBody) {

        return this.objectMapper.convertValue(errorResponseBody, ErrorInformationResponse.class);
    }

}