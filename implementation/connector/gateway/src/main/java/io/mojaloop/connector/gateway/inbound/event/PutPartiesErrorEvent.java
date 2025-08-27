package io.mojaloop.connector.gateway.inbound.event;

import io.mojaloop.component.misc.spring.event.DomainEvent;
import io.mojaloop.connector.gateway.inbound.command.parties.HandlePartiesErrorCommand;

public class PutPartiesErrorEvent extends DomainEvent<HandlePartiesErrorCommand.Input> {

    public PutPartiesErrorEvent(HandlePartiesErrorCommand.Input payload) {

        super(payload);
    }

}