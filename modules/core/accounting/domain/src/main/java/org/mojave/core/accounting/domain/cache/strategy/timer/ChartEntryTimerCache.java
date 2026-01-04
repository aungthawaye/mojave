/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
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
 * ===
 */
package org.mojave.core.accounting.domain.cache.strategy.timer;

import jakarta.annotation.PostConstruct;
import org.mojave.core.accounting.contract.data.ChartEntryData;
import org.mojave.core.accounting.domain.cache.ChartEntryCache;
import org.mojave.core.accounting.domain.model.ChartEntry;
import org.mojave.core.accounting.domain.repository.ChartEntryRepository;
import org.mojave.scheme.common.datatype.identifier.accounting.ChartEntryId;
import org.mojave.scheme.common.datatype.identifier.accounting.ChartId;
import org.mojave.scheme.common.datatype.type.accounting.ChartEntryCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChartEntryTimerCache implements ChartEntryCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChartEntryTimerCache.class);

    private final ChartEntryRepository chartEntryRepository;

    private final AtomicReference<Snapshot> snapshotRef = new AtomicReference<>(Snapshot.empty());

    private final Timer timer = new Timer("ChartEntryLocalCacheRefreshTimer", true);

    private final int interval;

    public ChartEntryTimerCache(final ChartEntryRepository chartEntryRepository,
                                final int interval) {

        assert chartEntryRepository != null;
        assert interval > 0;

        this.chartEntryRepository = chartEntryRepository;
        this.interval = interval;
    }

    @Override
    public void clear() {

        this.snapshotRef.set(Snapshot.empty());
    }

    @Override
    public void delete(final ChartEntryId chartEntryId) {

    }

    @Override
    public ChartEntryData get(final ChartEntryId chartEntryId) {

        if (chartEntryId == null) {
            return null;
        }

        return this.snapshotRef.get().withId.get(chartEntryId);
    }

    @Override
    public ChartEntryData get(final ChartEntryCode code) {

        if (code == null) {
            return null;
        }

        return this.snapshotRef.get().withCode.get(code);
    }

    @Override
    public Set<ChartEntryData> get(final ChartId chartId) {

        if (chartId == null) {
            return Set.of();
        }

        return this.snapshotRef.get().withChartId.getOrDefault(chartId, Set.of());
    }

    @PostConstruct
    public void postConstruct() {

        LOGGER.info("Bootstrapping ChartEntryTimerCache");

        this.refreshData();

        this.timer.scheduleAtFixedRate(
            new TimerTask() {

                @Override
                public void run() {

                    ChartEntryTimerCache.this.refreshData();
                }
            }, this.interval, this.interval);
    }

    @Override
    public void save(final ChartEntryData chartEntry) {

    }

    private void refreshData() {

        LOGGER.info("Start refreshing chart entry cache data");

        final var entities = this.chartEntryRepository.findAll();
        final var entries = entities.stream().map(ChartEntry::convert).toList();

        var _withId = entries
                          .stream()
                          .collect(Collectors.toUnmodifiableMap(
                              ChartEntryData::chartEntryId, Function.identity(), (a, b) -> a));
        var _withCode = entries
                            .stream()
                            .collect(Collectors.toUnmodifiableMap(
                                ChartEntryData::code, Function.identity(), (a, b) -> a));
        var _withChartId = Collections.unmodifiableMap(entries
                                                           .stream()
                                                           .collect(Collectors.groupingBy(
                                                               ChartEntryData::chartId,
                                                               Collectors.collectingAndThen(
                                                                   Collectors.toSet(),
                                                                   Collections::unmodifiableSet))));

        LOGGER.info("Refreshed ChartEntry cache data, count: {}", entries.size());

        this.snapshotRef.set(new Snapshot(_withId, _withCode, _withChartId));
    }

    private record Snapshot(Map<ChartEntryId, ChartEntryData> withId,
                            Map<ChartEntryCode, ChartEntryData> withCode,
                            Map<ChartId, Set<ChartEntryData>> withChartId) {

        static Snapshot empty() {

            return new Snapshot(Map.of(), Map.of(), Map.of());
        }

    }

}
