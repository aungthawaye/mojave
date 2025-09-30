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

package io.mojaloop.fspiop.service.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.jwt.JwtBase64Util;
import io.mojaloop.component.misc.jwt.Rs256Jwt;
import io.mojaloop.component.web.request.CachedServletRequest;
import io.mojaloop.component.web.spring.security.AuthenticationFailureException;
import io.mojaloop.component.web.spring.security.Authenticator;
import io.mojaloop.component.web.spring.security.SpringSecurityConfigurer;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.participant.ParticipantContext;
import io.mojaloop.fspiop.common.type.Payer;
import io.mojaloop.fspiop.component.handy.FspiopHeaders;
import io.mojaloop.fspiop.component.handy.FspiopSignature;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;

public class FspiopServiceGatekeeper implements Authenticator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FspiopServiceGatekeeper.class);

    private final ParticipantContext participantContext;

    private final ParticipantVerifier participantVerifier;

    private final ObjectMapper objectMapper;

    public FspiopServiceGatekeeper(SpringSecurityConfigurer.Settings settings,
                                   ParticipantContext participantContext,
                                   ParticipantVerifier participantVerifier,
                                   ObjectMapper objectMapper) {

        assert participantContext != null;
        assert participantVerifier != null;
        assert objectMapper != null;

        this.participantContext = participantContext;
        this.participantVerifier = participantVerifier;
        this.objectMapper = objectMapper;
    }

    @Override
    public UsernamePasswordAuthenticationToken authenticate(CachedServletRequest cachedServletRequest) throws GatekeeperFailureException {

        try {

            this.verifyFsps(cachedServletRequest);

            return this.authenticateUsingJws(cachedServletRequest);

        } catch (JsonProcessingException e) {

            LOGGER.error("Error : ", e);
            throw new GatekeeperFailureException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                                 new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, "Unable to parse the 'fspiop-signature' header."));

        } catch (GatekeeperFailureException e) {

            LOGGER.error("Error : ", e);
            throw e;

        } catch (Exception e) {

            LOGGER.error("Error : ", e);
            throw new GatekeeperFailureException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
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
            throw new GatekeeperFailureException(HttpServletResponse.SC_BAD_REQUEST,
                                                 new FspiopException(FspiopErrors.MISSING_MANDATORY_ELEMENT, "The 'fspiop-signature' header is missing."));
        }

        var signature = this.objectMapper.readValue(signatureHeader, FspiopSignature.Header.class);
        LOGGER.debug("FSPIOP_SIGNATURE : [{}]", signature);

        var publicKey = this.participantContext.publicKeys().get(payer);

        if (publicKey == null) {

            LOGGER.error("No public key found for Source FSP ({}).", payer);
            throw new GatekeeperFailureException(HttpServletResponse.SC_UNAUTHORIZED,
                                                 new FspiopException(FspiopErrors.INVALID_SIGNATURE, "No public key found for Source FSP (" + payer + ")."));
        }

        var payload = getMethod ? this.buildDummyPayload(cachedServletRequest) : cachedServletRequest.getCachedBodyAsString();
        LOGGER.debug("Payload : [{}]", payload);

        var encodedPayload = JwtBase64Util.encode(payload);
        LOGGER.debug("Encoded payload : [{}]", encodedPayload);

        var verificationOk = FspiopSignature.verify(publicKey, new Rs256Jwt.Token(signature.protectedHeader(), encodedPayload, signature.signature()));

        if (!verificationOk) {

            LOGGER.error("Signature verification failed when using Source FSP ({})'s public key.", payer);
            throw new GatekeeperFailureException(HttpServletResponse.SC_UNAUTHORIZED,
                                                 new FspiopException(FspiopErrors.INVALID_SIGNATURE,
                                                                     "Signature verification failed when using Source FSP (" + payer + ")'s public key."));
        }

        LOGGER.debug("Signature verification successful");
        return new UsernamePasswordAuthenticationToken(new Payer(payer), signature, new ArrayList<SimpleGrantedAuthority>() { });
    }

    private String buildDummyPayload(CachedServletRequest cachedServletRequest) {

        return "{\"date\":\"" + cachedServletRequest.getHeader(FspiopHeaders.Names.DATE) + "\"}";
    }

    private void verifyFsps(CachedServletRequest cachedServletRequest) {

        var source = cachedServletRequest.getHeader(FspiopHeaders.Names.FSPIOP_SOURCE);
        LOGGER.debug("FSPIOP_SOURCE : [{}]", source);

        if (source == null || source.isBlank()) {

            LOGGER.error("The 'fspiop-source' header is missing.");
            throw new GatekeeperFailureException(HttpServletResponse.SC_BAD_REQUEST,
                                                 new FspiopException(FspiopErrors.MISSING_MANDATORY_ELEMENT, "The 'fspiop-source' header or its value is missing."));
        }

        var destination = cachedServletRequest.getHeader(FspiopHeaders.Names.FSPIOP_DESTINATION);
        LOGGER.debug("FSPIOP_DESTINATION : [{}]", destination);

        if (destination == null || destination.isBlank()) {

            LOGGER.error("The 'fspiop-destination' header is missing.");
            throw new GatekeeperFailureException(HttpServletResponse.SC_BAD_REQUEST,
                                                 new FspiopException(FspiopErrors.MISSING_MANDATORY_ELEMENT, "The 'fspiop-destination' header or its value is missing."));
        }

        var uri = cachedServletRequest.getRequestURI();
        LOGGER.debug("URI : [{}]", uri);

        if (!this.participantVerifier.fspExists(source)) {

            LOGGER.error("The Source FSP ({}) does not exist.", source);
            throw new GatekeeperFailureException(HttpServletResponse.SC_NOT_ACCEPTABLE, new FspiopException(FspiopErrors.PAYER_FSP_ID_NOT_FOUND));
        }

        if (!this.participantVerifier.fspExists(destination)) {

            LOGGER.error("The Destination FSP ({}) does not exist.", destination);
            throw new GatekeeperFailureException(HttpServletResponse.SC_NOT_ACCEPTABLE, new FspiopException(FspiopErrors.PAYEE_FSP_ID_NOT_FOUND));
        }

        if (source.equals(destination)) {

            LOGGER.error("The Source FSP ({}) and the destination FSP ({}) must not be the same.", source, destination);
            throw new GatekeeperFailureException(HttpServletResponse.SC_NOT_ACCEPTABLE,
                                                 new FspiopException(FspiopErrors.DESTINATION_FSP_ERROR,
                                                                     "Source FSP (" + source + ") and Destination FSP (" + destination + ") must not be the same."));
        }

        if (destination.equals(this.participantContext.fspCode())) {

            LOGGER.error("Destination FSP ({}) must not be Hub ({}).", destination, this.participantContext.fspCode());
            throw new GatekeeperFailureException(HttpServletResponse.SC_NOT_ACCEPTABLE,
                                                 new FspiopException(FspiopErrors.DESTINATION_FSP_ERROR,
                                                                     "Destination FSP (" + destination + ") must not be Hub (" + this.participantContext.fspCode() + ")."));
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
