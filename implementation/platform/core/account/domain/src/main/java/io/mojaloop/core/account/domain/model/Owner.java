package io.mojaloop.core.account.domain.model;

import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.core.common.datatype.converter.identifier.account.OwnerIdConverter;
import io.mojaloop.core.common.datatype.enumeration.account.OwnerType;
import io.mojaloop.core.common.datatype.identifier.account.OwnerId;
import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.common.datatype.identifier.participant.FxpId;
import io.mojaloop.core.common.datatype.identifier.participant.HubId;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;

@Embeddable
public record Owner(@Column(name = "owner_type", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH) OwnerType ownerType,
                    @Column(name = "owner_id", nullable = false) @Convert(converter = OwnerIdConverter.class) OwnerId ownerId) {

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
