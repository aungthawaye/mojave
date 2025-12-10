package org.mojave.core.transaction.intercom;

import org.mojave.component.web.spring.security.AuthenticationErrorWriter;
import org.mojave.component.web.spring.security.Authenticator;
import org.mojave.core.transaction.intercom.controller.component.EmptyErrorWriter;
import org.mojave.core.transaction.intercom.controller.component.EmptyGatekeeper;
import org.springframework.context.annotation.Bean;

final class TransactionIntercomDependencies
    implements TransactionIntercomConfiguration.RequiredDependencies {

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
