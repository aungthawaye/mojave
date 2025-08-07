package io.mojaloop.connector.service.inbound.event;

import io.mojaloop.component.misc.spring.event.DomainEvent;
import io.mojaloop.connector.service.inbound.command.parties.HandlePartiesResponseCommand;

public class PutPartiesEvent extends DomainEvent<HandlePartiesResponseCommand.Input> {

    public PutPartiesEvent(HandlePartiesResponseCommand.Input payload) {

        super(payload);
    }

}
