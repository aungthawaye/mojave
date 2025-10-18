package io.mojaloop.core.accounting.domain.cache.local;

import io.mojaloop.core.accounting.contract.data.ChartEntryData;
import io.mojaloop.core.accounting.domain.cache.ChartEntryCache;
import io.mojaloop.core.accounting.domain.repository.ChartEntryRepository;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartId;
import io.mojaloop.core.common.datatype.type.accounting.ChartEntryCode;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Qualifier(ChartEntryCache.Qualifiers.IN_MEMORY)
public class ChartEntryLocalCache implements ChartEntryCache {

    private final ChartEntryRepository chartEntryRepository;

    private final Map<Long, ChartEntryData> withId;

    private final Map<String, ChartEntryData> withCode;

    public ChartEntryLocalCache(final ChartEntryRepository chartEntryRepository) {

        assert chartEntryRepository != null;

        this.chartEntryRepository = chartEntryRepository;

        this.withId = new ConcurrentHashMap<>();
        this.withCode = new ConcurrentHashMap<>();
    }

    @Override
    public void clear() {

        this.withId.clear();
        this.withCode.clear();
    }

    @Override
    public void delete(final ChartEntryId chartEntryId) {

        final var removed = this.withId.remove(chartEntryId.getId());

        if (removed != null) {
            this.withCode.remove(removed.code().value());
        }
    }

    @Override
    public ChartEntryData get(final ChartEntryId chartEntryId) {

        return this.withId.get(chartEntryId.getId());
    }

    @Override
    public ChartEntryData get(final ChartEntryCode code) {

        return this.withCode.get(code.value());
    }

    @Override
    public Set<ChartEntryData> get(final ChartId chartId) {

        final var result = new HashSet<ChartEntryData>();

        for (final var entry : this.withId.values()) {
            if (entry.chartId().equals(chartId)) {
                result.add(entry);
            }
        }

        return result;
    }

    @PostConstruct
    public void postConstruct() {

        this.clear();

        final var entries = this.chartEntryRepository.findAll();

        for (final var entry : entries) {
            this.save(entry.convert());
        }
    }

    @Override
    public void save(final ChartEntryData chartEntry) {

        this.withId.put(chartEntry.chartEntryId().getId(), chartEntry);
        this.withCode.put(chartEntry.code().value(), chartEntry);
    }
}
