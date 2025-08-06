package io.mojaloop.connector.service.inbound;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.web.security.spring.AuthenticationErrorWriter;
import io.mojaloop.component.web.security.spring.Authenticator;
import io.mojaloop.component.web.security.spring.SpringSecurityConfiguration;
import io.mojaloop.component.web.security.spring.SpringSecurityConfigurer;
import io.mojaloop.connector.adapter.ConnectorAdapterConfiguration;
import io.mojaloop.connector.service.inbound.component.FspiopInboundErrorWriter;
import io.mojaloop.connector.service.inbound.component.FspiopInboundGatekeeper;
import io.mojaloop.fspiop.common.participant.ParticipantContext;
import io.mojaloop.fspiop.invoker.FspiopInvokerConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@Import(value = {ConnectorAdapterConfiguration.class, FspiopInvokerConfiguration.class, SpringSecurityConfiguration.class})
@ComponentScan(basePackages = {"io.mojaloop.connector.service.inbound"})
public class ConnectorInboundConfiguration implements FspiopInvokerConfiguration.RequiredBeans, SpringSecurityConfiguration.RequiredBeans {

    private final SpringSecurityConfigurer.Settings springSecuritySettings;

    private final ParticipantContext participantContext;

    private final ObjectMapper objectMapper;

    public ConnectorInboundConfiguration(SpringSecurityConfigurer.Settings springSecuritySettings,
                                         ParticipantContext participantContext,
                                         ObjectMapper objectMapper) {

        assert springSecuritySettings != null;
        assert participantContext != null;
        assert objectMapper != null;

        this.springSecuritySettings = springSecuritySettings;
        this.participantContext = participantContext;
        this.objectMapper = objectMapper;

    }

    @Bean
    @Override
    public AuthenticationErrorWriter authenticationErrorWriter() {

        return new FspiopInboundErrorWriter(this.objectMapper);
    }

    @Bean
    @Override
    public Authenticator authenticator() {

        return new FspiopInboundGatekeeper(this.springSecuritySettings, this.participantContext, this.objectMapper);
    }

    public interface RequiredBeans extends ConnectorAdapterConfiguration.RequiredBeans { }

    public interface RequiredSettings extends ConnectorAdapterConfiguration.RequiredSettings,
                                              FspiopInvokerConfiguration.RequiredSettings,
                                              SpringSecurityConfiguration.RequiredSettings {

        InboundSettings inboundSettings();

    }

    public record InboundSettings(int portNo, int maxThreads, int connectionTimeout) { }

}
