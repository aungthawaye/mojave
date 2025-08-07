package io.mojaloop.connector.service.inbound.event;

import io.mojaloop.component.misc.spring.event.DomainEvent;
import io.mojaloop.connector.service.inbound.command.transfers.HandleTransfersRequestCommand;

public class PostTransfersEvent extends DomainEvent<HandleTransfersRequestCommand.Input> {

    public PostTransfersEvent(HandleTransfersRequestCommand.Input payload) {

        super(payload);
    }

}