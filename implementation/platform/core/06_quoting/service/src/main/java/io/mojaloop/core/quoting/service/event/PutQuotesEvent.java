package io.mojaloop.core.quoting.service.event;

import io.mojaloop.component.misc.spring.event.DomainEvent;
import io.mojaloop.core.quoting.contract.command.PutQuotesCommand;
import lombok.Getter;

@Getter
public class PutQuotesEvent extends DomainEvent<PutQuotesCommand.Input> {

    public PutQuotesEvent(PutQuotesCommand.Input input) {
        super(input);
    }
}
