package io.mojaloop.connector.gateway.inbound.event.listener;

import io.mojaloop.connector.gateway.inbound.command.parties.HandlePartiesRequestCommand;
import io.mojaloop.connector.gateway.inbound.event.GetPartiesEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class GetPartiesEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPartiesEventListener.class.getName());

    private final HandlePartiesRequestCommand handlePartiesRequest;

    public GetPartiesEventListener(HandlePartiesRequestCommand handlePartiesRequest) {

        assert null != handlePartiesRequest;

        this.handlePartiesRequest = handlePartiesRequest;
    }

    @Async
    @EventListener
    public void handle(GetPartiesEvent event) {

        LOGGER.info("Handling GetPartiesEvent : {}", event);

        var payload = event.getPayload();

        assert null != payload;

        try {

            this.handlePartiesRequest.execute(new HandlePartiesRequestCommand.Input(payload.source(),
                                                                                    payload.partyIdType(),
                                                                                    payload.partyId(),
                                                                                    payload.subId()));

            LOGGER.info("Done handling GetPartiesEvent : {}", event);

        } catch (Exception e) {

            LOGGER.error("Error handling GetPartiesEvent", e);
        }
    }

}
