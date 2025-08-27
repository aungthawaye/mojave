package io.mojaloop.connector.gateway.inbound.event;

import io.mojaloop.component.misc.spring.event.DomainEvent;
import io.mojaloop.connector.gateway.inbound.command.quotes.HandleQuotesResponseCommand;

public class PutQuotesEvent extends DomainEvent<HandleQuotesResponseCommand.Input> {

    public PutQuotesEvent(HandleQuotesResponseCommand.Input payload) {

        super(payload);
    }

}