package io.mojaloop.component.misc.error;

import com.fasterxml.jackson.databind.ObjectMapper;

public record RestErrorResponse(String code, String message) {

    public static RestErrorResponse decode(String payload, ObjectMapper objectMapper) {

        try {

            return objectMapper.readValue(payload, RestErrorResponse.class);

        } catch (Exception e) {

            return null;
        }
    }

}
