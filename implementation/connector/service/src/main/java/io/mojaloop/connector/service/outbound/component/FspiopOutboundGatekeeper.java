package io.mojaloop.connector.service.outbound.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.web.request.CachedServletRequest;
import io.mojaloop.component.web.security.spring.AuthenticationFailureException;
import io.mojaloop.component.web.security.spring.Authenticator;
import io.mojaloop.component.web.security.spring.SpringSecurityConfigurer;
import io.mojaloop.fspiop.common.participant.ParticipantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class FspiopOutboundGatekeeper implements Authenticator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FspiopOutboundGatekeeper.class);

    private final ParticipantContext participantContext;

    private final ObjectMapper objectMapper;

    public FspiopOutboundGatekeeper(SpringSecurityConfigurer.Settings settings,
                                    ParticipantContext participantContext,
                                    ObjectMapper objectMapper) {

        assert participantContext != null;
        assert objectMapper != null;

        this.participantContext = participantContext;
        this.objectMapper = objectMapper;
    }

    @Override
    public UsernamePasswordAuthenticationToken authenticate(CachedServletRequest cachedServletRequest)
        throws AuthenticationFailureException {

        return null;
    }

}
