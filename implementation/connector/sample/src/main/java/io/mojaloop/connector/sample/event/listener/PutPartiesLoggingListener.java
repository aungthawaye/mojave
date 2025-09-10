package io.mojaloop.connector.sample.event.listener;

import io.mojaloop.connector.gateway.inbound.event.GetPartiesEvent;
import io.mojaloop.connector.gateway.inbound.event.PutPartiesEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class PutPartiesLoggingListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PutPartiesLoggingListener.class);

    public PutPartiesLoggingListener() {

    }

    @Async
    @EventListener
    public void handle(PutPartiesEvent event) {

        LOGGER.info("Handling PutPartiesEvent in PutPartiesLoggingListener : {}", event);

    }
}
