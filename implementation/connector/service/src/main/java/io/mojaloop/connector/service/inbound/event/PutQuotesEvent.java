package io.mojaloop.connector.service.inbound.event;

import io.mojaloop.component.misc.spring.event.DomainEvent;
import io.mojaloop.connector.service.inbound.command.quotes.HandleQuotesResponseCommand;

public class PutQuotesEvent extends DomainEvent<HandleQuotesResponseCommand.Input> {

    public PutQuotesEvent(HandleQuotesResponseCommand.Input payload) {

        super(payload);
    }

}