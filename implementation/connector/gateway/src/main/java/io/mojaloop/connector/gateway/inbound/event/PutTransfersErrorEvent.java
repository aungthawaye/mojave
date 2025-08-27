package io.mojaloop.connector.gateway.inbound.event;

import io.mojaloop.component.misc.spring.event.DomainEvent;
import io.mojaloop.connector.gateway.inbound.command.transfers.HandleTransfersErrorCommand;

public class PutTransfersErrorEvent extends DomainEvent<HandleTransfersErrorCommand.Input> {

    public PutTransfersErrorEvent(HandleTransfersErrorCommand.Input payload) {

        super(payload);
    }

}