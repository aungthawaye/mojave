package io.mojaloop.connector.service.inbound;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.ComponentMiscConfiguration;
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
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableAsync
@Import(value = {
    ComponentMiscConfiguration.class, ConnectorAdapterConfiguration.class, FspiopInvokerConfiguration.class,
    SpringSecurityConfiguration.class})
@ComponentScan(basePackages = {"io.mojaloop.connector.service.inbound"})
public class ConnectorInboundConfiguration implements ComponentMiscConfiguration.RequiredBeans,
                                                      FspiopInvokerConfiguration.RequiredBeans,
                                                      SpringSecurityConfiguration.RequiredBeans {

    private final ParticipantContext participantContext;

    private final ObjectMapper objectMapper;

    public ConnectorInboundConfiguration(ParticipantContext participantContext, ObjectMapper objectMapper) {

        assert participantContext != null;
        assert objectMapper != null;

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

        return new FspiopInboundGatekeeper(new SpringSecurityConfigurer.Settings("/parties/**", "/quotes/**", "/transfers/**"),
                                           this.participantContext,
                                           this.objectMapper);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));  // or your allowed origins
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;

    }

    public interface RequiredBeans extends ConnectorAdapterConfiguration.RequiredBeans { }

    public interface RequiredSettings extends ComponentMiscConfiguration.RequiredSettings,
                                              ConnectorAdapterConfiguration.RequiredSettings,
                                              FspiopInvokerConfiguration.RequiredSettings {

        InboundSettings inboundSettings();

    }

    public record InboundSettings(int portNo, int maxThreads, int connectionTimeout) { }

}
