package io.mojaloop.connector.service.inbound.event;

import io.mojaloop.component.misc.spring.event.DomainEvent;
import io.mojaloop.connector.service.inbound.command.parties.HandlePartiesRequestCommand;

public class GetPartiesEvent extends DomainEvent<HandlePartiesRequestCommand.Input> {

    public GetPartiesEvent(HandlePartiesRequestCommand.Input payload) {

        super(payload);
    }

}
