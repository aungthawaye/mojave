package io.mojaloop.connector.gateway.inbound.command.parties;

import io.mojaloop.component.misc.pubsub.PubSubClient;
import io.mojaloop.connector.gateway.inbound.data.PartiesResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
class HandlePartiesResponseCommandHandler implements HandlePartiesResponseCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandlePartiesResponseCommandHandler.class.getName());

    private final PubSubClient pubSubClient;

    public HandlePartiesResponseCommandHandler(PubSubClient pubSubClient) {

        assert null != pubSubClient;

        this.pubSubClient = pubSubClient;
    }

    @Override
    public Output execute(Input input) {

        var channel = "parties:" + input.partyIdType().name() + "/" + input.partyId() + (input.subId() != null ? "/" + input.subId() : "");
        LOGGER.info("Publishing parties result to channel : {}", channel);

        this.pubSubClient.publish(channel, new PartiesResult(input.partyIdType(), input.partyId(), input.subId(), input.response()));
        LOGGER.info("Published parties result to channel : {}", channel);

        return new Output();
    }

}
