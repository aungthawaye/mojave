package io.mojaloop.component.misc.spring.event;

import io.mojaloop.component.misc.handy.Snowflake;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class DomainEvent<P> implements Serializable {

    private final long id;

    private final String name;

    private final P payload;

    public DomainEvent(P payload) {

        this.id = Snowflake.get().nextId();
        this.name = this.getClass().getName();

        assert payload != null;

        this.payload = payload;
    }

}
