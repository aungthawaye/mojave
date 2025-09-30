package io.mojaloop.core.quoting.service;

import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.core.participant.store.ParticipantStore;
import io.mojaloop.core.quoting.domain.QuotingDomainConfiguration;
import io.mojaloop.fspiop.service.FspiopServiceConfiguration;
import io.mojaloop.fspiop.service.component.ParticipantVerifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@Configuration(proxyBeanMethods = false)
@EnableWebMvc
@EnableAsync
@ComponentScan(basePackages = "io.mojaloop.core.quoting.service")
@Import(value = {QuotingDomainConfiguration.class, FspiopServiceConfiguration.class})
public class QuotingServiceConfiguration implements QuotingDomainConfiguration.RequiredBeans, FspiopServiceConfiguration.RequiredBeans {

    private final ParticipantStore participantStore;

    public QuotingServiceConfiguration(ParticipantStore participantStore) {

        assert participantStore != null;

        this.participantStore = participantStore;
    }

    @Bean
    @Override
    public ParticipantVerifier participantVerifier() {

        return fspCode -> this.participantStore.getFspData(new FspCode(fspCode)) != null;
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer(TomcatSettings settings) {

        return factory -> factory.setPort(settings.portNo());
    }

    public interface RequiredSettings extends QuotingDomainConfiguration.RequiredSettings, FspiopServiceConfiguration.RequiredSettings {

        TomcatSettings quotingServiceTomcatSettings();

    }

    public record TomcatSettings(int portNo) { }

}
