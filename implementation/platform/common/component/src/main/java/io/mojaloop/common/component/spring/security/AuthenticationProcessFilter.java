/*-
 * ================================================================================
 * Mojaloop OSS
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */

package io.mojaloop.common.component.spring.security;

import io.mojaloop.common.component.http.CachedBodyHttpServletRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.PathContainer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.io.IOException;

class AuthenticationProcessFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationProcessFilter.class);

    private final Authenticator authenticator;

    private final PathPattern pathPattern;

    public AuthenticationProcessFilter(Authenticator authenticator, SpringSecurityConfigurer.Settings settings) {

        assert authenticator != null;
        assert settings != null;

        this.authenticator = authenticator;

        var parser = new PathPatternParser();
        this.pathPattern = parser.parse(settings.securedEndpointPrefix());
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {

        var uri = request.getRequestURI();
        LOGGER.debug("Received request for URI : [{}]", uri);

        if (this.pathPattern.matches(PathContainer.parsePath(uri))) {

            LOGGER.info("Authentication is required for URI : [{}]", uri);

            var requestWrapper = this.getCachedBodyHttpServletRequest(request);

            try {

                var authenticationToken = this.authenticator.authenticate(requestWrapper);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                LOGGER.debug("Authentication is successful for URI : {}", uri);

            } catch (Exception e) {

                LOGGER.error("Authentication error : {}", e.getMessage());
                SecurityContextHolder.clearContext();

                throw new AuthenticationFailureException(e.getMessage(), e);
            }

        }

        filterChain.doFilter(request, response);
    }

    @NotNull
    private CachedBodyHttpServletRequest getCachedBodyHttpServletRequest(HttpServletRequest request) {

        CachedBodyHttpServletRequest requestWrapper = null;

        if (request instanceof CachedBodyHttpServletRequest) {

            LOGGER.debug("Already CachedBodyHttpServletRequest.");
            requestWrapper = (CachedBodyHttpServletRequest) request;

        } else {

            try {

                LOGGER.debug("Creating CachedBodyHttpServletRequest.");
                requestWrapper = new CachedBodyHttpServletRequest(request);
                requestWrapper.getParameterMap();

            } catch (IOException e) {

                LOGGER.error("Error :", e);
                LOGGER.error("Error occurred during reading request body for CachedBodyHttpServletRequest : [{}]", e.getMessage());
                throw new RuntimeException(e);
            }
        }

        return requestWrapper;
    }

}
