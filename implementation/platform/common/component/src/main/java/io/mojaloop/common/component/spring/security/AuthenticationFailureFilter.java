package io.mojaloop.common.component.spring.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
class AuthenticationFailureFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFailureFilter.class);

    private final AuthenticationErrorWriter authenticationErrorWriter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        try {

            filterChain.doFilter(request, response);

        } catch (AuthenticationException e) {

            LOGGER.error("Authentication error : {}", e.getMessage());
            this.authenticationErrorWriter.writeError(response, e);
        }

    }

}
