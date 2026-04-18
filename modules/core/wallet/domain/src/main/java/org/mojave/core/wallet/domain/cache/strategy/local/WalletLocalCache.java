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

package org.mojave.core.wallet.domain.cache.strategy.local;

import jakarta.annotation.PostConstruct;
import org.mojave.core.wallet.contract.data.WalletData;
import org.mojave.core.wallet.domain.cache.WalletCache;
import org.mojave.core.wallet.domain.repository.WalletRepository;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.identifier.wallet.WalletId;
import org.mojave.scheme.rule.identifier.wallet.WalletOwnerId;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WalletLocalCache implements WalletCache {

    private final WalletRepository walletRepository;

    private final Map<Long, WalletData> withId;

    private final Map<String, WalletData> withOwnerCurrencyTag;

    private final Map<Long, Set<WalletData>> withOwnerId;

    public WalletLocalCache(final WalletRepository walletRepository) {

        Objects.requireNonNull(walletRepository);

        this.walletRepository = walletRepository;
        this.withId = new ConcurrentHashMap<>();
        this.withOwnerCurrencyTag = new ConcurrentHashMap<>();
        this.withOwnerId = new ConcurrentHashMap<>();
    }

    private static String key(final WalletOwnerId walletOwnerId, final Currency currency,
                              final String tag) {

        return WalletCache.Key.get(walletOwnerId, currency, tag);
    }

    public void clear() {

        this.withId.clear();
        this.withOwnerCurrencyTag.clear();
        this.withOwnerId.clear();
    }

    @Override
    public WalletData get(final WalletId walletId) {

        if (walletId == null) {
            return null;
        }

        var data = this.withId.get(walletId.getId());

        if (data == null) {
            final var entity = this.walletRepository.findById(walletId).orElse(null);

            if (entity != null) {
                data = entity.convert();
                this.save(data);
            }
        }

        return data;
    }

    @Override
    public WalletData get(final WalletOwnerId walletOwnerId, final Currency currency,
                          final String tag) {

        if (walletOwnerId == null || currency == null || tag == null) {
            return null;
        }

        final var key = key(walletOwnerId, currency, tag);

        var data = this.withOwnerCurrencyTag.get(key);

        if (data == null) {

            final var entity = this.walletRepository
                                   .findOne(WalletRepository.Filters
                                                .withOwnerId(walletOwnerId)
                                                .and(
                                                    WalletRepository.Filters.withCurrency(currency))
                                                .and(WalletRepository.Filters.withTag(tag)))
                                   .orElse(null);

            if (entity != null) {
                data = entity.convert();
                this.save(data);
            }
        }

        return data;
    }

    @Override
    public Set<WalletData> get(final WalletOwnerId walletOwnerId) {

        if (walletOwnerId == null) {
            return Set.of();
        }

        final var set = this.withOwnerId.get(walletOwnerId.getId());

        if (set == null) {

            final var set2 = new HashSet<WalletData>();

            final var entities = this.walletRepository.findAll(
                WalletRepository.Filters.withOwnerId(walletOwnerId));

            entities.forEach((entity) -> {
                final var wallet = entity.convert();
                this.save(wallet);
                set2.add(wallet);
            });

            return set2;
        }

        return set;
    }

    public void save(final WalletData wallet) {

        this.withId.put(wallet.walletId().getId(), wallet);

        final var key = key(wallet.walletOwnerId(), wallet.currency(), wallet.tag());
        this.withOwnerCurrencyTag.put(key, wallet);

        final var set = this.withOwnerId.computeIfAbsent(
            wallet.walletOwnerId().getId(),
            __ -> Collections.newSetFromMap(new ConcurrentHashMap<>()));

        set.add(wallet);
    }

    @PostConstruct
    public void postConstruct() {

        this.clear();

        final var wallets = this.walletRepository.findAll();

        wallets.forEach((wallet) -> this.save(wallet.convert()));
    }

}
