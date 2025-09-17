package io.mojaloop.connector.gateway.outbound;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.MiscConfiguration;
import io.mojaloop.component.misc.pubsub.PubSubClient;
import io.mojaloop.component.web.security.spring.AuthenticationErrorWriter;
import io.mojaloop.component.web.security.spring.Authenticator;
import io.mojaloop.component.web.security.spring.SpringSecurityConfiguration;
import io.mojaloop.component.web.security.spring.SpringSecurityConfigurer;
import io.mojaloop.connector.gateway.outbound.component.FspiopOutboundGatekeeper;
import io.mojaloop.fspiop.invoker.FspiopInvokerConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableAutoConfiguration
@EnableAsync
@EnableWebMvc
@Import(value = {MiscConfiguration.class, FspiopInvokerConfiguration.class, SpringSecurityConfiguration.class})
@ComponentScan(basePackages = {"io.mojaloop.connector.gateway.outbound"})
public class ConnectorOutboundConfiguration
    implements MiscConfiguration.RequiredBeans, FspiopInvokerConfiguration.RequiredBeans, SpringSecurityConfiguration.RequiredBeans, SpringSecurityConfiguration.RequiredSettings {

    private final OutboundSettings outboundSettings;

    private final ObjectMapper objectMapper;

    public ConnectorOutboundConfiguration(OutboundSettings outboundSettings, ObjectMapper objectMapper) {

        assert outboundSettings != null;
        assert objectMapper != null;

        this.outboundSettings = outboundSettings;
        this.objectMapper = objectMapper;

    }

    @Bean
    @Override
    public SpringSecurityConfigurer.Settings springSecuritySettings() {

        return new SpringSecurityConfigurer.Settings(new String[]{"/lookup", "/quote", "/transfer"});
    }

    @Bean
    @Override
    public AuthenticationErrorWriter authenticationErrorWriter() {

        return new FspiopOutboundGatekeeper.ErrorWriter(this.objectMapper);
    }

    @Bean
    @Override
    public Authenticator authenticator() {

        return new FspiopOutboundGatekeeper(this.outboundSettings);
    }

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> webServerFactoryCustomizer(OutboundSettings outboundSettings) {

        return factory -> {

            factory.setPort(outboundSettings.portNo());

            factory.addConnectorCustomizers(connector -> {
                var protocol = (org.apache.coyote.http11.AbstractHttp11Protocol<?>) connector.getProtocolHandler();
                protocol.setConnectionTimeout(outboundSettings.connectionTimeout());
                protocol.setMaxThreads(outboundSettings.maxThreads());
            });
        };
    }

    public interface RequiredBeans {

        PubSubClient pubSubClient();

    }

    public interface RequiredSettings
        extends MiscConfiguration.RequiredSettings, FspiopInvokerConfiguration.RequiredSettings {

        OutboundSettings outboundSettings();

        TransactionSettings transactionSettings();

    }

    public record OutboundSettings(int portNo,
                                   int maxThreads,
                                   int connectionTimeout,
                                   int putResultTimeout,
                                   int pubSubTimeout,
                                   String publicKeyPem,
                                   boolean secured) { }

    public record TransactionSettings(int expireAfterSeconds) { }

}
