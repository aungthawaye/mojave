package io.mojaloop.core.participant.admin.client.api.oracle;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.error.RestErrorResponse;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.participant.admin.client.exception.ParticipantCommandClientException;
import io.mojaloop.core.participant.admin.client.service.ParticipantAdminService;
import io.mojaloop.core.participant.contract.command.oracle.ChangeOracleNameCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ChangeOracleName {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeOracleName.class);

    private final ParticipantAdminService.OracleCommands oracleCommands;

    private final ObjectMapper objectMapper;

    public ChangeOracleName(ParticipantAdminService.OracleCommands oracleCommands, ObjectMapper objectMapper) {

        assert oracleCommands != null;
        assert objectMapper != null;

        this.oracleCommands = oracleCommands;
        this.objectMapper = objectMapper;
    }

    public ChangeOracleNameCommand.Output execute(ChangeOracleNameCommand.Input input) throws ParticipantCommandClientException {

        try {

            return RetrofitService
                       .invoke(this.oracleCommands.changeName(input), (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, this.objectMapper))
                       .body();

        } catch (RetrofitService.InvocationException e) {

            LOGGER.error("Error invoking changeOracleName : {}", e.getMessage());

            var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse(String code, String message)) {

                throw new ParticipantCommandClientException(code, message);
            }

            throw new ParticipantCommandClientException("INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }
}
