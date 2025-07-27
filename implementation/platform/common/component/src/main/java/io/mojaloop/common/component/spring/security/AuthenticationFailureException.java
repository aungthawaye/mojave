package io.mojaloop.common.component.spring.security;

import org.springframework.security.core.AuthenticationException;

public class AuthenticationFailureException extends AuthenticationException {

    public AuthenticationFailureException(String msg) {

        super(msg);
    }

    public AuthenticationFailureException(String msg, Throwable cause) {

        super(msg, cause);
    }

}
