package io.mojaloop.core.quoting.service.event;

import io.mojaloop.component.misc.spring.event.DomainEvent;
import io.mojaloop.core.quoting.contract.command.PutQuotesErrorCommand;
import lombok.Getter;

@Getter
public class PutQuotesErrorEvent extends DomainEvent<PutQuotesErrorCommand.Input> {

    public PutQuotesErrorEvent(PutQuotesErrorCommand.Input input) {
        super(input);
    }
}
