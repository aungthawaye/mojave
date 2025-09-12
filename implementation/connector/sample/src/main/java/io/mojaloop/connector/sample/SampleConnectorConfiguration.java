package io.mojaloop.connector.sample;

import io.mojaloop.component.misc.pubsub.PubSubClient;
import io.mojaloop.component.misc.pubsub.local.LocalPubSub;
import io.mojaloop.component.misc.pubsub.local.LocalPubSubClient;
import io.mojaloop.connector.adapter.fsp.client.FspClient;
import io.mojaloop.connector.gateway.ConnectorGatewayConfiguration;
import io.mojaloop.connector.sample.adapter.client.SampleFspClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"io.mojaloop.connector.sample"})
public class SampleConnectorConfiguration implements ConnectorGatewayConfiguration.RequiredBeans {

    public SampleConnectorConfiguration() {

    }

    @Bean
    @Override
    public FspClient fspClient() {

        return new SampleFspClient();
    }

    @Bean
    @Override
    public PubSubClient pubSubClient() {

        return new LocalPubSubClient(new LocalPubSub());
    }

}
