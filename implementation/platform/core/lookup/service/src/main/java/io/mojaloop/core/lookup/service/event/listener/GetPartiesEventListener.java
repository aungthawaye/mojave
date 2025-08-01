package io.mojaloop.core.lookup.service.event.listener;

import io.mojaloop.core.lookup.contract.command.GetParties;
import io.mojaloop.core.lookup.service.event.GetPartiesEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class GetPartiesEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPartiesEventListener.class);

    private final GetParties getParties;

    public GetPartiesEventListener(GetParties getParties) {

        assert getParties != null;

        this.getParties = getParties;
    }

    @EventListener
    @Async
    public void onGetPartiesEvent(GetPartiesEvent event) {

        LOGGER.info("Start handling GetPartiesEvent : [{}]", event);

        var output = this.getParties.execute(event.getPayload());

        LOGGER.info("Done handling GetPartiesEvent : [{}], output : [{}]", event, output);
    }

}
