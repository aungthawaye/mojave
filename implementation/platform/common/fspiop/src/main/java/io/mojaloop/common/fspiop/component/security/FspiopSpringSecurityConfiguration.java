package io.mojaloop.common.fspiop.component.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.common.component.spring.security.AuthenticationErrorWriter;
import io.mojaloop.common.component.spring.security.Authenticator;
import io.mojaloop.common.component.spring.security.SpringSecurityConfiguration;
import io.mojaloop.common.component.spring.security.SpringSecurityConfigurer;
import io.mojaloop.common.fspiop.support.ParticipantDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(value = {SpringSecurityConfiguration.class})
public class FspiopSpringSecurityConfiguration implements SpringSecurityConfiguration.RequiredBeans {

    private final SpringSecurityConfigurer.Settings springSecuritySettings;

    private final ParticipantDetails participantDetails;

    private final ObjectMapper objectMapper;

    public FspiopSpringSecurityConfiguration(SpringSecurityConfigurer.Settings springSecuritySettings,
                                             ParticipantDetails participantDetails,
                                             ObjectMapper objectMapper) {

        assert springSecuritySettings != null;
        assert participantDetails != null;
        assert objectMapper != null;

        this.springSecuritySettings = springSecuritySettings;
        this.participantDetails = participantDetails;
        this.objectMapper = objectMapper;

    }

    @Bean
    @Override
    public AuthenticationErrorWriter authenticationErrorWriter() {

        return new FspiopAuthenticationErrorWriter(this.objectMapper);
    }

    @Bean
    @Override
    public Authenticator authenticator() {

        return new FspiopJwsVerificationAuthenticator(this.springSecuritySettings, this.participantDetails, this.objectMapper);
    }

    public interface RequiredBeans { }

    public interface RequiredSettings extends SpringSecurityConfiguration.RequiredSettings { }

}
