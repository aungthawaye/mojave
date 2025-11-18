package io.mojaloop.platform.core.transfer.service.event.listener;

import io.mojaloop.core.transfer.contract.command.GetTransfersCommand;
import io.mojaloop.platform.core.transfer.service.event.GetTransfersEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class GetTransfersEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetTransfersEventListener.class);

    private final GetTransfersCommand getTransfers;

    public GetTransfersEventListener(GetTransfersCommand getTransfers) {

        assert getTransfers != null;

        this.getTransfers = getTransfers;
    }

    @Async
    @EventListener
    public void onGetTransfersEvent(GetTransfersEvent event) {

        LOGGER.info("Start handling GetTransfersEvent : [{}]", event);

        var output = this.getTransfers.execute(event.getPayload());

        LOGGER.info("Done handling GetTransfersEvent : [{}], output : [{}]", event, output);
    }

}
