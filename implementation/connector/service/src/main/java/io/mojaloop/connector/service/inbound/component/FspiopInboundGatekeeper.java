package io.mojaloop.connector.service.inbound.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.jwt.JwtBase64Util;
import io.mojaloop.component.misc.jwt.Rs256Jwt;
import io.mojaloop.component.web.request.CachedServletRequest;
import io.mojaloop.component.web.security.spring.AuthenticationFailureException;
import io.mojaloop.component.web.security.spring.Authenticator;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.participant.ParticipantContext;
import io.mojaloop.fspiop.common.type.Source;
import io.mojaloop.fspiop.component.handy.FspiopHeaders;
import io.mojaloop.fspiop.component.handy.FspiopSignature;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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

    private UsernamePasswordAuthenticationToken authenticateUsingJws(CachedServletRequest cachedServletRequest)
        throws JsonProcessingException {

        var source = cachedServletRequest.getHeader(FspiopHeaders.Names.FSPIOP_SOURCE);

        if (!this.participantContext.verifyJws()) {

            return new UsernamePasswordAuthenticationToken(source,
                                                           new FspiopSignature.Header(null, null),
                                                           new ArrayList<SimpleGrantedAuthority>());
        }

        var getMethod = cachedServletRequest.getMethod().equalsIgnoreCase("GET");
        var signatureHeader = cachedServletRequest.getHeader(FspiopHeaders.Names.FSPIOP_SIGNATURE);

        if (signatureHeader == null || signatureHeader.isBlank()) {

            LOGGER.error("The 'fspiop-signature' header is missing.");
            throw new GatekeeperFailureException(HttpServletResponse.SC_BAD_REQUEST,
                                                 new FspiopException(FspiopErrors.MISSING_MANDATORY_ELEMENT,
                                                                     "The 'fspiop-signature' header is missing."));
        }

        var signature = this.objectMapper.readValue(signatureHeader, FspiopSignature.Header.class);
        LOGGER.debug("FSPIOP_SIGNATURE : [{}]", signature);

        var publicKey = this.participantContext.publicKeys().get(source);

        if (publicKey == null) {

            LOGGER.error("No public key found for Source FSP ({}).", source);
            throw new GatekeeperFailureException(HttpServletResponse.SC_UNAUTHORIZED,
                                                 new FspiopException(FspiopErrors.INVALID_SIGNATURE,
                                                                     "No public key found for Source FSP (" + source + ")."));
        }

        var payload = getMethod ? this.buildDummyPayload(cachedServletRequest) : cachedServletRequest.getCachedBodyAsString();
        LOGGER.debug("Payload : [{}]", payload);

        var encodedPayload = JwtBase64Util.encode(payload);
        LOGGER.debug("Encoded payload : [{}]", encodedPayload);

        var verificationOk = FspiopSignature.verify(publicKey,
                                                    new Rs256Jwt.Token(signature.protectedHeader(), encodedPayload, signature.signature()));

        if (!verificationOk) {

            LOGGER.error("Signature verification failed when using Source FSP ({})'s public key.", source);
            throw new GatekeeperFailureException(HttpServletResponse.SC_UNAUTHORIZED,
                                                 new FspiopException(FspiopErrors.INVALID_SIGNATURE,
                                                                     "Signature verification failed when using Source FSP (" + source +
                                                                         ")'s public key."));
        }

        LOGGER.debug("Signature verification successful");
        return new UsernamePasswordAuthenticationToken(new Source(source), signature, new ArrayList<SimpleGrantedAuthority>() { });
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
                                                 new FspiopException(FspiopErrors.MISSING_MANDATORY_ELEMENT,
                                                                     "The 'fspiop-source' header or its value is missing."));
        }

        var destination = cachedServletRequest.getHeader(FspiopHeaders.Names.FSPIOP_DESTINATION);
        LOGGER.debug("FSPIOP_DESTINATION : [{}]", destination);

        if (destination == null || destination.isBlank()) {

            LOGGER.error("The 'fspiop-destination' header is missing.");
            throw new GatekeeperFailureException(HttpServletResponse.SC_BAD_REQUEST,
                                                 new FspiopException(FspiopErrors.MISSING_MANDATORY_ELEMENT,
                                                                     "The 'fspiop-destination' header or its value is missing."));
        }

        if (!this.participantContext.fspCode().equalsIgnoreCase(destination)) {

            LOGGER.error("The Destination FSP ({}) is not valid.", source);
            throw new GatekeeperFailureException(HttpServletResponse.SC_NOT_ACCEPTABLE,
                                                 new FspiopException(FspiopErrors.GENERIC_PAYEE_REJECTION,
                                                                     "The received request does not pertain to this FSP and " +
                                                                         "seems to have been misrouted."));
        }

        if (source.equals(destination)) {

            LOGGER.error("The Source FSP ({}) and the destination FSP ({}) must not be the same.", source, destination);
            throw new GatekeeperFailureException(HttpServletResponse.SC_NOT_ACCEPTABLE,
                                                 new FspiopException(FspiopErrors.DESTINATION_FSP_ERROR,
                                                                     "Source FSP (" + source + ") and Destination FSP (" + destination +
                                                                         ") must not be the same."));
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
