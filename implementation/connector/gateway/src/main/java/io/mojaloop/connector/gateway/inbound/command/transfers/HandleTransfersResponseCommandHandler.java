package io.mojaloop.connector.gateway.inbound.command.transfers;

import io.mojaloop.component.misc.pubsub.PubSubClient;
import io.mojaloop.connector.gateway.inbound.data.TransfersResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
class HandleTransfersResponseCommandHandler implements HandleTransfersResponseCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandleTransfersResponseCommandHandler.class.getName());

    private final PubSubClient pubSubClient;

    public HandleTransfersResponseCommandHandler(PubSubClient pubSubClient) {

        assert null != pubSubClient;

        this.pubSubClient = pubSubClient;
    }

    @Override
    public Output execute(Input input) {

        var channel = "transfers:" + input.transferId();
        LOGGER.info("Publishing transfers result to channel : {}", channel);

        this.pubSubClient.publish(channel, new TransfersResult(input.transferId(), input.response()));
        LOGGER.info("Published transfers result to channel : {}", channel);

        return new Output();
    }

}