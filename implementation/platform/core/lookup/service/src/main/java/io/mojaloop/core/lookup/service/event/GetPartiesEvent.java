package io.mojaloop.core.lookup.service.event;

import io.mojaloop.component.misc.spring.event.DomainEvent;
import io.mojaloop.core.lookup.contract.command.GetParties;
import lombok.Getter;

@Getter
public class GetPartiesEvent extends DomainEvent<GetParties.Input> {

    public GetPartiesEvent(GetParties.Input input) {

        super(input);

    }

}
