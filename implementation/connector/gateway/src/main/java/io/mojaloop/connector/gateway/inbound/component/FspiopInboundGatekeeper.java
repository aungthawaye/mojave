/*-
 * ================================================================================
 * Mojave
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

package io.mojaloop.connector.gateway.inbound.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.jwt.JwtBase64Util;
import io.mojaloop.component.misc.jwt.Rs256Jwt;
import io.mojaloop.component.web.request.CachedServletRequest;
import io.mojaloop.component.web.spring.security.AuthenticationErrorWriter;
import io.mojaloop.component.web.spring.security.AuthenticationFailureException;
import io.mojaloop.component.web.spring.security.Authenticator;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.participant.ParticipantContext;
import io.mojaloop.fspiop.common.type.Payer;
import io.mojaloop.fspiop.component.handy.FspiopHeaders;
import io.mojaloop.fspiop.component.handy.FspiopSignature;
import io.mojaloop.fspiop.spec.core.ErrorInformation;
import io.mojaloop.fspiop.spec.core.ErrorInformationObject;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class FspiopInboundGatekeeper implements Authenticator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FspiopInboundGatekeeper.class);

    private final ParticipantContext participantContext;

    private final ObjectMapper objectMapper;

    public FspiopInboundGatekeeper(ParticipantContext participantContext, ObjectMapper objectMapper) {

        assert participantContext != null;
        assert objectMapper != null;

        this.participantContext = participantContext;
        this.objectMapper = objectMapper;
    }

    @Override
    public UsernamePasswordAuthenticationToken authenticate(CachedServletRequest cachedServletRequest) throws GatekeeperFailureException {

        try {

            this.verifyFsps(cachedServletRequest);

            return this.authenticateUsingJws(cachedServletRequest);

        } catch (JsonProcessingException e) {

            LOGGER.error("Error : ", e);
            throw new GatekeeperFailureException(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, "Unable to parse the 'fspiop-signature' header."));

        } catch (GatekeeperFailureException e) {

            LOGGER.error("Error : ", e);
            throw e;

        } catch (Exception e) {

            LOGGER.error("Error : ", e);
            throw new GatekeeperFailureException(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, "An unexpected error occurred while authenticating the request."));
        }
    }

    private UsernamePasswordAuthenticationToken authenticateUsingJws(CachedServletRequest cachedServletRequest) throws JsonProcessingException {

        var payer = cachedServletRequest.getHeader(FspiopHeaders.Names.FSPIOP_SOURCE);

        if (!this.participantContext.verifyJws()) {

            return new UsernamePasswordAuthenticationToken(payer, new FspiopSignature.Header(null, null), new ArrayList<SimpleGrantedAuthority>());
        }

        var getMethod = cachedServletRequest.getMethod().equalsIgnoreCase("GET");
        var signatureHeader = cachedServletRequest.getHeader(FspiopHeaders.Names.FSPIOP_SIGNATURE);

        if (signatureHeader == null || signatureHeader.isBlank()) {

            LOGGER.error("The 'fspiop-signature' header is missing.");
            throw new GatekeeperFailureException(
                HttpServletResponse.SC_BAD_REQUEST,
                new FspiopException(FspiopErrors.MISSING_MANDATORY_ELEMENT, "The 'fspiop-signature' header is missing."));
        }

        var signature = this.objectMapper.readValue(signatureHeader, FspiopSignature.Header.class);
        LOGGER.debug("FSPIOP_SIGNATURE : [{}]", signature);

        var publicKey = this.participantContext.publicKeys().get(payer);

        if (publicKey == null) {

            LOGGER.error("No public key found for Source FSP ({}).", payer);
            throw new GatekeeperFailureException(
                HttpServletResponse.SC_UNAUTHORIZED,
                new FspiopException(FspiopErrors.INVALID_SIGNATURE, "No public key found for Source FSP (" + payer + ")."));
        }

        var payload = getMethod ? this.buildDummyPayload(cachedServletRequest) : cachedServletRequest.getCachedBodyAsString();
        LOGGER.debug("Payload : [{}]", payload);

        var encodedPayload = JwtBase64Util.encode(payload);
        LOGGER.debug("Encoded payload : [{}]", encodedPayload);

        var verificationOk = FspiopSignature.verify(publicKey, new Rs256Jwt.Token(signature.protectedHeader(), encodedPayload, signature.signature()));

        if (!verificationOk) {

            LOGGER.error("Signature verification failed when using Source FSP ({})'s public key.", payer);
            throw new GatekeeperFailureException(
                HttpServletResponse.SC_UNAUTHORIZED,
                new FspiopException(FspiopErrors.INVALID_SIGNATURE, "Signature verification failed when using Source FSP (" + payer + ")'s public key."));
        }

        LOGGER.debug("Signature verification successful");
        return new UsernamePasswordAuthenticationToken(new Payer(payer), signature, new ArrayList<SimpleGrantedAuthority>() { });
    }

    private String buildDummyPayload(CachedServletRequest cachedServletRequest) {

        return "{\"date\":\"" + cachedServletRequest.getHeader(FspiopHeaders.Names.DATE) + "\"}";
    }

    private void verifyFsps(CachedServletRequest cachedServletRequest) {

        var payer = cachedServletRequest.getHeader(FspiopHeaders.Names.FSPIOP_SOURCE);
        LOGGER.debug("FSPIOP_SOURCE : [{}]", payer);

        if (payer == null || payer.isBlank()) {

            LOGGER.error("The 'fspiop-payer' header is missing.");
            throw new GatekeeperFailureException(
                HttpServletResponse.SC_BAD_REQUEST,
                new FspiopException(FspiopErrors.MISSING_MANDATORY_ELEMENT, "The 'fspiop-payer' header or its value is missing."));
        }

        var destination = cachedServletRequest.getHeader(FspiopHeaders.Names.FSPIOP_DESTINATION);
        LOGGER.debug("FSPIOP_DESTINATION : [{}]", destination);

        if (destination == null || destination.isBlank()) {

            LOGGER.error("The 'fspiop-destination' header is missing.");
            throw new GatekeeperFailureException(
                HttpServletResponse.SC_BAD_REQUEST,
                new FspiopException(FspiopErrors.MISSING_MANDATORY_ELEMENT, "The 'fspiop-destination' header or its value is missing."));
        }

        if (!this.participantContext.fspCode().equalsIgnoreCase(destination)) {

            LOGGER.error("The Destination FSP ({}) is not valid.", payer);
            throw new GatekeeperFailureException(
                HttpServletResponse.SC_NOT_ACCEPTABLE, new FspiopException(
                FspiopErrors.GENERIC_PAYEE_REJECTION,
                "Destination FSP (" + destination + ") is different from the current FSP (" + this.participantContext.fspCode() + ")."));
        }

        if (payer.equals(destination)) {

            LOGGER.error("The Source FSP ({}) and the destination FSP ({}) must not be the same.", payer, destination);
            throw new GatekeeperFailureException(
                HttpServletResponse.SC_NOT_ACCEPTABLE,
                new FspiopException(FspiopErrors.DESTINATION_FSP_ERROR, "Source FSP (" + payer + ") and Destination FSP (" + destination + ") must not be the same."));
        }
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

            assert objectMapper != null;

            this.objectMapper = objectMapper;
        }

        @Override
        public void write(HttpServletResponse response, AuthenticationFailureException exception) {

            PrintWriter writer = null;

            try {

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                writer = response.getWriter();

                if (exception instanceof FspiopInboundGatekeeper.GatekeeperFailureException ge) {

                    var cause = (FspiopException) ge.getCause();

                    response.setStatus(ge.getStatusCode());
                    writer.write(this.objectMapper.writeValueAsString(cause.toErrorObject()));

                } else {

                    var error = new ErrorInformationObject().errorInformation(
                        new ErrorInformation(FspiopErrors.GENERIC_CLIENT_ERROR.errorType().getCode(), FspiopErrors.GENERIC_CLIENT_ERROR.description()));

                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    writer.write(this.objectMapper.writeValueAsString(error));
                }

            } catch (JsonProcessingException e) {

                assert writer != null;

                String errorCode = FspiopErrors.GENERIC_SERVER_ERROR.errorType().getCode();
                String errorDescription = FspiopErrors.GENERIC_SERVER_ERROR.description();

                var json = "{\"errorInformation\":{\"errorCode\": \"" + errorCode + "\",\"errorDescription\":\"" + errorDescription + "\"}}";

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
