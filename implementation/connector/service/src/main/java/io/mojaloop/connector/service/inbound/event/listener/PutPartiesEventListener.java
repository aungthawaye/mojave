package io.mojaloop.connector.service.inbound.event.listener;

import io.mojaloop.connector.service.inbound.command.parties.HandlePartiesResponseCommand;
import io.mojaloop.connector.service.inbound.event.PutPartiesEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class PutPartiesEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PutPartiesEventListener.class.getName());

    private final HandlePartiesResponseCommand handlePartiesResponse;

    public PutPartiesEventListener(HandlePartiesResponseCommand handlePartiesResponse) {

        assert null != handlePartiesResponse;

        this.handlePartiesResponse = handlePartiesResponse;
    }

    @Async
    @EventListener
    public void handle(PutPartiesEvent event) {

        LOGGER.info("Handling PutPartiesEvent : {}", event);

        var payload = event.getPayload();

        assert null != payload;

        try {

            this.handlePartiesResponse.execute(new HandlePartiesResponseCommand.Input(payload.source(),
                                                                                      payload.partyIdType(),
                                                                                      payload.partyId(),
                                                                                      payload.subId(),
                                                                                      payload.response()));

            LOGGER.info("Done handling PutPartiesEvent : {}", event);

        } catch (Exception e) {

            LOGGER.error("Error handling PutPartiesEvent", e);
        }
    }

}
