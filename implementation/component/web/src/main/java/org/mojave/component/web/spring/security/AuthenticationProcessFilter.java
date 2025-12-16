/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
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
 * ===
 */

package org.mojave.component.web.spring.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mojave.component.web.request.CachedServletRequest;
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

    private final PathPattern[] pathPatterns;

    public AuthenticationProcessFilter(Authenticator authenticator,
                                       SpringSecurityConfigurer.Settings settings) {

        assert authenticator != null;
        assert settings != null;

        this.authenticator = authenticator;

        if (settings.securedEndpoints() == null || settings.securedEndpoints().length == 0) {

            this.pathPatterns = null;
            LOGGER.info("No secured endpoints are defined.");

        } else {

            this.pathPatterns = new PathPattern[settings.securedEndpoints().length];

            for (int i = 0; i < settings.securedEndpoints().length; i++) {
                var parser = new PathPatternParser();
                this.pathPatterns[i] = parser.parse(settings.securedEndpoints()[i]);
            }

            LOGGER.info("Secured endpoints are defined.");
        }

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        var method = request.getMethod();
        var uri = request.getRequestURI();
        LOGGER.debug("Received request for URI ({}) : ({})", method, uri);

        var match = false;

        if (this.pathPatterns != null) {

            for (var pathPattern : this.pathPatterns) {
                if (pathPattern.matches(PathContainer.parsePath(uri))) {
                    match = true;
                    break;
                }
            }

        }

        var requestWrapper = this.getCachedBodyHttpServletRequest(request);

        if (match && SecurityContextHolder.getContext().getAuthentication() == null) {

            LOGGER.info("Authentication is required for URI ({}) : ({})", method, uri);

            try {

                var authenticationToken = this.authenticator.authenticate(requestWrapper);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                LOGGER.info("Authentication is successful for URI ({}) : ({})", method, uri);

            } catch (AuthenticationFailureException e) {

                LOGGER.error("Error:", e);
                SecurityContextHolder.clearContext();
                throw e;
            }

        }

        filterChain.doFilter(requestWrapper, response);
    }

    private CachedServletRequest getCachedBodyHttpServletRequest(HttpServletRequest request) {

        CachedServletRequest requestWrapper = null;

        if (request instanceof CachedServletRequest) {

            LOGGER.debug("Already CachedServletRequest.");
            requestWrapper = (CachedServletRequest) request;

        } else {

            try {

                LOGGER.debug("Creating CachedServletRequest.");
                requestWrapper = new CachedServletRequest(request);
                requestWrapper.getParameterMap();

            } catch (IOException e) {

                LOGGER.error("Error :", e);
                LOGGER.error(
                    "Error occurred during reading request body for CachedServletRequest : ({})",
                    e.getMessage());
                throw new RuntimeException(e);
            }
        }

        return requestWrapper;
    }

}
