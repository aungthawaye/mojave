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

package io.mojaloop.core.wallet.store.timer;

import io.mojaloop.core.common.datatype.identifier.wallet.WalletId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.wallet.contract.data.WalletData;
import io.mojaloop.core.wallet.contract.query.WalletQuery;
import io.mojaloop.core.wallet.store.WalletStore;
import io.mojaloop.core.wallet.store.WalletStoreConfiguration;
import io.mojaloop.fspiop.spec.core.Currency;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class WalletTimerStore implements WalletStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(WalletTimerStore.class);

    private final WalletQuery walletQuery;

    private final WalletStoreConfiguration.Settings walletStoreSettings;

    private final AtomicReference<Snapshot> snapshotRef = new AtomicReference<>(Snapshot.empty());

    private final Timer timer = new Timer("WalletLocalStoreRefreshTimer", true);

    public WalletTimerStore(final WalletQuery walletQuery,
                            final WalletStoreConfiguration.Settings walletStoreSettings) {

        assert walletQuery != null;
        assert walletStoreSettings != null;

        this.walletQuery = walletQuery;
        this.walletStoreSettings = walletStoreSettings;
    }

    private static String key(final WalletOwnerId walletOwnerId, final Currency currency) {

        return walletOwnerId.getId().toString() + ":" + currency.name();
    }

    @PostConstruct
    public void bootstrap() {

        var interval = this.walletStoreSettings.refreshIntervalMs();

        LOGGER.info("Bootstrapping WalletTimerStore");
        this.refreshData();

        this.timer.scheduleAtFixedRate(
            new TimerTask() {

                @Override
                public void run() {

                    WalletTimerStore.this.refreshData();
                }
            }, interval, interval);
    }

    @Override
    public WalletData get(final WalletId walletId) {

        if (walletId == null) {
            return null;
        }

        return this.snapshotRef.get().withWalletId.get(walletId);
    }

    @Override
    public WalletData get(final WalletOwnerId walletOwnerId, final Currency currency) {

        if (walletOwnerId == null || currency == null) {
            return null;
        }

        var key = key(walletOwnerId, currency);
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

        LOGGER.info("Start refreshing wallet data");

        List<WalletData> wallets = this.walletQuery.getAll();

        var _withWalletId = wallets
                                .stream()
                                .collect(Collectors.toUnmodifiableMap(
                                    WalletData::walletId,
                                    Function.identity(), (a, b) -> a));

        var _withOwnerId = Collections.unmodifiableMap(wallets
                                                           .stream()
                                                           .collect(Collectors.groupingBy(
                                                               WalletData::walletOwnerId,
                                                               Collectors.collectingAndThen(
                                                                   Collectors.toSet(),
                                                                   Collections::unmodifiableSet))));

        var _withOwnerCurrency = wallets
                                     .stream()
                                     .collect(Collectors.toUnmodifiableMap(
                                         w -> key(w.walletOwnerId(), w.currency()),
                                         Function.identity(), (a, b) -> a));

        LOGGER.info("Refreshed Wallet data, count: {}", wallets.size());

        this.snapshotRef.set(new Snapshot(_withWalletId, _withOwnerId, _withOwnerCurrency));
    }

    private record Snapshot(Map<WalletId, WalletData> withWalletId,
                            Map<WalletOwnerId, Set<WalletData>> withOwnerId,
                            Map<String, WalletData> withOwnerCurrency) {

        static Snapshot empty() {

            return new Snapshot(Map.of(), Map.of(), Map.of());
        }

    }

}
