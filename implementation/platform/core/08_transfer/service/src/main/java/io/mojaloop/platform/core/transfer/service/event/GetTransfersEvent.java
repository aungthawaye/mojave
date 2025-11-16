package io.mojaloop.platform.core.transfer.service.event;

import io.mojaloop.component.misc.spring.event.DomainEvent;
import io.mojaloop.core.transfer.contract.command.GetTransfersCommand;
import lombok.Getter;

@Getter
public class GetTransfersEvent extends DomainEvent<GetTransfersCommand.Input> {

    public GetTransfersEvent(GetTransfersCommand.Input input) {

        super(input);
    }

}
