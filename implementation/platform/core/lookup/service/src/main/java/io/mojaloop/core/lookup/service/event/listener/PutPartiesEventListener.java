package io.mojaloop.core.lookup.service.event.listener;

import io.mojaloop.core.lookup.contract.command.PutPartiesCommand;
import io.mojaloop.core.lookup.service.event.PutPartiesEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PutPartiesEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PutPartiesEventListener.class);

    private final PutPartiesCommand putParties;

    public PutPartiesEventListener(PutPartiesCommand putParties) {

        assert putParties != null;

        this.putParties = putParties;
    }

    @Async
    @EventListener
    public void onPutPartiesEvent(PutPartiesEvent event) {

        LOGGER.info("Start handling PutPartiesEvent : [{}]", event);

        var output = this.putParties.execute(event.getPayload());

        LOGGER.info("Done handling PutPartiesEvent : [{}], output : [{}]", event, output);
    }

}