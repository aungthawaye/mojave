package io.mojaloop.platform.core.transfer.service.event;

import io.mojaloop.component.misc.spring.event.DomainEvent;
import io.mojaloop.core.transfer.contract.command.PutTransfersCommand;
import lombok.Getter;

@Getter
public class PutTransfersEvent extends DomainEvent<PutTransfersCommand.Input> {

    public PutTransfersEvent(PutTransfersCommand.Input input) {

        super(input);
    }

}
