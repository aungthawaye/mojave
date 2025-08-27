package io.mojaloop.connector.gateway.inbound.event.listener;

import io.mojaloop.connector.gateway.inbound.command.quotes.HandleQuotesResponseCommand;
import io.mojaloop.connector.gateway.inbound.event.PutQuotesEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class PutQuotesEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PutQuotesEventListener.class.getName());

    private final HandleQuotesResponseCommand handleQuotesResponse;

    public PutQuotesEventListener(HandleQuotesResponseCommand handleQuotesResponse) {

        assert null != handleQuotesResponse;

        this.handleQuotesResponse = handleQuotesResponse;
    }

    @Async
    @EventListener
    public void handle(PutQuotesEvent event) {

        LOGGER.info("Handling PutQuotesEvent : {}", event);

        var payload = event.getPayload();

        assert null != payload;

        try {

            this.handleQuotesResponse.execute(new HandleQuotesResponseCommand.Input(payload.source(), payload.quoteId(), payload.response()));

            LOGGER.info("Done handling PutQuotesEvent : {}", event);

        } catch (Exception e) {

            LOGGER.error("Error handling PutQuotesEvent", e);
        }
    }

}