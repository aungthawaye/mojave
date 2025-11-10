package io.mojaloop.platform.core.transfer.service.event.listener;

import io.mojaloop.core.transfer.contract.command.PatchTransfersCommand;
import io.mojaloop.platform.core.transfer.service.event.PatchTransfersEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PatchTransfersEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PatchTransfersEventListener.class);

    private final PatchTransfersCommand patchTransfers;

    public PatchTransfersEventListener(PatchTransfersCommand patchTransfers) {

        assert patchTransfers != null;
        this.patchTransfers = patchTransfers;
    }

    @Async
    @EventListener
    public void onPatchTransfersEvent(PatchTransfersEvent event) {

        LOGGER.info("Start handling PatchTransfersEvent : [{}]", event);

        var output = this.patchTransfers.execute(event.getPayload());

        LOGGER.info("Done handling PatchTransfersEvent : [{}], output : [{}]", event, output);
    }

}
