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

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

@RequiredArgsConstructor
class AuthenticationProcessFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationProcessFilter.class);

    private final Authenticator authenticator;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        var uri = request.getRequestURI();
        LOGGER.debug("Received request for URI : {}", uri);

        if (uri.contains("/secured/")) {

            var requestWrapper = new ContentCachingRequestWrapper(request);
            requestWrapper.getParameterMap();

            try {

                var authenticationToken = this.authenticator.authenticate(requestWrapper);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                LOGGER.debug("Authentication is successful for URI : {}", uri);

            } catch (Authenticator.AuthenticationException e) {

                LOGGER.error("Authentication error : {}", e.getMessage());
                SecurityContextHolder.clearContext();
                throw new BadCredentialsException(e.getMessage(), e.getCause() != null ? e.getCause() : e);
            }

        }

        filterChain.doFilter(request, response);
    }

}
