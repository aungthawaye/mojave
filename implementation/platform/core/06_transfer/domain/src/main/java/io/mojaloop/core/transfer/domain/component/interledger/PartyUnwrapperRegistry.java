package io.mojaloop.core.transfer.domain.component.interledger;

import io.mojaloop.core.common.datatype.type.participant.FspCode;

import java.util.HashMap;
import java.util.Map;

public class PartyUnwrapperRegistry {

    private final Map<FspCode, PartyUnwrapper<?>> unwrappers = new HashMap<>();

    public PartyUnwrapper<?> get(FspCode fspCode) {

        return this.unwrappers.get(fspCode);
    }

    public <T> void register(FspCode fspCode, PartyUnwrapper<T> unwrapper) {

        this.unwrappers.put(fspCode, unwrapper);
    }

}
