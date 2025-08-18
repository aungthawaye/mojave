package io.mojaloop.connector.service.outbound;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.ComponentMiscConfiguration;
import io.mojaloop.component.misc.pubsub.PubSubClient;
import io.mojaloop.component.web.security.spring.AuthenticationErrorWriter;
import io.mojaloop.component.web.security.spring.Authenticator;
import io.mojaloop.component.web.security.spring.SpringSecurityConfiguration;
import io.mojaloop.component.web.security.spring.SpringSecurityConfigurer;
import io.mojaloop.connector.service.outbound.component.FspiopOutboundErrorWriter;
import io.mojaloop.connector.service.outbound.component.FspiopOutboundGatekeeper;
import io.mojaloop.fspiop.common.participant.ParticipantContext;
import io.mojaloop.fspiop.invoker.FspiopInvokerConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@EnableAutoConfiguration
@Configuration(proxyBeanMethods = false)
@Import(value = {
    ComponentMiscConfiguration.class, FspiopInvokerConfiguration.class, SpringSecurityConfiguration.class})
@ComponentScan(basePackages = {"io.mojaloop.connector.service.outbound"})
public class ConnectorOutboundConfiguration implements ComponentMiscConfiguration.RequiredBeans,
                                                       FspiopInvokerConfiguration.RequiredBeans,
                                                       SpringSecurityConfiguration.RequiredBeans {

    private final ParticipantContext participantContext;

    private final ObjectMapper objectMapper;

    public ConnectorOutboundConfiguration(ParticipantContext participantContext, ObjectMapper objectMapper) {

        assert participantContext != null;
        assert objectMapper != null;

        this.participantContext = participantContext;
        this.objectMapper = objectMapper;

    }

    @Bean
    @Override
    public AuthenticationErrorWriter authenticationErrorWriter() {

        return new FspiopOutboundErrorWriter(this.objectMapper);
    }

    @Bean
    @Override
    public Authenticator authenticator() {

        return new FspiopOutboundGatekeeper(this.participantContext, this.objectMapper);
    }

    @Bean
    public SpringSecurityConfigurer.Settings springSecuritySettings() {

        return new SpringSecurityConfigurer.Settings("/lookup", "/quote", "/transfer");
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer(OutboundSettings outboundSettings) {

        return factory -> factory.setPort(outboundSettings.portNo());
    }

    public interface RequiredBeans {

        PubSubClient pubSubClient();

    }

    public interface RequiredSettings extends ComponentMiscConfiguration.RequiredSettings, FspiopInvokerConfiguration.RequiredSettings {

        OutboundSettings outboundSettings();

        TransactionSettings transactionSettings();

    }

    public record OutboundSettings(int portNo, int maxThreads, int connectionTimeout, int putResultTimeout, int pubSubTimeout) { }

    public record TransactionSettings(int expireAfterSeconds) { }

}
