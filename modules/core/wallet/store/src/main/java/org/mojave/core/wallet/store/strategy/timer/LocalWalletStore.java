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

package org.mojave.core.wallet.store.strategy.timer;

import jakarta.annotation.PostConstruct;
import org.mojave.core.wallet.contract.constant.WalletDefaultTag;
import org.mojave.core.wallet.contract.data.WalletData;
import org.mojave.core.wallet.contract.query.WalletQuery;
import org.mojave.core.wallet.store.WalletStore;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.identifier.wallet.WalletId;
import org.mojave.scheme.rule.identifier.wallet.WalletOwnerId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class LocalWalletStore implements WalletStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalWalletStore.class);

    private final WalletQuery walletQuery;

    private final Settings settings;

    private final AtomicReference<Snapshot> snapshotRef = new AtomicReference<>(Snapshot.empty());

    private final Timer timer = new Timer("LocalWalletStore", true);

    public LocalWalletStore(final WalletQuery walletQuery, final Settings settings) {

        Objects.requireNonNull(walletQuery);
        Objects.requireNonNull(settings);

        this.walletQuery = walletQuery;
        this.settings = settings;
    }

    private static <K, V> Map<K, List<V>> groupBy(final List<V> values,
                                                  final Function<V, K> classifier) {

        final var grouped = values.stream().collect(Collectors.groupingBy(classifier));

        return grouped
                   .entrySet()
                   .stream()
                   .collect(Collectors.toUnmodifiableMap(
                       Map.Entry::getKey,
                       entry -> List.copyOf(entry.getValue())));
    }

    private static String walletByOwnerCurrencyKey(final WalletOwnerId ownerId,
                                                   final Currency currency) {

        return ownerId.getId() + ":" + currency.name();
    }

    private static String walletByOwnerCurrencyTagKey(final WalletOwnerId ownerId,
                                                      final Currency currency, final String tag) {

        return ownerId.getId() + ":" + currency.name() + ":" + String.valueOf(tag);
    }

    @PostConstruct
    public void bootstrap() {

        final var interval = this.settings.refreshIntervalMs();

        LOGGER.info("Bootstrapping LocalWalletStore");
        this.refreshData();

        this.timer.scheduleAtFixedRate(
            new TimerTask() {

                @Override
                public void run() {

                    LocalWalletStore.this.refreshData();
                }
            }, interval, interval);
    }

    @Override
    public WalletData getWalletData(final WalletId walletId) {

        if (walletId == null) {
            return null;
        }

        return this.snapshotRef.get().walletById().get(walletId);
    }

    @Override
    public WalletData getWalletData(final WalletOwnerId ownerId, final Currency currency,
                                    final String tag) {

        if (ownerId == null || currency == null) {
            return null;
        }

        var data = this.snapshotRef
                       .get()
                       .walletByOwnerCurrencyTag()
                       .get(walletByOwnerCurrencyTagKey(ownerId, currency, tag));

        if (data == null) {
            return this.snapshotRef
                       .get()
                       .walletByOwnerCurrencyTag()
                       .get(walletByOwnerCurrencyTagKey(
                           ownerId, currency,
                           WalletDefaultTag.DEFAULT_TAG));
        }

        return data;
    }

    @Override
    public List<WalletData> getWalletData(final WalletOwnerId ownerId) {

        if (ownerId == null) {
            return List.of();
        }

        return this.snapshotRef.get().walletByOwnerId().getOrDefault(ownerId, List.of());
    }

    @Override
    public List<WalletData> getWalletData(final WalletOwnerId ownerId, final Currency currency) {

        if (ownerId == null || currency == null) {
            return List.of();
        }

        return this.snapshotRef
                   .get()
                   .walletByOwnerCurrency()
                   .getOrDefault(walletByOwnerCurrencyKey(ownerId, currency), List.of());
    }

    private void refreshData() {

        LOGGER.info("Start refreshing wallet data");

        final var wallets = this.walletQuery.getAll();

        final var walletById = wallets
                                   .stream()
                                   .collect(Collectors.toUnmodifiableMap(
                                       WalletData::walletId,
                                       Function.identity(), (a, b) -> a));

        final var walletByOwnerCurrencyTag = wallets.stream().collect(Collectors.toUnmodifiableMap(
            wallet -> walletByOwnerCurrencyTagKey(
                wallet.walletOwnerId(), wallet.currency(), wallet.tag()), Function.identity(),
            (a, b) -> a));

        final var walletByOwnerId = groupBy(wallets, WalletData::walletOwnerId);

        final var walletByOwnerCurrency = groupBy(
            wallets,
            wallet -> walletByOwnerCurrencyKey(wallet.walletOwnerId(), wallet.currency()));

        LOGGER.info("Refreshed wallet count: {}", wallets.size());

        this.snapshotRef.set(new Snapshot(
            walletById, walletByOwnerCurrencyTag, walletByOwnerId,
            walletByOwnerCurrency));
    }

    private record Snapshot(Map<WalletId, WalletData> walletById,
                            Map<String, WalletData> walletByOwnerCurrencyTag,
                            Map<WalletOwnerId, List<WalletData>> walletByOwnerId,
                            Map<String, List<WalletData>> walletByOwnerCurrency) {

        private static Snapshot empty() {

            return new Snapshot(Map.of(), Map.of(), Map.of(), Map.of());
        }

    }

    public record Settings(int refreshIntervalMs) {

    }

}
