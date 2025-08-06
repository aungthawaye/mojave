package io.mojaloop.connector.service;

import io.mojaloop.component.misc.pubsub.PubSubClient;
import io.mojaloop.component.tomcat.TomcatFactoryConfigurer;
import io.mojaloop.component.tomcat.connector.SimpleHttpConnectorDecorator;
import io.mojaloop.connector.service.inbound.ConnectorInboundConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

public class ConnectorServiceConfiguration {

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

    public interface RequiredBeans extends ConnectorInboundConfiguration.RequiredBeans {

        PubSubClient pubSubClient();

    }

    public interface RequiredSettings extends ConnectorInboundConfiguration.RequiredSettings { }

}
