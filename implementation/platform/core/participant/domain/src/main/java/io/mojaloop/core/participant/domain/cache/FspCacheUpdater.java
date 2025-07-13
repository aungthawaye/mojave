package io.mojaloop.core.participant.domain.cache;

import io.mojaloop.core.participant.domain.model.Fsp;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

public class FspCacheUpdater {

    @PostPersist
    @PostUpdate
    public void persistOrUpdate(Fsp fsp) {

    }

    @PostRemove
    public void postRemove(Fsp fsp) {

    }

}
