package io.mojaloop.platform.core.transfer.service.event.listener;

import io.mojaloop.core.transfer.contract.command.PutTransfersCommand;
import io.mojaloop.platform.core.transfer.service.event.PutTransfersEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PutTransfersEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PutTransfersEventListener.class);

    private final PutTransfersCommand putTransfers;

    public PutTransfersEventListener(PutTransfersCommand putTransfers) {

        assert putTransfers != null;
        this.putTransfers = putTransfers;
    }

    @Async
    @EventListener
    public void onPutTransfersEvent(PutTransfersEvent event) {

        LOGGER.info("Start handling PutTransfersEvent : [{}]", event);

        var output = this.putTransfers.execute(event.getPayload());

        LOGGER.info("Done handling PutTransfersEvent : [{}], output : [{}]", event, output);
    }

}
