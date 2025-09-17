package io.mojaloop.connector.gateway.inbound;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.MiscConfiguration;
import io.mojaloop.component.misc.handy.P12Reader;
import io.mojaloop.component.misc.pubsub.PubSubClient;
import io.mojaloop.component.tomcat.connector.MutualTLSConnectorDecorator;
import io.mojaloop.component.web.security.spring.AuthenticationErrorWriter;
import io.mojaloop.component.web.security.spring.Authenticator;
import io.mojaloop.component.web.security.spring.SpringSecurityConfiguration;
import io.mojaloop.connector.adapter.ConnectorAdapterConfiguration;
import io.mojaloop.connector.gateway.inbound.component.FspiopInboundGatekeeper;
import io.mojaloop.fspiop.common.participant.ParticipantContext;
import io.mojaloop.fspiop.invoker.FspiopInvokerConfiguration;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

@EnableAutoConfiguration
@Configuration(proxyBeanMethods = false)
@EnableAsync
@EnableWebMvc
@Import(value = {MiscConfiguration.class, ConnectorAdapterConfiguration.class, FspiopInvokerConfiguration.class, SpringSecurityConfiguration.class,})
@ComponentScan(basePackages = {"io.mojaloop.connector.gateway.inbound"})
public class ConnectorInboundConfiguration
    implements MiscConfiguration.RequiredBeans, FspiopInvokerConfiguration.RequiredBeans, SpringSecurityConfiguration.RequiredBeans {

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

        return new FspiopInboundGatekeeper.ErrorWriter(this.objectMapper);
    }

    @Bean
    @Override
    public Authenticator authenticator() {

        return new FspiopInboundGatekeeper(this.participantContext, this.objectMapper);
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

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> webServerFactoryCustomizer(InboundSettings inboundSettings) {

        return factory -> {
            // existing port configuration
            factory.setPort(inboundSettings.portNo());

            factory.addConnectorCustomizers(connector -> {

                var protocolHandler = connector.getProtocolHandler();

                if (protocolHandler instanceof Http11NioProtocol protocol) {

                    protocol.setConnectionTimeout(inboundSettings.connectionTimeout());
                    protocol.setMaxThreads(inboundSettings.maxThreads());

                    if (inboundSettings.useMutualTls()) {

                        try {

                            var keystoreSettings = inboundSettings.keyStoreSettings();
                            var truststoreSettings = inboundSettings.trustStoreSettings();
                            var connectorSettings = new MutualTLSConnectorDecorator.Settings(inboundSettings.portNo(), inboundSettings.maxThreads(),
                                                                                             inboundSettings.connectionTimeout(),
                                                                                             new MutualTLSConnectorDecorator.Settings.TrustStoreSettings(
                                                                                                 truststoreSettings.contentType(),
                                                                                                 truststoreSettings.contentValue(),
                                                                                                 truststoreSettings.password),
                                                                                             new MutualTLSConnectorDecorator.Settings.KeyStoreSettings(
                                                                                                 keystoreSettings.contentType(),
                                                                                                 keystoreSettings.contentValue(), keystoreSettings.password,
                                                                                                 keystoreSettings.keyAlias()));

                            var decorator = new MutualTLSConnectorDecorator(connectorSettings);

                        } catch (Exception e) {

                            throw new RuntimeException(e);
                        }

                    }
                }

            });
        };
    }

    public interface RequiredBeans extends ConnectorAdapterConfiguration.RequiredBeans {

        PubSubClient pubSubClient();

    }

    public interface RequiredSettings extends MiscConfiguration.RequiredSettings,
                                              ConnectorAdapterConfiguration.RequiredSettings,
                                              FspiopInvokerConfiguration.RequiredSettings,
                                              SpringSecurityConfiguration.RequiredSettings {

        InboundSettings inboundSettings();

    }

    public record InboundSettings(int portNo,
                                  int maxThreads,
                                  int connectionTimeout,
                                  boolean useMutualTls,
                                  KeyStoreSettings keyStoreSettings,
                                  TrustStoreSettings trustStoreSettings) {

        public record KeyStoreSettings(P12Reader.ContentType contentType, String contentValue, String password, String keyAlias) { }

        public record TrustStoreSettings(P12Reader.ContentType contentType, String contentValue, String password) { }

    }

}
