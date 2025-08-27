package io.mojaloop.connector.sample;

import io.mojaloop.component.misc.pubsub.PubSubClient;
import io.mojaloop.component.misc.pubsub.local.LocalPubSub;
import io.mojaloop.component.misc.pubsub.local.LocalPubSubClient;
import io.mojaloop.connector.adapter.fsp.FspAdapter;
import io.mojaloop.connector.gateway.ConnectorGatewayConfiguration;
import io.mojaloop.connector.sample.adapter.SampleFspAdapter;
import org.springframework.context.annotation.Bean;

public class SampleConnectorConfiguration implements ConnectorGatewayConfiguration.RequiredBeans {

    @Bean
    @Override
    public FspAdapter fspAdapter() {

        return new SampleFspAdapter();
    }

    @Bean
    @Override
    public PubSubClient pubSubClient() {

        return new LocalPubSubClient(new LocalPubSub());
    }

}
