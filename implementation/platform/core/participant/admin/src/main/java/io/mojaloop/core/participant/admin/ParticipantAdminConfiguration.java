package io.mojaloop.core.participant.admin;

import io.mojaloop.component.web.error.ApplicationErrorConfiguration;
import io.mojaloop.component.web.security.spring.AuthenticationErrorWriter;
import io.mojaloop.component.web.security.spring.Authenticator;
import io.mojaloop.component.web.security.spring.SpringSecurityConfiguration;
import io.mojaloop.core.participant.admin.component.BlindGatekeeper;
import io.mojaloop.core.participant.admin.component.EmptyErrorWriter;
import io.mojaloop.core.participant.domain.ParticipantDomainConfiguration;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@EnableAsync
@ComponentScan(basePackages = "io.mojaloop.core.participant.admin")
@Import(value = {ParticipantDomainConfiguration.class, ApplicationErrorConfiguration.class, SpringSecurityConfiguration.class})
public class ParticipantAdminConfiguration implements ParticipantDomainConfiguration.RequiredBeans,
                                                      SpringSecurityConfiguration.RequiredBeans {

    @Bean
    @Override
    public AuthenticationErrorWriter authenticationErrorWriter() {

        return new EmptyErrorWriter();
    }

    @Bean
    @Override
    public Authenticator authenticator() {

        return new BlindGatekeeper();
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer(TomcatSettings settings) {

        return factory -> factory.setPort(settings.portNo());
    }

    public interface RequiredSettings extends ParticipantDomainConfiguration.RequiredSettings,
                                              SpringSecurityConfiguration.RequiredSettings {

        TomcatSettings tomcatSettings();

    }

    public record TomcatSettings(int portNo) { }

}
