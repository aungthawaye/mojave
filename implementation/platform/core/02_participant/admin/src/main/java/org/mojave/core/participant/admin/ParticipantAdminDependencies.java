package org.mojave.core.participant.admin;

import org.mojave.component.web.spring.security.AuthenticationErrorWriter;
import org.mojave.component.web.spring.security.Authenticator;
import org.mojave.core.participant.admin.controller.component.EmptyErrorWriter;
import org.mojave.core.participant.admin.controller.component.EmptyGatekeeper;
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
