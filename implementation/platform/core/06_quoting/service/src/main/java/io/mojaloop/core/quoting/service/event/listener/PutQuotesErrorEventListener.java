package io.mojaloop.core.quoting.service.event.listener;

import io.mojaloop.core.quoting.contract.command.PutQuotesErrorCommand;
import io.mojaloop.core.quoting.service.event.PutQuotesErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PutQuotesErrorEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PutQuotesErrorEventListener.class);

    private final PutQuotesErrorCommand putQuotesError;

    public PutQuotesErrorEventListener(PutQuotesErrorCommand putQuotesError) {

        assert putQuotesError != null;

        this.putQuotesError = putQuotesError;
    }

    @Async
    @EventListener
    public void onPutQuotesErrorEvent(PutQuotesErrorEvent event) {

        LOGGER.info("Start handling PutQuotesErrorEvent : [{}]", event);

        var output = this.putQuotesError.execute(event.getPayload());

        LOGGER.info("Done handling PutQuotesErrorEvent : [{}], output : [{}]", event, output);
    }
}
