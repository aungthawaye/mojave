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

package io.mojaloop.common.fspiop.component.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.common.component.http.CachedBodyHttpServletRequest;
import io.mojaloop.common.component.spring.security.Authenticator;
import io.mojaloop.common.component.spring.security.SpringSecurityConfigurer;
import io.mojaloop.common.datatype.type.fspiop.FspCode;
import io.mojaloop.common.fspiop.component.FspiopHeaders;
import io.mojaloop.common.fspiop.component.FspiopSignature;
import io.mojaloop.common.fspiop.support.ParticipantDetails;
import io.mojaloop.common.fspiop.support.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class FspiopJwsVerificationAuthenticator implements Authenticator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FspiopJwsVerificationAuthenticator.class);

    private final ParticipantDetails participantDetails;

    private final ObjectMapper objectMapper;

    public FspiopJwsVerificationAuthenticator(SpringSecurityConfigurer.Settings settings,
                                              ParticipantDetails participantDetails,
                                              ObjectMapper objectMapper) {

        assert participantDetails != null;
        assert objectMapper != null;

        this.participantDetails = participantDetails;
        this.objectMapper = objectMapper;
    }

    @Override
    public UsernamePasswordAuthenticationToken authenticate(CachedBodyHttpServletRequest cachedBodyHttpServletRequest)
        throws AuthenticatorException {

        try {

            var source = cachedBodyHttpServletRequest.getHeader(FspiopHeaders.Names.FSPIOP_SOURCE);
            LOGGER.debug("FSPIOP_SOURCE : [{}]", source);

            List<SimpleGrantedAuthority> authorities = new ArrayList<>();

            if (!this.participantDetails.verifyJws()) {

                return new UsernamePasswordAuthenticationToken(source, new FspiopSignature.Header(null, null), authorities);
            }

            var getMethod = cachedBodyHttpServletRequest.getMethod().equalsIgnoreCase("GET");

            var destination = cachedBodyHttpServletRequest.getHeader(FspiopHeaders.Names.FSPIOP_DESTINATION);
            LOGGER.debug("FSPIOP_DESTINATION : [{}]", destination);

            var signatureHeader = cachedBodyHttpServletRequest.getHeader(FspiopHeaders.Names.FSPIOP_SIGNATURE);

            if (signatureHeader == null) {

                throw new Authenticator.AuthenticatorException("The 'fspiop-signature' header is missing.");
            }

            var signature = this.objectMapper.readValue(signatureHeader, FspiopSignature.Header.class);
            LOGGER.debug("FSPIOP_SIGNATURE : [{}]", signature);

            var publicKey = this.participantDetails.publicKeys().get(source);

            var payload = getMethod ? "{}" : cachedBodyHttpServletRequest.getCachedBodyAsString();
            LOGGER.debug("Payload : [{}]", payload);

            var verificationOk = FspiopSignature.verify(publicKey,
                                                        signature.protectedHeader(),
                                                        Base64.getUrlEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8)),
                                                        signature.signature());

            if (!verificationOk) {

                LOGGER.error("Signature verification failed when using Source FSP (" + source + ")'s public key.");
                throw new Authenticator.AuthenticatorException(
                    "Signature verification failed when using Source FSP (" + source + ")'s public key.");
            }

            LOGGER.debug("Signature verification successful");
            return new UsernamePasswordAuthenticationToken(new Source(new FspCode(source)), signature, authorities);

        } catch (JsonProcessingException e) {

            LOGGER.error("Error : ", e);
            throw new AuthenticatorException(e.getMessage());
        }
    }

}
