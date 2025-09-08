package io.mojaloop.core.account.domain.model;

import io.mojaloop.core.common.datatype.enumeration.account.OwnerType;
import io.mojaloop.core.common.datatype.identifier.account.OwnerId;
import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.common.datatype.identifier.participant.FxpId;
import io.mojaloop.core.common.datatype.identifier.participant.HubId;

public record Owner(OwnerType ownerType, OwnerId ownerId) {

    public Owner {

        assert ownerType != null;
        assert ownerId != null;
    }

    public static Owner ofFsp(FspId fspId) {

        return new Owner(OwnerType.FSP, new OwnerId(fspId.getId()));
    }

    public static Owner ofFxp(FxpId fxpId) {

        return new Owner(OwnerType.FXP, new OwnerId(fxpId.getId()));
    }

    public static Owner ofHub(HubId hubId) {

        return new Owner(OwnerType.HUB, new OwnerId(hubId.getId()));
    }
}
