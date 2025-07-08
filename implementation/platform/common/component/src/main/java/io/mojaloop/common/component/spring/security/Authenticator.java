package io.mojaloop.common.component.spring.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.util.ContentCachingRequestWrapper;

public interface Authenticator {

    UsernamePasswordAuthenticationToken authenticate(ContentCachingRequestWrapper requestWrapper) throws AuthenticationException;

    class AuthenticationException extends Exception {

        public AuthenticationException(String message) {

            super(message);
        }

    }

}
