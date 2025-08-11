package io.mojaloop.connector.service.inbound.command.transfers;

import io.mojaloop.component.misc.pubsub.PubSubClient;
import io.mojaloop.connector.service.inbound.data.TransfersErrorResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
class HandleTransfersErrorCommandHandler implements HandleTransfersErrorCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandleTransfersErrorCommandHandler.class.getName());

    private final PubSubClient pubSubClient;

    public HandleTransfersErrorCommandHandler(PubSubClient pubSubClient) {

        assert null != pubSubClient;

        this.pubSubClient = pubSubClient;
    }

    @Override
    public Output execute(Input input) {

        var channel = "transfers-error:" + input.transferId();
        LOGGER.info("Publishing transfers error result to channel : {}", channel);

        this.pubSubClient.publish(channel, new TransfersErrorResult(input.transferId(), input.errorInformationObject()));
        LOGGER.info("Published transfers error result to channel : {}", channel);

        return new Output();
    }

}