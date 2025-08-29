package io.mojaloop.core.participant.intercom.client.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.error.RestErrorResponse;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.participant.contract.data.OracleData;
import io.mojaloop.core.participant.intercom.client.exception.ParticipantIntercomClientException;
import io.mojaloop.core.participant.intercom.client.service.ParticipantIntercomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetOracles {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetOracles.class);

    private final ParticipantIntercomService participantIntercomService;

    private final ObjectMapper objectMapper;

    public GetOracles(ParticipantIntercomService participantIntercomService, ObjectMapper objectMapper) {

        assert participantIntercomService != null;
        assert objectMapper != null;

        this.participantIntercomService = participantIntercomService;
        this.objectMapper = objectMapper;
    }

    public List<OracleData> execute() throws ParticipantIntercomClientException {

        try {

            return RetrofitService
                       .invoke(this.participantIntercomService.getOracles(),
                               (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, objectMapper)
                              )
                       .body();

        } catch (RetrofitService.InvocationException e) {

            LOGGER.error("Error invoking getOracles : {}", e.getMessage());

            var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse(String code, String message)) {

                throw new ParticipantIntercomClientException(code, message);
            }

            throw new ParticipantIntercomClientException("INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

}
