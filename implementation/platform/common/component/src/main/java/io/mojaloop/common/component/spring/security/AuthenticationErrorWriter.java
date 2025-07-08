package io.mojaloop.common.component.spring.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;

public interface AuthenticationErrorWriter {

    void writeError(HttpServletResponse response, AuthenticationException exception);

}
