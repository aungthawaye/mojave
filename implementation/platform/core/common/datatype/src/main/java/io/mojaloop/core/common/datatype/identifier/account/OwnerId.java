package io.mojaloop.core.common.datatype.identifier.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.mojaloop.component.misc.ddd.EntityId;

public class OwnerId extends EntityId<Long> {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public OwnerId(Long id) {

        super(id);
    }
}
