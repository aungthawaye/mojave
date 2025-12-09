package io.mojaloop.core.participant.admin;

import io.mojaloop.component.web.spring.security.AuthenticationErrorWriter;
import io.mojaloop.component.web.spring.security.Authenticator;
import io.mojaloop.core.participant.admin.controller.component.EmptyErrorWriter;
import io.mojaloop.core.participant.admin.controller.component.EmptyGatekeeper;
import org.springframework.context.annotation.Bean;

final class ParticipantAdminDependencies
    implements ParticipantAdminConfiguration.RequiredDependencies {

    @Bean
    @Override
    public AuthenticationErrorWriter authenticationErrorWriter() {

        return new EmptyErrorWriter();
    }

    @Bean
    @Override
    public Authenticator authenticator() {

        return new EmptyGatekeeper();
    }

}
