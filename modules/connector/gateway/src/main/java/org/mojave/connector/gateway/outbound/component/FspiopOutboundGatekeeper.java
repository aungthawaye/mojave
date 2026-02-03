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
package org.mojave.connector.gateway.outbound.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.mojave.component.misc.crypto.KeyPairs;
import org.mojave.component.misc.jwt.Jwt;
import org.mojave.component.web.request.CachedServletRequest;
import org.mojave.component.web.spring.security.AuthenticationErrorWriter;
import org.mojave.component.web.spring.security.AuthenticationFailureException;
import org.mojave.component.web.spring.security.Authenticator;
import org.mojave.connector.gateway.outbound.ConnectorOutboundConfiguration;
import org.mojave.rail.fspiop.component.error.FspiopErrors;
import org.mojave.rail.fspiop.component.exception.FspiopException;
import org.mojave.scheme.fspiop.core.ErrorInformation;
import org.mojave.scheme.fspiop.core.ErrorInformationObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Objects;

public class FspiopOutboundGatekeeper implements Authenticator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FspiopOutboundGatekeeper.class);

    private final ConnectorOutboundConfiguration.OutboundSettings outboundSettings;

    private final PublicKey publicKey;

    public FspiopOutboundGatekeeper(ConnectorOutboundConfiguration.OutboundSettings outboundSettings) {

        Objects.requireNonNull(outboundSettings);

        this.outboundSettings = outboundSettings;

        try {

            this.publicKey = KeyPairs.Rsa.publicKeyOf(outboundSettings.publicKeyPem());

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UsernamePasswordAuthenticationToken authenticate(CachedServletRequest cachedServletRequest)
        throws AuthenticationFailureException {

        if (!this.outboundSettings.secured()) {

            return new UsernamePasswordAuthenticationToken(
                "FSP", "skipped", new ArrayList<SimpleGrantedAuthority>() { });
        }

        var authorization = cachedServletRequest.getHeader("Authorization");

        if (authorization == null ||
                SecurityContextHolder.getContext().getAuthentication() == null) {

            throw new FspiopOutboundGatekeeper.GatekeeperFailureException(
                HttpServletResponse.SC_UNAUTHORIZED, new FspiopException(
                FspiopErrors.MISSING_MANDATORY_ELEMENT,
                "Authorization header or its value is missing."));
        }

        var payload = cachedServletRequest.getCachedBodyAsString();
        var base64Payload = Jwt.encode(payload);
        var parts = authorization.split("\\.", -1);
        var header = parts[0];
        var signature = parts[2];

        var verificationOk = Jwt.verify(
            this.publicKey, new Jwt.Token(header, base64Payload, signature));

        if (!verificationOk) {

            LOGGER.error("Signature verification failed when using FSP's public key.");
            throw new FspiopOutboundGatekeeper.GatekeeperFailureException(
                HttpServletResponse.SC_UNAUTHORIZED,
                new FspiopException(FspiopErrors.INVALID_SIGNATURE, "Invalid signature."));
        }

        LOGGER.debug("Signature verification successful");
        return new UsernamePasswordAuthenticationToken(
            "FSP", authorization, new ArrayList<SimpleGrantedAuthority>() { });
    }

    public static class GatekeeperFailureException extends AuthenticationFailureException {

        @Getter
        private final int statusCode;

        public GatekeeperFailureException(int statusCode, FspiopException cause) {

            super(cause.getMessage(), cause);

            this.statusCode = statusCode;
        }

    }

    public static class ErrorWriter implements AuthenticationErrorWriter {

        private static final Logger LOGGER = LoggerFactory.getLogger(ErrorWriter.class);

        private final ObjectMapper objectMapper;

        public ErrorWriter(ObjectMapper objectMapper) {

            Objects.requireNonNull(objectMapper);

            this.objectMapper = objectMapper;
        }

        @Override
        public void write(HttpServletResponse response, AuthenticationFailureException exception) {

            PrintWriter writer = null;

            try {

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                writer = response.getWriter();

                if (exception instanceof FspiopOutboundGatekeeper.GatekeeperFailureException ge) {

                    var cause = (FspiopException) ge.getCause();

                    response.setStatus(ge.getStatusCode());
                    writer.write(this.objectMapper.writeValueAsString(cause.toErrorObject()));

                } else {

                    var error = new ErrorInformationObject().errorInformation(
                        new ErrorInformation(
                            FspiopErrors.GENERIC_CLIENT_ERROR.errorType().getCode(),
                            FspiopErrors.GENERIC_CLIENT_ERROR.description()));

                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    writer.write(this.objectMapper.writeValueAsString(error));
                }

            } catch (JsonProcessingException e) {

                Objects.requireNonNull(writer);

                String errorCode = FspiopErrors.GENERIC_SERVER_ERROR.errorType().getCode();
                String errorDescription = FspiopErrors.GENERIC_SERVER_ERROR.description();

                var json = "{\"errorInformation\":{\"errorCode\": \"" + errorCode +
                               "\",\"errorDescription\":\"" + errorDescription + "\"}}";

                LOGGER.error("Problem occurred :", e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                writer.write(json);

            } catch (IOException e) {

                LOGGER.error("Problem occurred :", e);
                throw new RuntimeException(e);
            }
        }

    }

}
