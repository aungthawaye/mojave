package io.mojaloop.core.quoting.service.event.listener;

import io.mojaloop.core.quoting.contract.command.GetQuotesCommand;
import io.mojaloop.core.quoting.service.event.GetQuotesEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class GetQuotesEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetQuotesEventListener.class);

    private final GetQuotesCommand getQuotes;

    public GetQuotesEventListener(GetQuotesCommand getQuotes) {

        assert getQuotes != null;

        this.getQuotes = getQuotes;
    }

    @Async
    @EventListener
    public void onGetQuotesEvent(GetQuotesEvent event) {
        LOGGER.info("Start handling GetQuotesEvent : [{}]", event);

        var output = this.getQuotes.execute(event.getPayload());

        LOGGER.info("Done handling GetQuotesEvent : [{}], output : [{}]", event, output);
    }
}
