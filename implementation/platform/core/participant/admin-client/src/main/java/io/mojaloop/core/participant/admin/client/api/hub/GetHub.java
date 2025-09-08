package io.mojaloop.core.participant.admin.client.api.hub;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.error.RestErrorResponse;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.participant.admin.client.exception.ParticipantCommandClientException;
import io.mojaloop.core.participant.admin.client.service.ParticipantAdminService;
import io.mojaloop.core.participant.contract.data.HubData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GetHub {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetHub.class);

    private final ParticipantAdminService.HubCommands hubCommands;

    private final ObjectMapper objectMapper;

    public GetHub(ParticipantAdminService.HubCommands hubCommands, ObjectMapper objectMapper) {

        assert hubCommands != null;
        assert objectMapper != null;

        this.hubCommands = hubCommands;
        this.objectMapper = objectMapper;
    }

    public HubData execute(Long hubId) throws ParticipantCommandClientException {

        try {

            return RetrofitService
                       .invoke(this.hubCommands.getHub(hubId), (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, this.objectMapper))
                       .body();

        } catch (RetrofitService.InvocationException e) {

            LOGGER.error("Error invoking getHub : {}", e.getMessage());

            var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse(String code, String message)) {

                throw new ParticipantCommandClientException(code, message);
            }

            throw new ParticipantCommandClientException("INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }
}
