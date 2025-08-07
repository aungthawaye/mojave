package io.mojaloop.connector.service;

import io.mojaloop.component.misc.pubsub.PubSubClient;
import io.mojaloop.component.tomcat.TomcatFactoryConfigurer;
import io.mojaloop.component.tomcat.connector.SimpleHttpConnectorDecorator;
import io.mojaloop.connector.service.inbound.ConnectorInboundConfiguration;
import io.mojaloop.connector.service.outbound.ConnectorOutboundConfiguration;
import io.mojaloop.fspiop.invoker.FspiopInvokerConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConnectorServiceConfiguration implements FspiopInvokerConfiguration.RequiredBeans {

    private final ApplicationContext applicationContext;

    public ConnectorServiceConfiguration(ApplicationContext applicationContext) {

        this.applicationContext = applicationContext;
    }

    @Bean
    public TomcatServletWebServerFactory tomcatServletWebServerFactory(ConnectorInboundConfiguration.InboundSettings inboundSettings) {

        return TomcatFactoryConfigurer.configure(this.applicationContext, this.inboundHostSettings(inboundSettings));
    }

    private TomcatFactoryConfigurer.HostSettings inboundHostSettings(ConnectorInboundConfiguration.InboundSettings inboundSettings) {

        var inboundConnectorSettings = new SimpleHttpConnectorDecorator.Settings(inboundSettings.portNo(),
                                                                                 inboundSettings.maxThreads(),
                                                                                 inboundSettings.connectionTimeout());
        var inboundConnector = new SimpleHttpConnectorDecorator(inboundConnectorSettings);

        return new TomcatFactoryConfigurer.HostSettings("inbound", "", inboundConnector, ConnectorInboundConfiguration.class);
    }

    public interface RequiredBeans extends ConnectorInboundConfiguration.RequiredBeans, FspiopInvokerConfiguration.RequiredBeans {

        PubSubClient pubSubClient();

    }

    public interface RequiredSettings extends ConnectorInboundConfiguration.RequiredSettings,
                                              ConnectorOutboundConfiguration.RequiredSettings,
                                              FspiopInvokerConfiguration.RequiredSettings { }

}
