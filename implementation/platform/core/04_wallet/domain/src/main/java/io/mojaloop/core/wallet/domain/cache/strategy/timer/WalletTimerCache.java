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
package io.mojaloop.core.wallet.domain.cache.strategy.timer;

import io.mojaloop.core.common.datatype.identifier.wallet.WalletId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.wallet.contract.data.WalletData;
import io.mojaloop.core.wallet.domain.cache.WalletCache;
import io.mojaloop.core.wallet.domain.repository.WalletRepository;
import io.mojaloop.fspiop.spec.core.Currency;
import jakarta.annotation.PostConstruct;
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

public class WalletTimerCache implements WalletCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(WalletTimerCache.class);

    private final WalletRepository walletRepository;

    private final int interval;

    private final AtomicReference<Snapshot> snapshotRef = new AtomicReference<>(Snapshot.empty());

    private final Timer timer = new Timer("WalletTimerCacheRefreshTimer", true);

    public WalletTimerCache(final WalletRepository walletRepository, final int interval) {

        assert walletRepository != null;
        assert interval > 0;

        this.walletRepository = walletRepository;
        this.interval = interval;
    }

    @PostConstruct
    public void postConstruct() {

        LOGGER.info("Bootstrapping WalletTimerCache");

        this.refreshData();

        this.timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                WalletTimerCache.this.refreshData();
            }
        }, this.interval, this.interval);
    }

    @Override
    public WalletData get(final WalletId walletId) {

        if (walletId == null) {
            return null;
        }

        return this.snapshotRef.get().withId.get(walletId);
    }

    @Override
    public WalletData get(final WalletOwnerId walletOwnerId, final Currency currency) {

        if (walletOwnerId == null || currency == null) {
            return null;
        }

        final var key = key(walletOwnerId, currency);
        return this.snapshotRef.get().withOwnerCurrency.get(key);
    }

    @Override
    public Set<WalletData> get(final WalletOwnerId walletOwnerId) {

        if (walletOwnerId == null) {
            return Set.of();
        }

        return this.snapshotRef.get().withOwnerId.getOrDefault(walletOwnerId, Set.of());
    }

    private void refreshData() {

        LOGGER.info("Start refreshing wallet cache data");

        final var wallets = this.walletRepository.findAll();
        final var entries = wallets.stream().map(w -> w.convert()).collect(Collectors.toUnmodifiableList());

        var _withId = entries.stream().collect(Collectors.toUnmodifiableMap(WalletData::walletId, Function.identity(), (a, b) -> a));

        var _withOwnerCurrency = entries.stream()
                                        .collect(Collectors.toUnmodifiableMap(e -> key(e.walletOwnerId(), e.currency()), Function.identity(), (a, b) -> a));

        var _withOwnerId = Collections.unmodifiableMap(
            entries.stream().collect(Collectors.groupingBy(WalletData::walletOwnerId, Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet))));

        LOGGER.info("Refreshed Wallet cache data, count: {}", entries.size());

        this.snapshotRef.set(new Snapshot(_withId, _withOwnerCurrency, _withOwnerId));
    }

    private static String key(final WalletOwnerId walletOwnerId, final Currency currency) {

        return walletOwnerId.getId().toString() + ":" + currency.name();
    }

    private record Snapshot(Map<WalletId, WalletData> withId,
                            Map<String, WalletData> withOwnerCurrency,
                            Map<WalletOwnerId, Set<WalletData>> withOwnerId) {

        static Snapshot empty() {

            return new Snapshot(Map.of(), Map.of(), Map.of());
        }

    }
}
