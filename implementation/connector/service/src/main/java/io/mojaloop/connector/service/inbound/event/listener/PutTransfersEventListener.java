package io.mojaloop.connector.service.inbound.event.listener;

import io.mojaloop.connector.service.inbound.command.transfers.HandleTransfersResponseCommand;
import io.mojaloop.connector.service.inbound.event.PutTransfersEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class PutTransfersEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PutTransfersEventListener.class.getName());

    private final HandleTransfersResponseCommand handleTransfersResponse;

    public PutTransfersEventListener(HandleTransfersResponseCommand handleTransfersResponse) {

        assert null != handleTransfersResponse;

        this.handleTransfersResponse = handleTransfersResponse;
    }

    @Async
    @EventListener
    public void handle(PutTransfersEvent event) {

        LOGGER.info("Handling PutTransfersEvent : {}", event);

        var payload = event.getPayload();

        assert null != payload;

        try {

            this.handleTransfersResponse.execute(new HandleTransfersResponseCommand.Input(payload.source(),
                                                                                          payload.transferId(),
                                                                                          payload.response()));

            LOGGER.info("Done handling PutTransfersEvent : {}", event);

        } catch (Exception e) {

            LOGGER.error("Error handling PutTransfersEvent", e);
        }
    }

}