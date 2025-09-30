package io.mojaloop.core.quoting.service.event.listener;

import io.mojaloop.core.quoting.contract.command.PostQuotesCommand;
import io.mojaloop.core.quoting.service.event.PostQuotesEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PostQuotesEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostQuotesEventListener.class);

    private final PostQuotesCommand postQuotes;

    public PostQuotesEventListener(PostQuotesCommand postQuotes) {

        assert postQuotes != null;

        this.postQuotes = postQuotes;
    }

    @Async
    @EventListener
    public void onPostQuotesEvent(PostQuotesEvent event) {

        LOGGER.info("Start handling PostQuotesEvent : [{}]", event);

        var output = this.postQuotes.execute(event.getPayload());

        LOGGER.info("Done handling PostQuotesEvent : [{}], output : [{}]", event, output);
    }
}
