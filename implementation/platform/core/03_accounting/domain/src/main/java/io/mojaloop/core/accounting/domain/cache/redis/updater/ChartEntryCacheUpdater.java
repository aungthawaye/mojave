package io.mojaloop.core.accounting.domain.cache.redis.updater;

import io.mojaloop.component.misc.spring.SpringContext;
import io.mojaloop.core.accounting.domain.cache.ChartEntryCache;
import io.mojaloop.core.accounting.domain.model.ChartEntry;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

public class ChartEntryCacheUpdater {

    @PostPersist
    @PostUpdate
    public void persistOrUpdate(ChartEntry entry) {

        var cache = SpringContext.getBean(ChartEntryCache.class, ChartEntryCache.Qualifiers.DEFAULT);

        cache.save(entry.convert());
    }

    @PostRemove
    public void postRemove(ChartEntry entry) {

        var cache = SpringContext.getBean(ChartEntryCache.class, ChartEntryCache.Qualifiers.DEFAULT);

        cache.delete(entry.getId());
    }
}
