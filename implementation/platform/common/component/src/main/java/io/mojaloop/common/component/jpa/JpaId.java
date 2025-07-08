package io.mojaloop.common.component.jpa;

import java.io.Serial;
import java.io.Serializable;

public abstract class JpaId<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj instanceof JpaId<?> other) {

            return this.getEntityId()
                       .equals(other.getEntityId());
        }

        return false;
    }

    public abstract T getEntityId();

    @Override
    public int hashCode() {

        return this.getEntityId()
                   .hashCode();
    }

    @Override
    public String toString() {

        return this.getEntityId()
                   .toString();
    }

}
