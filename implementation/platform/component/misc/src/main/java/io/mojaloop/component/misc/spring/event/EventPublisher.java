package io.mojaloop.component.misc.spring.event;

public interface EventPublisher {

    <E extends DomainEvent<?>> void publish(E event);

}
