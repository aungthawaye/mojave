package io.mojaloop.core.quoting.service.event.listener;

import io.mojaloop.core.quoting.contract.command.PutQuotesCommand;
import io.mojaloop.core.quoting.service.event.PutQuotesEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PutQuotesEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PutQuotesEventListener.class);

    private final PutQuotesCommand putQuotes;

    public PutQuotesEventListener(PutQuotesCommand putQuotes) {

        assert putQuotes != null;

        this.putQuotes = putQuotes;
    }

    @Async
    @EventListener
    public void onPutQuotesEvent(PutQuotesEvent event) {

        LOGGER.info("Start handling PutQuotesEvent : [{}]", event);

        var output = this.putQuotes.execute(event.getPayload());

        LOGGER.info("Done handling PutQuotesEvent : [{}], output : [{}]", event, output);
    }
}
