package io.mojaloop.connector.sample.event.listener;

import io.mojaloop.connector.gateway.inbound.event.GetPartiesEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class GetPartiesLoggingListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPartiesLoggingListener.class);

    public GetPartiesLoggingListener() {

    }

    @Async
    @EventListener
    public void handle(GetPartiesEvent event) {

        LOGGER.info("Handling GetPartiesEvent in GetPartiesLoggingListener : {}", event);

    }

}
