package io.mojaloop.core.quoting.service.event;

import io.mojaloop.component.misc.spring.event.DomainEvent;
import io.mojaloop.core.quoting.contract.command.GetQuotesCommand;
import lombok.Getter;

@Getter
public class GetQuotesEvent extends DomainEvent<GetQuotesCommand.Input> {

    public GetQuotesEvent(GetQuotesCommand.Input input) {
        super(input);
    }
}
