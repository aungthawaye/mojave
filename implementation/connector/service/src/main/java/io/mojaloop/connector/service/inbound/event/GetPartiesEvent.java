package io.mojaloop.connector.service.inbound.event;

import io.mojaloop.component.misc.spring.event.DomainEvent;
import io.mojaloop.connector.service.inbound.command.parties.HandlePartiesRequest;

public class GetPartiesEvent extends DomainEvent<HandlePartiesRequest.Input> {

    public GetPartiesEvent(HandlePartiesRequest.Input payload) {

        super(payload);
    }

}
