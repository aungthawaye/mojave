package io.mojaloop.core.participant.admin.client.api.oracle;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.error.RestErrorResponse;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.participant.admin.client.exception.ParticipantCommandClientException;
import io.mojaloop.core.participant.admin.client.service.ParticipantAdminService;
import io.mojaloop.core.participant.contract.data.OracleData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetAllOracles {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetAllOracles.class);

    private final ParticipantAdminService.OracleCommands oracleCommands;

    private final ObjectMapper objectMapper;

    public GetAllOracles(ParticipantAdminService.OracleCommands oracleCommands, ObjectMapper objectMapper) {

        assert oracleCommands != null;
        assert objectMapper != null;

        this.oracleCommands = oracleCommands;
        this.objectMapper = objectMapper;
    }

    public List<OracleData> execute() throws ParticipantCommandClientException {

        try {

            return RetrofitService
                       .invoke(this.oracleCommands.getAllOracles(), (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, this.objectMapper))
                       .body();

        } catch (RetrofitService.InvocationException e) {

            LOGGER.error("Error invoking getAllOracles : {}", e.getMessage());

            var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse(String code, String message)) {

                throw new ParticipantCommandClientException(code, message);
            }

            throw new ParticipantCommandClientException("INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }
}
