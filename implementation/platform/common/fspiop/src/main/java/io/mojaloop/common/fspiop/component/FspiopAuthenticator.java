package io.mojaloop.common.fspiop.component;

import io.mojaloop.common.component.spring.security.Authenticator;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.util.ContentCachingRequestWrapper;

public class FspiopAuthenticator implements Authenticator {

    @Override
    public UsernamePasswordAuthenticationToken authenticate(ContentCachingRequestWrapper requestWrapper) throws AuthenticationException {

        return null;
    }

}
