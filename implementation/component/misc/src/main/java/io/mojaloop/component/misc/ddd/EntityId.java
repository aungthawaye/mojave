package io.mojaloop.component.misc.ddd;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Objects;

public abstract class EntityId<T> {

    @Getter
    @JsonValue
    private final T id;

    public EntityId(T id) {

        assert id != null;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof EntityId<?> that)) {
            return false;
        }

        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(id);
    }

}
