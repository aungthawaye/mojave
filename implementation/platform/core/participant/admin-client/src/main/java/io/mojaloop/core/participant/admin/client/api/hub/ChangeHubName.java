package io.mojaloop.core.participant.admin.client.api.hub;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.error.RestErrorResponse;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.participant.admin.client.exception.ParticipantCommandClientException;
import io.mojaloop.core.participant.admin.client.service.ParticipantAdminService;
import io.mojaloop.core.participant.contract.command.hub.ChangeHubNameCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ChangeHubName {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeHubName.class);

    private final ParticipantAdminService.HubCommands hubCommands;

    private final ObjectMapper objectMapper;

    public ChangeHubName(ParticipantAdminService.HubCommands hubCommands, ObjectMapper objectMapper) {

        assert hubCommands != null;
        assert objectMapper != null;

        this.hubCommands = hubCommands;
        this.objectMapper = objectMapper;
    }

    public ChangeHubNameCommand.Output execute(ChangeHubNameCommand.Input input) throws ParticipantCommandClientException {

        try {

            return RetrofitService
                       .invoke(this.hubCommands.changeName(input), (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, this.objectMapper))
                       .body();

        } catch (RetrofitService.InvocationException e) {

            LOGGER.error("Error invoking changeHubName : {}", e.getMessage());

            var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse(String code, String message)) {

                throw new ParticipantCommandClientException(code, message);
            }

            throw new ParticipantCommandClientException("INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }
}
