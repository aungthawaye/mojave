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

package io.mojaloop.core.wallet.domain.cache.strategy.local;

import io.mojaloop.core.common.datatype.identifier.wallet.PositionId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.wallet.contract.data.PositionData;
import io.mojaloop.core.wallet.domain.cache.PositionCache;
import io.mojaloop.core.wallet.domain.repository.PositionRepository;
import io.mojaloop.fspiop.spec.core.Currency;
import jakarta.annotation.PostConstruct;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PositionLocalCache implements PositionCache {

    private final PositionRepository positionRepository;

    private final Map<Long, PositionData> withId;

    private final Map<String, PositionData> withOwnerCurrency;

    private final Map<Long, Set<PositionData>> withOwnerId;

    public PositionLocalCache(final PositionRepository positionRepository) {

        assert positionRepository != null;

        this.positionRepository = positionRepository;

        this.withId = new ConcurrentHashMap<>();
        this.withOwnerCurrency = new ConcurrentHashMap<>();
        this.withOwnerId = new ConcurrentHashMap<>();
    }

    private static String key(final WalletOwnerId walletOwnerId, final Currency currency) {

        return walletOwnerId.getId().toString() + ":" + currency.name();
    }

    @Override
    public PositionData get(final PositionId positionId) {

        if (positionId == null) {
            return null;
        }

        return this.withId.get(positionId.getId());
    }

    @Override
    public PositionData get(final WalletOwnerId walletOwnerId, final Currency currency) {

        if (walletOwnerId == null || currency == null) {
            return null;
        }

        final var key = key(walletOwnerId, currency);
        return this.withOwnerCurrency.get(key);
    }

    @Override
    public Set<PositionData> get(final WalletOwnerId walletOwnerId) {

        if (walletOwnerId == null) {
            return Set.of();
        }

        final var set = this.withOwnerId.get(walletOwnerId.getId());
        return set == null ? Set.of() : Set.copyOf(set);
    }

    @PostConstruct
    public void postConstruct() {

        this.clear();

        final var positions = this.positionRepository.findAll();

        for (final var position : positions) {
            this.save(position.convert());
        }
    }

    private void clear() {

        this.withId.clear();
        this.withOwnerCurrency.clear();
        this.withOwnerId.clear();
    }

    private void save(final PositionData position) {

        this.withId.put(position.positionId().getId(), position);

        final var key = key(position.walletOwnerId(), position.currency());
        this.withOwnerCurrency.put(key, position);

        final var set = this.withOwnerId.computeIfAbsent(
            position.walletOwnerId().getId(),
            __ -> Collections.newSetFromMap(new ConcurrentHashMap<>()));
        set.add(position);
    }

}
