package io.mojaloop.connector.gateway.outbound.component;

import io.mojaloop.component.misc.crypto.Rs256;
import io.mojaloop.component.misc.jwt.JwtBase64Util;
import io.mojaloop.component.misc.jwt.Rs256Jwt;
import io.mojaloop.component.web.request.CachedServletRequest;
import io.mojaloop.component.web.security.spring.AuthenticationFailureException;
import io.mojaloop.component.web.security.spring.Authenticator;
import io.mojaloop.connector.gateway.outbound.ConnectorOutboundConfiguration;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

public class FspiopOutboundGatekeeper implements Authenticator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FspiopOutboundGatekeeper.class);

    private final ConnectorOutboundConfiguration.OutboundSettings outboundSettings;

    private final PublicKey publicKey;

    public FspiopOutboundGatekeeper(ConnectorOutboundConfiguration.OutboundSettings outboundSettings) {

        assert outboundSettings != null;

        this.outboundSettings = outboundSettings;

        try {

            this.publicKey = Rs256.publicKeyFromPem(outboundSettings.publicKeyPem());

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UsernamePasswordAuthenticationToken authenticate(CachedServletRequest cachedServletRequest)
        throws AuthenticationFailureException {

        if (!this.outboundSettings.secured()) {

            return new UsernamePasswordAuthenticationToken("FSP", "skipped", new ArrayList<SimpleGrantedAuthority>() { });
        }

        var authorization = cachedServletRequest.getHeader("Authorization");

        if (authorization == null || SecurityContextHolder.getContext().getAuthentication() == null) {

            throw new FspiopOutboundGatekeeper.GatekeeperFailureException(HttpServletResponse.SC_UNAUTHORIZED,
                                                                          new FspiopException(FspiopErrors.MISSING_MANDATORY_ELEMENT,
                                                                                              "Authorization header or its value is missing."));
        }

        var payload = cachedServletRequest.getCachedBodyAsString();
        var base64Payload = JwtBase64Util.encode(payload);
        var parts = authorization.split("\\.", -1);
        var header = parts[0];
        var signature = parts[2];

        var verificationOk = Rs256Jwt.verify(this.publicKey, new Rs256Jwt.Token(header, base64Payload, signature));

        if (!verificationOk) {

            LOGGER.error("Signature verification failed when using FSP's public key.");
            throw new FspiopOutboundGatekeeper.GatekeeperFailureException(HttpServletResponse.SC_UNAUTHORIZED,
                                                                          new FspiopException(FspiopErrors.INVALID_SIGNATURE,
                                                                                              "Invalid signature."));
        }

        LOGGER.debug("Signature verification successful");
        return new UsernamePasswordAuthenticationToken("FSP", authorization, new ArrayList<SimpleGrantedAuthority>() { });
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
