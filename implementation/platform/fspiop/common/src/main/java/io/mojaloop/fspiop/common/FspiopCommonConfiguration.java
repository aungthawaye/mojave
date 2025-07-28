package io.mojaloop.fspiop.common;

import io.mojaloop.fspiop.common.support.ParticipantDetails;
import org.springframework.context.annotation.Bean;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

public class FspiopCommonConfiguration {

    @Bean
    public ParticipantDetails participantSettings(FspiopCommonConfiguration.Settings settings)
        throws NoSuchAlgorithmException, InvalidKeySpecException {

        return ParticipantDetails.with(settings.fspCode(),
                                       settings.fspName(),
                                       settings.ilpSecret(),
                                       settings.signJws(),
                                       settings.verifyJws(),
                                       settings.base64PrivateKey(),
                                       settings.base64FspPublicKeys());
    }

    public interface RequiredSettings {

        FspiopCommonConfiguration.Settings fspiopCommonSettings();

    }

    public record Settings(String fspCode,
                           String fspName,
                           String ilpSecret,
                           boolean signJws,
                           boolean verifyJws,
                           String base64PrivateKey,
                           Map<String, String> base64FspPublicKeys) { }

}
