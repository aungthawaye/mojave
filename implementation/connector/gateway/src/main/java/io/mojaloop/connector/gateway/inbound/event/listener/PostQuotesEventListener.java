package io.mojaloop.connector.gateway.inbound.event.listener;

import io.mojaloop.connector.gateway.inbound.command.quotes.HandleQuotesRequestCommand;
import io.mojaloop.connector.gateway.inbound.event.PostQuotesEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class PostQuotesEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostQuotesEventListener.class);

    private final HandleQuotesRequestCommand handleQuotesRequest;

    public PostQuotesEventListener(HandleQuotesRequestCommand handleQuotesRequest) {

        assert null != handleQuotesRequest;

        this.handleQuotesRequest = handleQuotesRequest;
    }

    @Async
    @EventListener
    public void handle(PostQuotesEvent event) {

        LOGGER.info("Handling PostQuotesEvent : {}", event);

        var payload = event.getPayload();

        assert null != payload;

        try {

            this.handleQuotesRequest.execute(new HandleQuotesRequestCommand.Input(payload.source(), payload.quoteId(), payload.request()));

            LOGGER.info("Done handling PostQuotesEvent : {}", event);

        } catch (Exception e) {

            LOGGER.error("Error handling PostQuotesEvent", e);
        }
    }

}