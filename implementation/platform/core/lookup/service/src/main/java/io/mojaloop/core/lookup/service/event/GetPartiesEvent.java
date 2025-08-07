package io.mojaloop.core.lookup.service.event;

import io.mojaloop.component.misc.spring.event.DomainEvent;
import io.mojaloop.core.lookup.contract.command.GetPartiesCommand;
import lombok.Getter;

@Getter
public class GetPartiesEvent extends DomainEvent<GetPartiesCommand.Input> {

    public GetPartiesEvent(GetPartiesCommand.Input input) {

        super(input);

    }

}
