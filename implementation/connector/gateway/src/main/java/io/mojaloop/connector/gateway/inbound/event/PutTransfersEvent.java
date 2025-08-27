package io.mojaloop.connector.gateway.inbound.event;

import io.mojaloop.component.misc.spring.event.DomainEvent;
import io.mojaloop.connector.gateway.inbound.command.transfers.HandleTransfersResponseCommand;

public class PutTransfersEvent extends DomainEvent<HandleTransfersResponseCommand.Input> {

    public PutTransfersEvent(HandleTransfersResponseCommand.Input payload) {

        super(payload);
    }

}