package io.mojaloop.core.common.datatype.identifier.transaction;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.mojaloop.component.misc.ddd.EntityId;

public class TransactionId extends EntityId<Long> {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public TransactionId(Long id) {

        super(id);
    }
}
