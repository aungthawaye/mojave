package io.mojaloop.platform.core.transfer.service.event;

import io.mojaloop.component.misc.spring.event.DomainEvent;
import io.mojaloop.core.transfer.contract.command.PutTransfersErrorCommand;
import lombok.Getter;

@Getter
public class PutTransfersErrorEvent extends DomainEvent<PutTransfersErrorCommand.Input> {

    public PutTransfersErrorEvent(PutTransfersErrorCommand.Input input) {

        super(input);
    }

}
