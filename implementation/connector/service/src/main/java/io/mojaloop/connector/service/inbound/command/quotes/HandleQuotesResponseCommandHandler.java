package io.mojaloop.connector.service.inbound.command.quotes;

import io.mojaloop.component.misc.pubsub.PubSubClient;
import io.mojaloop.connector.service.inbound.data.QuotesResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HandleQuotesResponseCommandHandler implements HandleQuotesResponseCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandleQuotesResponseCommandHandler.class.getName());

    private final PubSubClient pubSubClient;

    public HandleQuotesResponseCommandHandler(PubSubClient pubSubClient) {

        assert null != pubSubClient;

        this.pubSubClient = pubSubClient;
    }

    @Override
    public Output execute(Input input) {

        var channel = "quotes:" + input.quoteId();
        LOGGER.info("Publishing quotes result to channel : {}", channel);

        this.pubSubClient.publish(channel, new QuotesResult(input.quoteId(), input.response()));
        LOGGER.info("Published quotes result to channel : {}", channel);

        return new Output();
    }

}