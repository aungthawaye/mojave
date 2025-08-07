package io.mojaloop.connector.service.inbound.event.listener;

import io.mojaloop.connector.service.inbound.command.parties.HandlePartiesErrorCommand;
import io.mojaloop.connector.service.inbound.event.PutPartiesErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class PutPartiesErrorEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PutPartiesErrorEventListener.class.getName());

    private final HandlePartiesErrorCommand handlePartiesError;

    public PutPartiesErrorEventListener(HandlePartiesErrorCommand handlePartiesError) {

        assert null != handlePartiesError;

        this.handlePartiesError = handlePartiesError;
    }

    @Async
    @EventListener
    public void handle(PutPartiesErrorEvent event) {

        LOGGER.info("Handling PutPartiesErrorEvent : {}", event);

        var payload = event.getPayload();

        assert null != payload;

        try {

            this.handlePartiesError.execute(new HandlePartiesErrorCommand.Input(payload.source(),
                                                                                payload.partyIdType(),
                                                                                payload.partyId(),
                                                                                payload.subId(),
                                                                                payload.errorInformationObject()));

            LOGGER.info("Done handling PutPartiesErrorEvent : {}", event);

        } catch (Exception e) {

            LOGGER.error("Error handling PutPartiesErrorEvent", e);
        }
    }

}