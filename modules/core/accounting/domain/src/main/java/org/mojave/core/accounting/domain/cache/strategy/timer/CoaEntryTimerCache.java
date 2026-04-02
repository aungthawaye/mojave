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
import org.mojave.common.datatype.identifier.accounting.CoaEntryId;
import org.mojave.common.datatype.identifier.accounting.CoaId;
import org.mojave.common.datatype.type.accounting.CoaEntryCode;
import org.mojave.core.accounting.contract.data.CoaEntryData;
import org.mojave.core.accounting.domain.cache.CoaEntryCache;
import org.mojave.core.accounting.domain.model.CoaEntry;
import org.mojave.core.accounting.domain.repository.CoaEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CoaEntryTimerCache implements CoaEntryCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoaEntryTimerCache.class);

    private final CoaEntryRepository coaEntryRepository;

    private final AtomicReference<Snapshot> snapshotRef = new AtomicReference<>(Snapshot.empty());

    private final Timer timer = new Timer("CoaEntryLocalCacheRefreshTimer", true);

    private final int interval;

    public CoaEntryTimerCache(final CoaEntryRepository coaEntryRepository,
                              final int interval) {

        Objects.requireNonNull(coaEntryRepository);
        if (interval <= 0) {
            throw new IllegalArgumentException("interval must be greater than 0");
        }

        this.coaEntryRepository = coaEntryRepository;
        this.interval = interval;
    }

    @Override
    public void clear() {

        this.snapshotRef.set(Snapshot.empty());
    }

    @Override
    public void delete(final CoaEntryId coaEntryId) {

    }

    @Override
    public CoaEntryData get(final CoaEntryId coaEntryId) {

        if (coaEntryId == null) {
            return null;
        }

        return this.snapshotRef.get().withId.get(coaEntryId);
    }

    @Override
    public CoaEntryData get(final CoaEntryCode code) {

        if (code == null) {
            return null;
        }

        return this.snapshotRef.get().withCode.get(code);
    }

    @Override
    public Set<CoaEntryData> get(final CoaId coaId) {

        if (coaId == null) {
            return Set.of();
        }

        return this.snapshotRef.get().withCoaId.getOrDefault(coaId, Set.of());
    }

    @PostConstruct
    public void postConstruct() {

        LOGGER.info("Bootstrapping CoaEntryTimerCache");

        this.refreshData();

        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                CoaEntryTimerCache.this.refreshData();
            }
        }, this.interval, this.interval);
    }

    @Override
    public void save(final CoaEntryData coaEntry) {

    }

    private void refreshData() {

        LOGGER.info("Start refreshing CoA entry cache data");

        final var entities = this.coaEntryRepository.findAll();
        final var entries = entities.stream().map(CoaEntry::convert).toList();

        var _withId = entries
                          .stream()
                          .collect(Collectors.toUnmodifiableMap(
                              CoaEntryData::coaEntryId,
                              Function.identity(), (a, b) -> a));
        var _withCode = entries
                            .stream()
                            .collect(Collectors.toUnmodifiableMap(
                                CoaEntryData::code,
                                Function.identity(), (a, b) -> a));
        var _withCoaId = Collections.unmodifiableMap(
            entries.stream().collect(Collectors.groupingBy(
                CoaEntryData::coaId,
                Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet))));

        LOGGER.info("Refreshed CoaEntry cache data, count: {}", entries.size());

        this.snapshotRef.set(new Snapshot(_withId, _withCode, _withCoaId));
    }

    private record Snapshot(Map<CoaEntryId, CoaEntryData> withId,
                            Map<CoaEntryCode, CoaEntryData> withCode,
                            Map<CoaId, Set<CoaEntryData>> withCoaId) {

        static Snapshot empty() {

            return new Snapshot(Map.of(), Map.of(), Map.of());
        }

    }

}
