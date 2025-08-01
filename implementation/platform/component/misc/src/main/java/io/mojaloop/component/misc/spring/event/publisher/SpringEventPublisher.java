package io.mojaloop.component.misc.spring.event.publisher;

import io.mojaloop.component.misc.spring.event.DomainEvent;
import io.mojaloop.component.misc.spring.event.EventPublisher;
import org.springframework.context.ApplicationEventPublisher;

public class SpringEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public SpringEventPublisher(ApplicationEventPublisher applicationEventPublisher) {

        assert applicationEventPublisher != null;

        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public <E extends DomainEvent<?>> void publish(E event) {

        assert event != null;

        this.applicationEventPublisher.publishEvent(event);
    }

}
