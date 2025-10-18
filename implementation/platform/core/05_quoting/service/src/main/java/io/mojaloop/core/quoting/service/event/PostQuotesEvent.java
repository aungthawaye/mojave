package io.mojaloop.core.quoting.service.event;

import io.mojaloop.component.misc.spring.event.DomainEvent;
import io.mojaloop.core.quoting.contract.command.PostQuotesCommand;
import lombok.Getter;

@Getter
public class PostQuotesEvent extends DomainEvent<PostQuotesCommand.Input> {

    public PostQuotesEvent(PostQuotesCommand.Input input) {
        super(input);
    }
}
