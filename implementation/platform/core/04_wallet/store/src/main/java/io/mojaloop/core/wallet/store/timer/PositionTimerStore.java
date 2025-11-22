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

import io.mojaloop.core.common.datatype.identifier.wallet.PositionId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.wallet.contract.data.PositionData;
import io.mojaloop.core.wallet.contract.query.PositionQuery;
import io.mojaloop.core.wallet.store.PositionStore;
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
public class PositionTimerStore implements PositionStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(PositionTimerStore.class);

    private final PositionQuery positions;

    private final WalletStoreConfiguration.Settings walletStoreSettings;

    private final AtomicReference<Snapshot> snapshotRef = new AtomicReference<>(Snapshot.empty());

    private final Timer timer = new Timer("PositionLocalStoreRefreshTimer", true);

    public PositionTimerStore(final PositionQuery positions,
                              final WalletStoreConfiguration.Settings walletStoreSettings) {

        assert positions != null;
        assert walletStoreSettings != null;

        this.positions = positions;
        this.walletStoreSettings = walletStoreSettings;
    }

    private static String key(final WalletOwnerId walletOwnerId, final Currency currency) {

        return walletOwnerId.getId().toString() + ":" + currency.name();
    }

    @PostConstruct
    public void bootstrap() {

        var interval = this.walletStoreSettings.refreshIntervalMs();

        LOGGER.info("Bootstrapping PositionTimerStore");
        this.refreshData();

        this.timer.scheduleAtFixedRate(
            new TimerTask() {

                @Override
                public void run() {

                    PositionTimerStore.this.refreshData();
                }
            }, interval, interval);
    }

    @Override
    public PositionData get(final PositionId positionId) {

        if (positionId == null) {
            return null;
        }

        return this.snapshotRef.get().withPositionId.get(positionId);
    }

    @Override
    public PositionData get(final WalletOwnerId walletOwnerId, final Currency currency) {

        if (walletOwnerId == null || currency == null) {
            return null;
        }

        var key = key(walletOwnerId, currency);
        return this.snapshotRef.get().withOwnerCurrency.get(key);
    }

    @Override
    public Set<PositionData> get(final WalletOwnerId walletOwnerId) {

        if (walletOwnerId == null) {
            return Set.of();
        }

        return this.snapshotRef.get().withOwnerId.getOrDefault(walletOwnerId, Set.of());
    }

    private void refreshData() {

        LOGGER.info("Start refreshing position data");

        List<PositionData> positions = this.positions.getAll();

        var _withPositionId = positions
                                  .stream()
                                  .collect(Collectors.toUnmodifiableMap(
                                      PositionData::positionId,
                                      Function.identity(), (a, b) -> a));

        var _withOwnerId = Collections.unmodifiableMap(positions
                                                           .stream()
                                                           .collect(Collectors.groupingBy(
                                                               PositionData::walletOwnerId,
                                                               Collectors.collectingAndThen(
                                                                   Collectors.toSet(),
                                                                   Collections::unmodifiableSet))));

        var _withOwnerCurrency = positions
                                     .stream()
                                     .collect(Collectors.toUnmodifiableMap(
                                         w -> key(w.walletOwnerId(), w.currency()),
                                         Function.identity(), (a, b) -> a));

        LOGGER.info("Refreshed Position data, count: {}", positions.size());

        this.snapshotRef.set(new Snapshot(_withPositionId, _withOwnerId, _withOwnerCurrency));
    }

    private record Snapshot(Map<PositionId, PositionData> withPositionId,
                            Map<WalletOwnerId, Set<PositionData>> withOwnerId,
                            Map<String, PositionData> withOwnerCurrency) {

        static Snapshot empty() {

            return new Snapshot(Map.of(), Map.of(), Map.of());
        }

    }

}
