package io.mojaloop.core.lookup.service.event.listener;

import io.mojaloop.core.lookup.contract.command.PutPartiesErrorCommand;
import io.mojaloop.core.lookup.service.event.PutPartiesErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PutPartiesErrorEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PutPartiesErrorEventListener.class);

    private final PutPartiesErrorCommand putPartiesError;

    public PutPartiesErrorEventListener(PutPartiesErrorCommand putPartiesError) {

        assert putPartiesError != null;

        this.putPartiesError = putPartiesError;
    }

    @Async
    @EventListener
    public void onPutPartiesErrorEvent(PutPartiesErrorEvent event) {

        LOGGER.info("Start handling PutPartiesErrorEvent : [{}]", event);

        var output = this.putPartiesError.execute(event.getPayload());

        LOGGER.info("Done handling PutPartiesErrorEvent : [{}], output : [{}]", event, output);
    }

}