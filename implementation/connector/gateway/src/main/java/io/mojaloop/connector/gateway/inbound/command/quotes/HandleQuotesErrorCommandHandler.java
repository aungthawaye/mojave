package io.mojaloop.connector.gateway.inbound.command.quotes;

import io.mojaloop.component.misc.pubsub.PubSubClient;
import io.mojaloop.connector.gateway.inbound.data.QuotesErrorResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
class HandleQuotesErrorCommandHandler implements HandleQuotesErrorCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandleQuotesErrorCommandHandler.class.getName());

    private final PubSubClient pubSubClient;

    public HandleQuotesErrorCommandHandler(PubSubClient pubSubClient) {

        assert null != pubSubClient;

        this.pubSubClient = pubSubClient;
    }

    @Override
    public Output execute(Input input) {

        var channel = "quotes:error" + input.quoteId();
        LOGGER.info("Publishing quotes error result to channel : {}", channel);

        this.pubSubClient.publish(channel, new QuotesErrorResult(input.quoteId(), input.errorInformationObject()));
        LOGGER.info("Published quotes error result to channel : {}", channel);

        return new Output();
    }

}