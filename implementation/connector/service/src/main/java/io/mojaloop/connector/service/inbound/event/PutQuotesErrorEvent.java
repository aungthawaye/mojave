package io.mojaloop.connector.service.inbound.event;

import io.mojaloop.component.misc.spring.event.DomainEvent;
import io.mojaloop.connector.service.inbound.command.quotes.HandleQuotesErrorCommand;

public class PutQuotesErrorEvent extends DomainEvent<HandleQuotesErrorCommand.Input> {

    public PutQuotesErrorEvent(HandleQuotesErrorCommand.Input payload) {

        super(payload);
    }

}