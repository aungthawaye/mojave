package io.mojaloop.connector.service.outbound;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.ComponentMiscConfiguration;
import io.mojaloop.component.web.security.spring.AuthenticationErrorWriter;
import io.mojaloop.component.web.security.spring.Authenticator;
import io.mojaloop.component.web.security.spring.SpringSecurityConfiguration;
import io.mojaloop.component.web.security.spring.SpringSecurityConfigurer;
import io.mojaloop.connector.service.outbound.component.FspiopOutboundErrorWriter;
import io.mojaloop.connector.service.outbound.component.FspiopOutboundGatekeeper;
import io.mojaloop.fspiop.common.participant.ParticipantContext;
import io.mojaloop.fspiop.invoker.FspiopInvokerConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {
    ComponentMiscConfiguration.class, FspiopInvokerConfiguration.class, SpringSecurityConfiguration.class})
@ComponentScan(basePackages = {"io.mojaloop.connector.service.outbound"})
public class ConnectorOutboundConfiguration implements SpringSecurityConfiguration.RequiredBeans {

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

        return new FspiopOutboundGatekeeper(new SpringSecurityConfigurer.Settings("/lookup", "/quote", "/transfer"),
                                            this.participantContext,
                                            this.objectMapper);
    }

    public interface RequiredSettings extends ComponentMiscConfiguration.RequiredSettings, FspiopInvokerConfiguration.RequiredSettings {

        OutboundSettings outboundSettings();

    }

    public record OutboundSettings(int portNo, int maxThreads, int connectionTimeout) { }

}
