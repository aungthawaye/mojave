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
import io.mojaloop.component.web.request.CachedServletRequest;
import io.mojaloop.component.web.security.spring.AuthenticationFailureException;
import io.mojaloop.component.web.security.spring.Authenticator;
import io.mojaloop.component.web.security.spring.SpringSecurityConfigurer;
import io.mojaloop.fspiop.common.data.ParticipantDetails;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.handy.FspiopHeaders;
import io.mojaloop.fspiop.common.handy.FspiopSignature;
import io.mojaloop.fspiop.common.type.Source;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

public class FspiopServiceGatekeeper implements Authenticator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FspiopServiceGatekeeper.class);

    private final ParticipantDetails participantDetails;

    private final ParticipantVerifier participantVerifier;

    private final ObjectMapper objectMapper;

    public FspiopServiceGatekeeper(SpringSecurityConfigurer.Settings settings,
                                   ParticipantDetails participantDetails,
                                   ParticipantVerifier participantVerifier,
                                   ObjectMapper objectMapper) {

        assert participantDetails != null;
        assert participantVerifier != null;
        assert objectMapper != null;

        this.participantDetails = participantDetails;
        this.participantVerifier = participantVerifier;
        this.objectMapper = objectMapper;
    }

    @Override
    public UsernamePasswordAuthenticationToken authenticate(CachedServletRequest cachedServletRequest) throws GatekeeperFailureException {

        try {

            var source = cachedServletRequest.getHeader(FspiopHeaders.Names.FSPIOP_SOURCE);
            LOGGER.debug("FSPIOP_SOURCE : [{}]", source);

            var destination = cachedServletRequest.getHeader(FspiopHeaders.Names.FSPIOP_DESTINATION);
            LOGGER.debug("FSPIOP_DESTINATION : [{}]", destination);

            if (source == null) {

                LOGGER.error("The 'fspiop-source' header is missing.");
                throw new GatekeeperFailureException(HttpServletResponse.SC_BAD_REQUEST,
                                                     new FspiopException(FspiopErrors.MISSING_MANDATORY_ELEMENT,
                                                                         "The 'fspiop-source' header is missing."));
            }

            if (!this.participantVerifier.fspExists(source)) {

                LOGGER.error("The Source FSP ({}) does not exist.", source);
                throw new GatekeeperFailureException(HttpServletResponse.SC_NOT_ACCEPTABLE,
                                                     new FspiopException(FspiopErrors.PAYER_FSP_ID_NOT_FOUND));
            }

            if (destination != null && !this.participantVerifier.fspExists(destination)) {

                LOGGER.error("The Destination FSP ({}) does not exist.", destination);
                throw new GatekeeperFailureException(HttpServletResponse.SC_NOT_ACCEPTABLE,
                                                     new FspiopException(FspiopErrors.PAYEE_FSP_ID_NOT_FOUND));
            }

            if (!this.participantDetails.verifyJws()) {

                return new UsernamePasswordAuthenticationToken(source,
                                                               new FspiopSignature.Header(null, null),
                                                               new ArrayList<SimpleGrantedAuthority>());
            }

            var getMethod = cachedServletRequest.getMethod().equalsIgnoreCase("GET");

            var signatureHeader = cachedServletRequest.getHeader(FspiopHeaders.Names.FSPIOP_SIGNATURE);

            if (signatureHeader == null) {

                LOGGER.error("The 'fspiop-signature' header is missing.");
                throw new GatekeeperFailureException(HttpServletResponse.SC_BAD_REQUEST,
                                                     new FspiopException(FspiopErrors.MISSING_MANDATORY_ELEMENT,
                                                                         "The 'fspiop-signature' header is missing."));
            }

            var signature = this.objectMapper.readValue(signatureHeader, FspiopSignature.Header.class);
            LOGGER.debug("FSPIOP_SIGNATURE : [{}]", signature);

            var publicKey = this.participantDetails.publicKeys().get(source);

            if (publicKey == null) {

                LOGGER.error("No public key found for Source FSP ({}).", source);
                throw new GatekeeperFailureException(HttpServletResponse.SC_UNAUTHORIZED,
                                                     new FspiopException(FspiopErrors.INVALID_SIGNATURE,
                                                                         "No public key found for Source FSP (" + source + ")."));
            }

            var payload = getMethod ? "{}" : cachedServletRequest.getCachedBodyAsString();
            LOGGER.debug("Payload : [{}]", payload);

            var verificationOk = FspiopSignature.verify(publicKey,
                                                        signature.protectedHeader(),
                                                        Base64.getUrlEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8)),
                                                        signature.signature());

            if (!verificationOk) {

                LOGGER.error("Signature verification failed when using Source FSP ({})'s public key.", source);
                throw new GatekeeperFailureException(HttpServletResponse.SC_UNAUTHORIZED,
                                                     new FspiopException(FspiopErrors.INVALID_SIGNATURE,
                                                                         "Signature verification failed when using Source FSP (" + source +
                                                                             ")'s public key."));
            }

            LOGGER.debug("Signature verification successful");
            return new UsernamePasswordAuthenticationToken(new Source(source), signature, new ArrayList<SimpleGrantedAuthority>() { });

        } catch (JsonProcessingException e) {

            LOGGER.error("Error : ", e);
            throw new GatekeeperFailureException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                                 new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR,
                                                                     "Unable to parse the 'fspiop-signature' header."));

        } catch (GatekeeperFailureException e) {

            LOGGER.error("Error : ", e);
            throw e;

        } catch (Exception e) {

            LOGGER.error("Error : ", e);
            throw new GatekeeperFailureException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                                 new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR,
                                                                     "An unexpected error occurred while authenticating the request."));
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
