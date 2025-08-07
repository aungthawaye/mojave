package io.mojaloop.core.lookup.service.event;

import io.mojaloop.component.misc.spring.event.DomainEvent;
import io.mojaloop.core.lookup.contract.command.PutPartiesErrorCommand;
import lombok.Getter;

@Getter
public class PutPartiesErrorEvent extends DomainEvent<PutPartiesErrorCommand.Input> {

    public PutPartiesErrorEvent(PutPartiesErrorCommand.Input input) {
        super(input);
    }

}