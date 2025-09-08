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

import java.util.List;

@Component
public class GetAllHubs {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetAllHubs.class);

    private final ParticipantAdminService.HubCommands hubCommands;

    private final ObjectMapper objectMapper;

    public GetAllHubs(ParticipantAdminService.HubCommands hubCommands, ObjectMapper objectMapper) {

        assert hubCommands != null;
        assert objectMapper != null;

        this.hubCommands = hubCommands;
        this.objectMapper = objectMapper;
    }

    public List<HubData> execute() throws ParticipantCommandClientException {

        try {

            return RetrofitService
                       .invoke(this.hubCommands.getAllHubs(), (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, this.objectMapper))
                       .body();

        } catch (RetrofitService.InvocationException e) {

            LOGGER.error("Error invoking getAllHubs : {}", e.getMessage());

            var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse(String code, String message)) {

                throw new ParticipantCommandClientException(code, message);
            }

            throw new ParticipantCommandClientException("INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }
}
