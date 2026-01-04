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
import org.mojave.core.accounting.contract.data.FlowDefinitionData;
import org.mojave.core.accounting.domain.cache.FlowDefinitionCache;
import org.mojave.core.accounting.domain.model.FlowDefinition;
import org.mojave.core.accounting.domain.repository.FlowDefinitionRepository;
import org.mojave.scheme.common.datatype.enums.trasaction.TransactionType;
import org.mojave.scheme.common.datatype.identifier.accounting.FlowDefinitionId;
import org.mojave.scheme.fspiop.core.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FlowDefinitionTimerCache implements FlowDefinitionCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlowDefinitionTimerCache.class);

    private final FlowDefinitionRepository flowDefinitionRepository;

    private final AtomicReference<Snapshot> snapshotRef = new AtomicReference<>(Snapshot.empty());

    private final Timer timer = new Timer("FlowDefinitionLocalCacheRefreshTimer", true);

    private final int interval;

    public FlowDefinitionTimerCache(final FlowDefinitionRepository flowDefinitionRepository,
                                    final int interval) {

        assert flowDefinitionRepository != null;
        assert interval > 0;

        this.flowDefinitionRepository = flowDefinitionRepository;
        this.interval = interval;
    }

    @Override
    public void clear() {

        this.snapshotRef.set(Snapshot.empty());
    }

    @Override
    public void delete(final FlowDefinitionId flowDefinitionId) {

    }

    @Override
    public FlowDefinitionData get(final FlowDefinitionId flowDefinitionId) {

        if (flowDefinitionId == null) {
            return null;
        }

        return this.snapshotRef.get().withId.get(flowDefinitionId);
    }

    @Override
    public FlowDefinitionData get(final TransactionType transactionType, final Currency currency) {

        if (transactionType == null || currency == null) {
            return null;
        }

        final var key = FlowDefinitionCache.Keys.forTransaction(transactionType, currency);
        return this.snapshotRef.get().withTxnTypeCurrency.get(key);
    }

    @PostConstruct
    public void postConstruct() {

        LOGGER.info("Bootstrapping FlowDefinitionTimerCache");

        this.refreshData();

        this.timer.scheduleAtFixedRate(
            new TimerTask() {

                @Override
                public void run() {

                    FlowDefinitionTimerCache.this.refreshData();
                }
            }, this.interval, this.interval);
    }

    @Override
    public void save(final FlowDefinitionData flowDefinition) {

    }

    private void refreshData() {

        LOGGER.info("Start refreshing flow definition cache data");

        final var defs = this.flowDefinitionRepository.findAll();
        final var entries = defs.stream().map(FlowDefinition::convert).toList();

        var _withId = entries
                          .stream()
                          .collect(
                              Collectors.toUnmodifiableMap(
                                  FlowDefinitionData::flowDefinitionId, Function.identity(),
                                  (a, b) -> a));

        var _withTxnTypeCurrency = entries.stream().collect(Collectors.toUnmodifiableMap(
            e -> FlowDefinitionCache.Keys.forTransaction(e.transactionType(), e.currency()),
            Function.identity(), (a, b) -> a));

        LOGGER.info("Refreshed FlowDefinition cache data, count: {}", entries.size());

        this.snapshotRef.set(new Snapshot(_withId, _withTxnTypeCurrency));
    }

    private record Snapshot(Map<FlowDefinitionId, FlowDefinitionData> withId,
                            Map<String, FlowDefinitionData> withTxnTypeCurrency) {

        static Snapshot empty() {

            return new Snapshot(Map.of(), Map.of());
        }

    }

}
