package io.mojaloop.core.participant.admin.component;

import io.mojaloop.component.web.security.spring.AuthenticationErrorWriter;
import io.mojaloop.component.web.security.spring.AuthenticationFailureException;
import jakarta.servlet.http.HttpServletResponse;

public class EmptyErrorWriter implements AuthenticationErrorWriter {

    @Override
    public void write(HttpServletResponse response, AuthenticationFailureException exception) {

    }

}
