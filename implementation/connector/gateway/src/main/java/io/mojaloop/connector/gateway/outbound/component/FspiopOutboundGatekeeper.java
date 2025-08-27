package io.mojaloop.connector.gateway.outbound.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.web.request.CachedServletRequest;
import io.mojaloop.component.web.security.spring.AuthenticationFailureException;
import io.mojaloop.component.web.security.spring.Authenticator;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.participant.ParticipantContext;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class FspiopOutboundGatekeeper implements Authenticator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FspiopOutboundGatekeeper.class);

    private final ParticipantContext participantContext;

    private final ObjectMapper objectMapper;

    public FspiopOutboundGatekeeper(ParticipantContext participantContext, ObjectMapper objectMapper) {

        assert participantContext != null;
        assert objectMapper != null;

        this.participantContext = participantContext;
        this.objectMapper = objectMapper;
    }

    @Override
    public UsernamePasswordAuthenticationToken authenticate(CachedServletRequest cachedServletRequest)
        throws AuthenticationFailureException {

        var authorization = cachedServletRequest.getHeader("Authorization");

        if (authorization == null || SecurityContextHolder.getContext().getAuthentication() == null) {

            throw new FspiopOutboundGatekeeper.GatekeeperFailureException(HttpServletResponse.SC_UNAUTHORIZED,
                                                                          new FspiopException(FspiopErrors.MISSING_MANDATORY_ELEMENT,
                                                                                              "Authorization header or its value is missing."));
        }

        return null;
    }

    public static class GatekeeperFailureException extends AuthenticationFailureException {

        @Getter
        private final int statusCode;

        public GatekeeperFailureException(int statusCode, FspiopException cause) {

            super(cause.getMessage(), cause);

            this.statusCode = statusCode;
        }

    }

}
