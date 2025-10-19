package io.mojaloop.core.accounting.domain.cache.redis.updater;

import io.mojaloop.component.misc.spring.SpringContext;
import io.mojaloop.core.accounting.domain.cache.FlowDefinitionCache;
import io.mojaloop.core.accounting.domain.model.FlowDefinition;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

public class FlowDefinitionCacheUpdater {

    @PostPersist
    @PostUpdate
    public void persistOrUpdate(final FlowDefinition flowDefinition) {

        final var cache = SpringContext.getBean(FlowDefinitionCache.class);
        cache.save(flowDefinition.convert());
    }

    @PostRemove
    public void postRemove(final FlowDefinition flowDefinition) {

        final var cache = SpringContext.getBean(FlowDefinitionCache.class);
        cache.delete(flowDefinition.getId());
    }

}
