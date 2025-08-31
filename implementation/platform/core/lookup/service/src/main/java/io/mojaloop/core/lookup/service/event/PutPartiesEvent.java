package io.mojaloop.core.lookup.service.event;

import io.mojaloop.component.misc.spring.event.DomainEvent;
import io.mojaloop.core.lookup.contract.command.PutPartiesCommand;
import lombok.Getter;

@Getter
public class PutPartiesEvent extends DomainEvent<PutPartiesCommand.Input> {

    public PutPartiesEvent(PutPartiesCommand.Input input) {

        super(input);
    }

}