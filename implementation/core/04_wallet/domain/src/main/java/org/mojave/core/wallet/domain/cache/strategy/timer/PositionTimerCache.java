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
package org.mojave.core.wallet.domain.cache.strategy.timer;

import jakarta.annotation.PostConstruct;
import org.mojave.core.common.datatype.identifier.wallet.PositionId;
import org.mojave.core.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.core.wallet.contract.data.PositionData;
import org.mojave.core.wallet.domain.cache.PositionCache;
import org.mojave.core.wallet.domain.repository.PositionRepository;
import org.mojave.scheme.fspiop.core.Currency;
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

public class PositionTimerCache implements PositionCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(PositionTimerCache.class);

    private final PositionRepository positionRepository;

    private final int interval;

    private final AtomicReference<Snapshot> snapshotRef = new AtomicReference<>(Snapshot.empty());

    private final Timer timer = new Timer("PositionTimerCacheRefreshTimer", true);

    public PositionTimerCache(final PositionRepository positionRepository, final int interval) {

        assert positionRepository != null;
        assert interval > 0;

        this.positionRepository = positionRepository;
        this.interval = interval;
    }

    private static String key(final WalletOwnerId walletOwnerId, final Currency currency) {

        return walletOwnerId.getId().toString() + ":" + currency.name();
    }

    @Override
    public PositionData get(final PositionId positionId) {

        if (positionId == null) {
            return null;
        }

        return this.snapshotRef.get().withId.get(positionId);
    }

    @Override
    public PositionData get(final WalletOwnerId walletOwnerId, final Currency currency) {

        if (walletOwnerId == null || currency == null) {
            return null;
        }

        final var key = key(walletOwnerId, currency);
        return this.snapshotRef.get().withOwnerCurrency.get(key);
    }

    @Override
    public Set<PositionData> get(final WalletOwnerId walletOwnerId) {

        if (walletOwnerId == null) {
            return Set.of();
        }

        return this.snapshotRef.get().withOwnerId.getOrDefault(walletOwnerId, Set.of());
    }

    @PostConstruct
    public void postConstruct() {

        LOGGER.info("Bootstrapping PositionTimerCache");

        this.refreshData();

        this.timer.scheduleAtFixedRate(
            new TimerTask() {

                @Override
                public void run() {

                    PositionTimerCache.this.refreshData();
                }
            }, this.interval, this.interval);
    }

    private void refreshData() {

        LOGGER.info("Start refreshing position cache data");

        final var positions = this.positionRepository.findAll();
        final var entries = positions
                                .stream()
                                .map(p -> p.convert())
                                .collect(Collectors.toUnmodifiableList());

        var _withId = entries
                          .stream()
                          .collect(Collectors.toUnmodifiableMap(
                              PositionData::positionId, Function.identity(), (a, b) -> a));

        var _withOwnerCurrency = entries
                                     .stream()
                                     .collect(Collectors.toUnmodifiableMap(
                                         e -> key(e.walletOwnerId(), e.currency()),
                                         Function.identity(), (a, b) -> a));

        var _withOwnerId = Collections.unmodifiableMap(entries
                                                           .stream()
                                                           .collect(Collectors.groupingBy(
                                                               PositionData::walletOwnerId,
                                                               Collectors.collectingAndThen(
                                                                   Collectors.toSet(),
                                                                   Collections::unmodifiableSet))));

        LOGGER.info("Refreshed Position cache data, count: {}", entries.size());

        this.snapshotRef.set(new Snapshot(_withId, _withOwnerCurrency, _withOwnerId));
    }

    private record Snapshot(Map<PositionId, PositionData> withId,
                            Map<String, PositionData> withOwnerCurrency,
                            Map<WalletOwnerId, Set<PositionData>> withOwnerId) {

        static Snapshot empty() {

            return new Snapshot(Map.of(), Map.of(), Map.of());
        }

    }

}
