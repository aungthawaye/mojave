package io.mojaloop.connector.service.inbound.event;

import io.mojaloop.component.misc.spring.event.DomainEvent;
import io.mojaloop.connector.service.inbound.command.quotes.HandleQuotesRequestCommand;

public class PostQuotesEvent extends DomainEvent<HandleQuotesRequestCommand.Input> {

    public PostQuotesEvent(HandleQuotesRequestCommand.Input payload) {

        super(payload);
    }

}