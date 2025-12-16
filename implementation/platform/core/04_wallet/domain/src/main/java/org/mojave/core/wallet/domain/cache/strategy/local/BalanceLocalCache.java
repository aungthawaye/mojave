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
import org.mojave.core.common.datatype.identifier.wallet.BalanceId;
import org.mojave.core.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.core.wallet.contract.data.BalanceData;
import org.mojave.core.wallet.domain.cache.BalanceCache;
import org.mojave.core.wallet.domain.repository.BalanceRepository;
import org.mojave.fspiop.spec.core.Currency;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BalanceLocalCache implements BalanceCache {

    private final BalanceRepository balanceRepository;

    private final Map<Long, BalanceData> withId;

    private final Map<String, BalanceData> withOwnerCurrency;

    private final Map<Long, Set<BalanceData>> withOwnerId;

    public BalanceLocalCache(final BalanceRepository balanceRepository) {

        assert balanceRepository != null;

        this.balanceRepository = balanceRepository;

        this.withId = new ConcurrentHashMap<>();
        this.withOwnerCurrency = new ConcurrentHashMap<>();
        this.withOwnerId = new ConcurrentHashMap<>();
    }

    private static String key(final WalletOwnerId walletOwnerId, final Currency currency) {

        return walletOwnerId.getId().toString() + ":" + currency.name();
    }

    @Override
    public BalanceData get(final BalanceId balanceId) {

        if (balanceId == null) {
            return null;
        }

        var data = this.withId.get(balanceId.getId());

        if (data == null) {

            var entity = this.balanceRepository.findById(balanceId).orElse(null);

            if (entity != null) {
                data = entity.convert();
                this.save(data);
            }

            return data;
        }

        return data;
    }

    @Override
    public BalanceData get(final WalletOwnerId walletOwnerId, final Currency currency) {

        if (walletOwnerId == null || currency == null) {
            return null;
        }

        final var key = key(walletOwnerId, currency);

        var data = this.withOwnerCurrency.get(key);

        if (data == null) {

            var entity = this.balanceRepository
                             .findOne(BalanceRepository.Filters
                                          .withOwnerId(walletOwnerId)
                                          .and(BalanceRepository.Filters.withCurrency(currency)))
                             .orElse(null);

            if (entity != null) {
                data = entity.convert();
                this.save(data);
            }

            return data;
        }

        return data;
    }

    @Override
    public Set<BalanceData> get(final WalletOwnerId walletOwnerId) {

        if (walletOwnerId == null) {
            return Set.of();
        }

        final var set = this.withOwnerId.get(walletOwnerId.getId());

        if (set == null) {

            var set2 = new HashSet<BalanceData>();

            var entities = this.balanceRepository.findAll(
                BalanceRepository.Filters.withOwnerId(walletOwnerId));

            entities.forEach((entity) -> {
                var balance = entity.convert();
                this.save(balance);
                set2.add(balance);
            });

            return set2;
        }

        return set;
    }

    @PostConstruct
    public void postConstruct() {

        this.clear();

        final var wallets = this.balanceRepository.findAll();

        for (final var wallet : wallets) {
            this.save(wallet.convert());
        }
    }

    private void clear() {

        this.withId.clear();
        this.withOwnerCurrency.clear();
        this.withOwnerId.clear();
    }

    private void save(final BalanceData wallet) {

        this.withId.put(wallet.balanceId().getId(), wallet);

        final var key = key(wallet.walletOwnerId(), wallet.currency());
        this.withOwnerCurrency.put(key, wallet);

        final var set = this.withOwnerId.computeIfAbsent(
            wallet.walletOwnerId().getId(),
            __ -> Collections.newSetFromMap(new ConcurrentHashMap<>()));
        set.add(wallet);
    }

}
