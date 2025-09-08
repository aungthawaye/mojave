package io.mojaloop.core.participant.admin.client.api.hub;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.error.RestErrorResponse;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.participant.admin.client.exception.ParticipantCommandClientException;
import io.mojaloop.core.participant.admin.client.service.ParticipantAdminService;
import io.mojaloop.core.participant.contract.command.hub.CreateHubCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CreateHub {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateHub.class);

    private final ParticipantAdminService.HubCommands hubCommands;

    private final ObjectMapper objectMapper;

    public CreateHub(ParticipantAdminService.HubCommands hubCommands, ObjectMapper objectMapper) {

        assert hubCommands != null;
        assert objectMapper != null;

        this.hubCommands = hubCommands;
        this.objectMapper = objectMapper;
    }

    public CreateHubCommand.Output execute(CreateHubCommand.Input input) throws ParticipantCommandClientException {

        try {

            return RetrofitService
                       .invoke(this.hubCommands.createHub(input), (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, this.objectMapper))
                       .body();

        } catch (RetrofitService.InvocationException e) {

            LOGGER.error("Error invoking createHub : {}", e.getMessage());

            var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse(String code, String message)) {

                throw new ParticipantCommandClientException(code, message);
            }

            throw new ParticipantCommandClientException("INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }
}
