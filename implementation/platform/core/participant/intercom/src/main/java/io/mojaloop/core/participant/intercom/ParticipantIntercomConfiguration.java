package io.mojaloop.core.participant.intercom;

import io.mojaloop.component.web.error.ApplicationErrorConfiguration;
import io.mojaloop.component.web.security.spring.AuthenticationErrorWriter;
import io.mojaloop.component.web.security.spring.Authenticator;
import io.mojaloop.component.web.security.spring.SpringSecurityConfiguration;
import io.mojaloop.core.participant.domain.ParticipantDomainConfiguration;
import io.mojaloop.core.participant.intercom.component.BlindGatekeeper;
import io.mojaloop.core.participant.intercom.component.EmptyErrorWriter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableAutoConfiguration
@Configuration(proxyBeanMethods = false)
@EnableWebMvc
@EnableAsync
@ComponentScan(basePackages = "io.mojaloop.core.participant.intercom")
@Import(value = {ParticipantDomainConfiguration.class, ApplicationErrorConfiguration.class, SpringSecurityConfiguration.class})
public class ParticipantIntercomConfiguration implements ParticipantDomainConfiguration.RequiredBeans,
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
