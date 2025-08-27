package io.mojaloop.connector.gateway.inbound.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.web.security.spring.AuthenticationErrorWriter;
import io.mojaloop.component.web.security.spring.AuthenticationFailureException;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.spec.core.ErrorInformation;
import io.mojaloop.fspiop.spec.core.ErrorInformationObject;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;

public class FspiopInboundErrorWriter implements AuthenticationErrorWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(FspiopInboundErrorWriter.class);

    private final ObjectMapper objectMapper;

    public FspiopInboundErrorWriter(ObjectMapper objectMapper) {

        assert objectMapper != null;

        this.objectMapper = objectMapper;
    }

    @Override
    public void write(HttpServletResponse response, AuthenticationFailureException exception) {

        PrintWriter writer = null;

        try {

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            writer = response.getWriter();

            if (exception instanceof FspiopInboundGatekeeper.GatekeeperFailureException ge) {

                var cause = (FspiopException) ge.getCause();

                response.setStatus(ge.getStatusCode());
                writer.write(this.objectMapper.writeValueAsString(cause.toErrorObject()));

            } else {

                var error = new ErrorInformationObject().errorInformation(new ErrorInformation(FspiopErrors.GENERIC_CLIENT_ERROR.code(),
                                                                                               FspiopErrors.GENERIC_CLIENT_ERROR.description()));

                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                writer.write(this.objectMapper.writeValueAsString(error));
            }

        } catch (JsonProcessingException e) {

            assert writer != null;

            String errorCode = FspiopErrors.GENERIC_SERVER_ERROR.code();
            String errorDescription = FspiopErrors.GENERIC_SERVER_ERROR.description();

            var json = "{\"errorInformation\":{\"errorCode\": \"" + errorCode + "\",\"errorDescription\":\"" + errorDescription + "\"}}";

            LOGGER.error("Problem occurred :", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writer.write(json);

        } catch (IOException e) {

            LOGGER.error("Problem occurred :", e);
            throw new RuntimeException(e);
        }
    }

}
