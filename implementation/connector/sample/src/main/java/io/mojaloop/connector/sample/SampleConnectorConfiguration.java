package io.mojaloop.connector.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.pubsub.PubSubClient;
import io.mojaloop.component.misc.pubsub.local.LocalPubSub;
import io.mojaloop.component.misc.pubsub.local.LocalPubSubClient;
import io.mojaloop.connector.adapter.fsp.FspAdapter;
import io.mojaloop.connector.gateway.inbound.ConnectorInboundConfiguration;
import io.mojaloop.connector.gateway.outbound.ConnectorOutboundConfiguration;
import io.mojaloop.connector.sample.adapter.SampleFspAdapter;
import io.mojaloop.fspiop.common.participant.ParticipantContext;
import org.springframework.context.annotation.Bean;

public class SampleConnectorConfiguration
    implements ConnectorInboundConfiguration.RequiredBeans, ConnectorOutboundConfiguration.RequiredBeans {

    private final ParticipantContext participantContext;

    private final ObjectMapper objectMapper;

    public SampleConnectorConfiguration(ParticipantContext participantContext, ObjectMapper objectMapper) {

        assert participantContext != null;
        assert objectMapper != null;

        this.participantContext = participantContext;
        this.objectMapper = objectMapper;
    }

    @Bean
    @Override
    public FspAdapter fspAdapter() {

        return new SampleFspAdapter(this.participantContext, this.objectMapper);
    }

    @Bean
    @Override
    public PubSubClient pubSubClient() {

        return new LocalPubSubClient(new LocalPubSub());
    }

}
