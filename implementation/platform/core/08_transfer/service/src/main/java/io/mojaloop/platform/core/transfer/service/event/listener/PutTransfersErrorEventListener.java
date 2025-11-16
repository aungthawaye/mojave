package io.mojaloop.platform.core.transfer.service.event.listener;

import io.mojaloop.core.transfer.contract.command.PutTransfersErrorCommand;
import io.mojaloop.platform.core.transfer.service.event.PutTransfersErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PutTransfersErrorEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PutTransfersErrorEventListener.class);

    private final PutTransfersErrorCommand putTransfersError;

    public PutTransfersErrorEventListener(PutTransfersErrorCommand putTransfersError) {

        assert putTransfersError != null;
        this.putTransfersError = putTransfersError;
    }

    @Async
    @EventListener
    public void onPutTransfersErrorEvent(PutTransfersErrorEvent event) {

        LOGGER.info("Start handling PutTransfersErrorEvent : [{}]", event);

        var output = this.putTransfersError.execute(event.getPayload());

        LOGGER.info("Done handling PutTransfersErrorEvent : [{}], output : [{}]", event, output);
    }

}
