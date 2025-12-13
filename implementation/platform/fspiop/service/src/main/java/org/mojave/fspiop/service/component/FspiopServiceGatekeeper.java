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
package org.mojave.fspiop.service.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.mojave.component.misc.jwt.Jwt;
import org.mojave.component.web.request.CachedServletRequest;
import org.mojave.component.web.spring.security.AuthenticationFailureException;
import org.mojave.component.web.spring.security.Authenticator;
import org.mojave.component.web.spring.security.SpringSecurityConfigurer;
import org.mojave.fspiop.component.error.FspiopErrors;
import org.mojave.fspiop.component.exception.FspiopException;
import org.mojave.fspiop.component.participant.ParticipantContext;
import org.mojave.fspiop.component.type.Payer;
import org.mojave.fspiop.component.handy.FspiopDates;
import org.mojave.fspiop.component.handy.FspiopHeaders;
import org.mojave.fspiop.component.handy.FspiopSignature;
import org.mojave.fspiop.service.FspiopServiceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class FspiopServiceGatekeeper implements Authenticator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FspiopServiceGatekeeper.class);

    private final ParticipantContext participantContext;

    private final ParticipantVerifier participantVerifier;

    private final ObjectMapper objectMapper;

    private final FspiopServiceConfiguration.ServiceSettings serviceSettings;

    public FspiopServiceGatekeeper(SpringSecurityConfigurer.Settings settings,
                                   ParticipantContext participantContext,
                                   ParticipantVerifier participantVerifier,
                                   ObjectMapper objectMapper,
                                   FspiopServiceConfiguration.ServiceSettings serviceSettings) {

        assert participantContext != null;
        assert participantVerifier != null;
        assert objectMapper != null;
        assert serviceSettings != null;

        this.participantContext = participantContext;
        this.participantVerifier = participantVerifier;
        this.objectMapper = objectMapper;
        this.serviceSettings = serviceSettings;
    }

    @Override
    public UsernamePasswordAuthenticationToken authenticate(CachedServletRequest cachedServletRequest)
        throws GatekeeperFailureException {

        try {

            this.verifyRequestAge(cachedServletRequest);
            this.verifyFsps(cachedServletRequest);

            return this.authenticateUsingJws(cachedServletRequest);

        } catch (JsonProcessingException e) {

            LOGGER.error("Error : ", e);
            throw new GatekeeperFailureException(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                new FspiopException(
                    FspiopErrors.GENERIC_SERVER_ERROR,
                    "Unable to parse the 'fspiop-signature' header."));

        } catch (GatekeeperFailureException e) {

            LOGGER.error("Error : ", e);
            throw e;

        } catch (Exception e) {

            LOGGER.error("Error : ", e);
            throw new GatekeeperFailureException(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new FspiopException(
                FspiopErrors.GENERIC_SERVER_ERROR,
                "An unexpected error occurred while authenticating the request."));
        }
    }

    private UsernamePasswordAuthenticationToken authenticateUsingJws(CachedServletRequest cachedServletRequest)
        throws JsonProcessingException {

        var payer = cachedServletRequest.getHeader(FspiopHeaders.Names.FSPIOP_SOURCE);

        if (!this.participantContext.verifyJws()) {

            LOGGER.warn("JWS verification is disabled.");
            return new UsernamePasswordAuthenticationToken(
                payer,
                new FspiopSignature.Header(null, null), new ArrayList<SimpleGrantedAuthority>());
        }

        var getMethod = cachedServletRequest.getMethod().equalsIgnoreCase("GET");
        var signatureHeader = cachedServletRequest.getHeader(FspiopHeaders.Names.FSPIOP_SIGNATURE);

        if (signatureHeader == null || signatureHeader.isBlank()) {

            LOGGER.error("The 'fspiop-signature' header is missing.");
            throw new GatekeeperFailureException(
                HttpServletResponse.SC_BAD_REQUEST,
                new FspiopException(
                    FspiopErrors.MISSING_MANDATORY_ELEMENT,
                    "The 'fspiop-signature' header is missing."));
        }

        var signature = this.objectMapper.readValue(signatureHeader, FspiopSignature.Header.class);

        var publicKey = this.participantContext.publicKeys().get(payer);

        if (publicKey == null) {

            LOGGER.warn("No public key found for Source FSP ({}).", payer);
            throw new GatekeeperFailureException(
                HttpServletResponse.SC_UNAUTHORIZED,
                new FspiopException(
                    FspiopErrors.INVALID_SIGNATURE,
                    "No public key found for Source FSP (" + payer + ")."));
        }

        var payload = getMethod ? this.buildDummyPayload(cachedServletRequest) :
                          cachedServletRequest.getCachedBodyAsString();

        var encodedPayload = Jwt.encode(payload);

        var verificationOk = FspiopSignature.verify(
            publicKey,
            new Jwt.Token(signature.protectedHeader(), encodedPayload, signature.signature()));

        if (!verificationOk) {

            LOGGER.info(
                "Signature verification failed when using Source FSP ({})'s public key.", payer);
            throw new GatekeeperFailureException(
                HttpServletResponse.SC_UNAUTHORIZED, new FspiopException(
                FspiopErrors.INVALID_SIGNATURE,
                "Signature verification failed when using Source FSP (" + payer +
                    ")'s public key."));
        }

        return new UsernamePasswordAuthenticationToken(
            new Payer(payer), signature, new ArrayList<SimpleGrantedAuthority>() { });
    }

    private String buildDummyPayload(CachedServletRequest cachedServletRequest) {

        return "{\"date\":\"" + cachedServletRequest.getHeader(FspiopHeaders.Names.DATE) + "\"}";
    }

    private void verifyFsps(CachedServletRequest cachedServletRequest) {

        var source = cachedServletRequest.getHeader(FspiopHeaders.Names.FSPIOP_SOURCE);

        if (source == null || source.isBlank()) {

            LOGGER.info("The 'fspiop-source' header is missing.");
            throw new GatekeeperFailureException(
                HttpServletResponse.SC_BAD_REQUEST, new FspiopException(
                FspiopErrors.MISSING_MANDATORY_ELEMENT,
                "The 'fspiop-source' header or its value is missing."));
        }

        var destination = cachedServletRequest.getHeader(FspiopHeaders.Names.FSPIOP_DESTINATION);

        if (destination == null || destination.isBlank()) {

            LOGGER.info("The 'fspiop-destination' header is missing.");
            throw new GatekeeperFailureException(
                HttpServletResponse.SC_BAD_REQUEST, new FspiopException(
                FspiopErrors.MISSING_MANDATORY_ELEMENT,
                "The 'fspiop-destination' header or its value is missing."));
        }

        var uri = cachedServletRequest.getRequestURI();

        if (!this.participantVerifier.fspExists(source)) {

            LOGGER.warn("The Source FSP ({}) does not exist.", source);
            throw new GatekeeperFailureException(
                HttpServletResponse.SC_NOT_ACCEPTABLE,
                new FspiopException(FspiopErrors.PAYER_FSP_ID_NOT_FOUND));
        }

        if (!this.participantVerifier.fspExists(destination)) {

            LOGGER.warn("The Destination FSP ({}) does not exist.", destination);
            throw new GatekeeperFailureException(
                HttpServletResponse.SC_NOT_ACCEPTABLE,
                new FspiopException(FspiopErrors.PAYEE_FSP_ID_NOT_FOUND));
        }

        if (source.equals(destination)) {

            LOGGER.info(
                "The Source FSP ({}) and the destination FSP ({}) must not be the same.", source,
                destination);
            throw new GatekeeperFailureException(
                HttpServletResponse.SC_NOT_ACCEPTABLE, new FspiopException(
                FspiopErrors.DESTINATION_FSP_ERROR,
                "Source FSP (" + source + ") and Destination FSP (" + destination +
                    ") must not be the same."));
        }

        if (destination.equals(this.participantContext.fspCode())) {

            LOGGER.info(
                "Destination FSP ({}) must not be Hub ({}).", destination,
                this.participantContext.fspCode());
            throw new GatekeeperFailureException(
                HttpServletResponse.SC_NOT_ACCEPTABLE, new FspiopException(
                FspiopErrors.DESTINATION_FSP_ERROR,
                "Destination FSP must not be Hub (" + this.participantContext.fspCode() + ")."));
        }

    }

    private void verifyRequestAge(CachedServletRequest cachedServletRequest) {

        var date = cachedServletRequest.getHeader(FspiopHeaders.Names.DATE);

        if (date == null || date.isBlank()) {

            LOGGER.info("The 'date' header is missing.");
            throw new GatekeeperFailureException(
                HttpServletResponse.SC_BAD_REQUEST,
                new FspiopException(
                    FspiopErrors.MISSING_MANDATORY_ELEMENT,
                    "The 'date' header or its value is missing."));
        }

        Instant sentAt = null;

        try {

            sentAt = FspiopDates.fromRequestHeader(date);

        } catch (FspiopException e) {

            throw new GatekeeperFailureException(HttpServletResponse.SC_BAD_REQUEST, e);

        }

        if (sentAt
                .plus(this.serviceSettings.requestAgeMs(), ChronoUnit.SECONDS)
                .isBefore(Instant.now())) {

            throw new GatekeeperFailureException(
                HttpServletResponse.SC_BAD_REQUEST,
                new FspiopException(
                    FspiopErrors.GENERIC_VALIDATION_ERROR, "The request is too old."));
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

}
