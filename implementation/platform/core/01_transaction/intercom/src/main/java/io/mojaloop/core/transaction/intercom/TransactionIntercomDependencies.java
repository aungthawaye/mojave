package io.mojaloop.core.transaction.intercom;

import io.mojaloop.component.web.spring.security.AuthenticationErrorWriter;
import io.mojaloop.component.web.spring.security.Authenticator;
import io.mojaloop.core.transaction.intercom.controller.component.EmptyErrorWriter;
import io.mojaloop.core.transaction.intercom.controller.component.EmptyGatekeeper;
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
