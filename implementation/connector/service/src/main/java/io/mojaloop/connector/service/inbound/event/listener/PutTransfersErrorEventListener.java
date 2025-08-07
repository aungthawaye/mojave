package io.mojaloop.connector.service.inbound.event.listener;

import io.mojaloop.connector.service.inbound.command.transfers.HandleTransfersErrorCommand;
import io.mojaloop.connector.service.inbound.event.PutTransfersErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class PutTransfersErrorEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PutTransfersErrorEventListener.class.getName());

    private final HandleTransfersErrorCommand handleTransfersError;

    public PutTransfersErrorEventListener(HandleTransfersErrorCommand handleTransfersError) {

        assert null != handleTransfersError;

        this.handleTransfersError = handleTransfersError;
    }

    @Async
    @EventListener
    public void handle(PutTransfersErrorEvent event) {

        LOGGER.info("Handling PutTransfersErrorEvent : {}", event);

        var payload = event.getPayload();

        assert null != payload;

        try {

            this.handleTransfersError.execute(new HandleTransfersErrorCommand.Input(payload.source(),
                                                                                    payload.transferId(),
                                                                                    payload.errorInformationObject()));

            LOGGER.info("Done handling PutTransfersErrorEvent : {}", event);

        } catch (Exception e) {

            LOGGER.error("Error handling PutTransfersErrorEvent", e);
        }
    }

}