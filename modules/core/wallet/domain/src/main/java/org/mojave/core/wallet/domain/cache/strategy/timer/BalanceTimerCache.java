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
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.wallet.BalanceId;
import org.mojave.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.core.wallet.contract.data.BalanceData;
import org.mojave.core.wallet.domain.cache.BalanceCache;
import org.mojave.core.wallet.domain.repository.BalanceRepository;
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

public class BalanceTimerCache implements BalanceCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(BalanceTimerCache.class);

    private final BalanceRepository balanceRepository;

    private final int interval;

    private final AtomicReference<Snapshot> snapshotRef = new AtomicReference<>(Snapshot.empty());

    private final Timer timer = new Timer("WalletTimerCacheRefreshTimer", true);

    public BalanceTimerCache(final BalanceRepository balanceRepository, final int interval) {

        Objects.requireNonNull(balanceRepository);
        if (interval <= 0) {
            throw new IllegalArgumentException("interval must be greater than 0");
        }

        this.balanceRepository = balanceRepository;
        this.interval = interval;
    }

    private static String key(final WalletOwnerId walletOwnerId, final Currency currency) {

        return walletOwnerId.getId().toString() + ":" + currency.name();
    }

    @Override
    public BalanceData get(final BalanceId balanceId) {

        if (balanceId == null) {
            return null;
        }

        return this.snapshotRef.get().withId.get(balanceId);
    }

    @Override
    public BalanceData get(final WalletOwnerId walletOwnerId, final Currency currency) {

        if (walletOwnerId == null || currency == null) {
            return null;
        }

        final var key = key(walletOwnerId, currency);
        return this.snapshotRef.get().withOwnerCurrency.get(key);
    }

    @Override
    public Set<BalanceData> get(final WalletOwnerId walletOwnerId) {

        if (walletOwnerId == null) {
            return Set.of();
        }

        return this.snapshotRef.get().withOwnerId.getOrDefault(walletOwnerId, Set.of());
    }

    @PostConstruct
    public void postConstruct() {

        LOGGER.info("Bootstrapping BalanceTimerCache");

        this.refreshData();

        this.timer.scheduleAtFixedRate(
            new TimerTask() {

                @Override
                public void run() {

                    BalanceTimerCache.this.refreshData();
                }
            }, this.interval, this.interval);
    }

    private void refreshData() {

        LOGGER.info("Start refreshing wallet cache data");

        final var wallets = this.balanceRepository.findAll();
        final var entries = wallets
                                .stream()
                                .map(w -> w.convert())
                                .collect(Collectors.toUnmodifiableList());

        var _withId = entries
                          .stream()
                          .collect(Collectors.toUnmodifiableMap(
                              BalanceData::balanceId,
                              Function.identity(), (a, b) -> a));

        var _withOwnerCurrency = entries
                                     .stream()
                                     .collect(Collectors.toUnmodifiableMap(
                                         e -> key(e.walletOwnerId(), e.currency()),
                                         Function.identity(), (a, b) -> a));

        var _withOwnerId = Collections.unmodifiableMap(
            entries.stream().collect(Collectors.groupingBy(
                BalanceData::walletOwnerId,
                Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet))));

        LOGGER.info("Refreshed Balance cache data, count: {}", entries.size());

        this.snapshotRef.set(new Snapshot(_withId, _withOwnerCurrency, _withOwnerId));
    }

    private record Snapshot(Map<BalanceId, BalanceData> withId,
                            Map<String, BalanceData> withOwnerCurrency,
                            Map<WalletOwnerId, Set<BalanceData>> withOwnerId) {

        static Snapshot empty() {

            return new Snapshot(Map.of(), Map.of(), Map.of());
        }

    }

}
