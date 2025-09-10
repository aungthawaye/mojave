package io.mojaloop.connector.gateway.inbound.event.listener;

import io.mojaloop.connector.gateway.inbound.command.transfers.HandleTransfersRequestCommand;
import io.mojaloop.connector.gateway.inbound.event.PostTransfersEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class PostTransfersEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostTransfersEventListener.class);

    private final HandleTransfersRequestCommand handleTransfersRequest;

    public PostTransfersEventListener(HandleTransfersRequestCommand handleTransfersRequest) {

        assert null != handleTransfersRequest;

        this.handleTransfersRequest = handleTransfersRequest;
    }

    @Async
    @EventListener
    public void handle(PostTransfersEvent event) {

        LOGGER.info("Handling PostTransfersEvent : {}", event);

        var payload = event.getPayload();

        assert null != payload;

        try {

            this.handleTransfersRequest.execute(new HandleTransfersRequestCommand.Input(payload.source(),
                                                                                        payload.transferId(),
                                                                                        payload.request()));

            LOGGER.info("Done handling PostTransfersEvent : {}", event);

        } catch (Exception e) {

            LOGGER.error("Error handling PostTransfersEvent", e);
        }
    }

}