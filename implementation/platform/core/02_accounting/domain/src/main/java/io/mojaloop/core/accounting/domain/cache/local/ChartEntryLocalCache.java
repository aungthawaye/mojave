/*-
 * ================================================================================
 * Mojave
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */

package io.mojaloop.core.accounting.domain.cache.local;

import io.mojaloop.core.accounting.contract.data.ChartEntryData;
import io.mojaloop.core.accounting.domain.cache.ChartEntryCache;
import io.mojaloop.core.accounting.domain.repository.ChartEntryRepository;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartId;
import io.mojaloop.core.common.datatype.type.accounting.ChartEntryCode;
import jakarta.annotation.PostConstruct;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
