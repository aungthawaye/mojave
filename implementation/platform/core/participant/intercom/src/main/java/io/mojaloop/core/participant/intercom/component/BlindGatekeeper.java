package io.mojaloop.core.participant.intercom.component;

import io.mojaloop.component.web.request.CachedServletRequest;
import io.mojaloop.component.web.security.spring.AuthenticationFailureException;
import io.mojaloop.component.web.security.spring.Authenticator;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.ArrayList;

public class BlindGatekeeper implements Authenticator {

    @Override
    public UsernamePasswordAuthenticationToken authenticate(CachedServletRequest cachedServletRequest)
        throws AuthenticationFailureException {

        return new UsernamePasswordAuthenticationToken("user", "password", new ArrayList<>());
    }

}
