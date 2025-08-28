package io.mojaloop.core.participant.admin;

import io.mojaloop.component.web.error.RestErrorConfiguration;
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
@Import(value = {ParticipantDomainConfiguration.class, RestErrorConfiguration.class})
public class ParticipantAdminConfiguration implements ParticipantDomainConfiguration.RequiredBeans {

    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer(TomcatSettings settings) {

        return factory -> factory.setPort(settings.portNo());
    }

    public interface RequiredSettings extends ParticipantDomainConfiguration.RequiredSettings {

        TomcatSettings tomcatSettings();

    }

    public record TomcatSettings(int portNo) { }

}
