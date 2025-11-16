package io.mojaloop.platform.core.transfer.service.event;

import io.mojaloop.component.misc.spring.event.DomainEvent;
import io.mojaloop.core.transfer.contract.command.PatchTransfersCommand;
import lombok.Getter;

@Getter
public class PatchTransfersEvent extends DomainEvent<PatchTransfersCommand.Input> {

    public PatchTransfersEvent(PatchTransfersCommand.Input input) {

        super(input);
    }

}
