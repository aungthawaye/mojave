package io.mojaloop.core.participant.admin.client.api.fsp;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.error.RestErrorResponse;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.participant.admin.client.exception.ParticipantCommandClientException;
import io.mojaloop.core.participant.admin.client.service.ParticipantAdminService;
import io.mojaloop.core.participant.contract.command.fsp.DeactivateFspCurrencyCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DeactivateSupportedCurrency {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeactivateSupportedCurrency.class);

    private final ParticipantAdminService.FspCommands fspCommands;

    private final ObjectMapper objectMapper;

    public DeactivateSupportedCurrency(ParticipantAdminService.FspCommands fspCommands, ObjectMapper objectMapper) {

        assert fspCommands != null;
        assert objectMapper != null;

        this.fspCommands = fspCommands;
        this.objectMapper = objectMapper;
    }

    public DeactivateFspCurrencyCommand.Output execute(DeactivateFspCurrencyCommand.Input input) throws ParticipantCommandClientException {

        try {

            return RetrofitService
                       .invoke(this.fspCommands.deactivateSupportedCurrency(input), (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, this.objectMapper))
                       .body();

        } catch (RetrofitService.InvocationException e) {

            LOGGER.error("Error invoking deactivateSupportedCurrency : {}", e.getMessage());

            var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse(String code, String message)) {

                throw new ParticipantCommandClientException(code, message);
            }

            throw new ParticipantCommandClientException("INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }
}
