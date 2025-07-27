package io.mojaloop.common.fspiop;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.common.component.ComponentConfiguration;
import io.mojaloop.common.fspiop.component.retrofit.FspiopErrorDecoder;
import io.mojaloop.common.fspiop.component.retrofit.FspiopJwsSigningInterceptor;
import io.mojaloop.common.fspiop.support.ParticipantDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

@Import(value = {ComponentConfiguration.class})
public class FspiopConfiguration {

    @Bean
    public FspiopErrorDecoder fspiopErrorDecoder(ObjectMapper objectMapper) {

        return new FspiopErrorDecoder(objectMapper);
    }

    @Bean
    public FspiopJwsSigningInterceptor fspiopSignatureInterceptor(ParticipantDetails participantDetails, ObjectMapper objectMapper) {

        return new FspiopJwsSigningInterceptor(participantDetails, objectMapper);
    }

    @Bean
    public ParticipantDetails participantSettings(FspiopConfiguration.Settings settings)
        throws NoSuchAlgorithmException, InvalidKeySpecException {

        return ParticipantDetails.with(settings.fspCode(),
                                       settings.fspName(),
                                       settings.ilpSecret(),
                                       settings.signJws(),
                                       settings.verifyJws(),
                                       settings.base64PrivateKey(),
                                       settings.base64FspPublicKeys());
    }

    public interface RequiredBeans { }

    public interface RequiredSettings extends ComponentConfiguration.RequiredSettings {

        Settings fspiopConfigurationSettings();

    }

    public record Settings(String fspCode,
                           String fspName,
                           String ilpSecret,
                           boolean signJws,
                           boolean verifyJws,
                           String base64PrivateKey,
                           Map<String, String> base64FspPublicKeys) { }

}
