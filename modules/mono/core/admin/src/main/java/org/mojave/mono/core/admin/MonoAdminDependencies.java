package org.mojave.mono.core.admin;

import org.mojave.component.web.spring.security.AuthenticationErrorWriter;
import org.mojave.component.web.spring.security.Authenticator;
import org.mojave.mono.core.admin.component.EmptyErrorWriter;
import org.mojave.mono.core.admin.component.EmptyGatekeeper;
import org.springframework.context.annotation.Bean;

public class MonoAdminDependencies implements MonoAdminConfiguration.RequiredDependencies {

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
