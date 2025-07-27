package io.mojaloop.core.lookup.service;

import io.mojaloop.common.fspiop.component.security.FspiopSpringSecurityConfiguration;
import io.mojaloop.core.lookup.domain.LookUpDomainConfiguration;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@ComponentScan(basePackages = "io.mojaloop.core.lookup.service")
@Import(value = {
    LookUpDomainConfiguration.class, FspiopSpringSecurityConfiguration.class, LookUpServiceSettings.class})
public class LookUpServiceConfiguration implements FspiopSpringSecurityConfiguration.RequiredBeans {

    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer(TomcatSettings settings) {

        return factory -> factory.setPort(settings.portNo());
    }

    public interface RequiredSettings extends FspiopSpringSecurityConfiguration.RequiredSettings {

        TomcatSettings lookUpServiceTomcatSettings();

    }

    public record TomcatSettings(int portNo) { }

}
