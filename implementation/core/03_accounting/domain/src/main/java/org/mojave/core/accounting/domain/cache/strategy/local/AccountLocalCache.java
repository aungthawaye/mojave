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
package org.mojave.core.accounting.domain.cache.strategy.local;

import jakarta.annotation.PostConstruct;
import org.mojave.core.accounting.contract.data.AccountData;
import org.mojave.core.accounting.domain.cache.AccountCache;
import org.mojave.core.accounting.domain.repository.AccountRepository;
import org.mojave.core.common.datatype.identifier.accounting.AccountId;
import org.mojave.core.common.datatype.identifier.accounting.AccountOwnerId;
import org.mojave.core.common.datatype.identifier.accounting.ChartEntryId;
import org.mojave.core.common.datatype.type.accounting.AccountCode;
import org.mojave.scheme.fspiop.core.Currency;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AccountLocalCache implements AccountCache {

    private final AccountRepository accountRepository;

    private final Map<Long, AccountData> withId;

    private final Map<String, AccountData> withCode;

    private final Map<Long, Set<AccountData>> withOwnerId;

    private final Map<Long, Set<AccountData>> withChartEntryId;

    private final Map<String, AccountData> withChartEntryIdOwnerIdCurrency;

    public AccountLocalCache(final AccountRepository accountRepository) {

        assert accountRepository != null;

        this.accountRepository = accountRepository;

        this.withId = new ConcurrentHashMap<>();
        this.withCode = new ConcurrentHashMap<>();
        this.withOwnerId = new ConcurrentHashMap<>();
        this.withChartEntryId = new ConcurrentHashMap<>();
        this.withChartEntryIdOwnerIdCurrency = new ConcurrentHashMap<>();
    }

    @Override
    public void clear() {

        this.withId.clear();
        this.withCode.clear();
        this.withOwnerId.clear();
        this.withChartEntryIdOwnerIdCurrency.clear();
    }

    @Override
    public void delete(final AccountId accountId) {

        final var deleted = this.withId.remove(accountId.getId());

        if (deleted == null) {
            return;
        }

        this.withCode.remove(deleted.code().value());

        final var set = this.withOwnerId.get(deleted.ownerId().getId());

        if (set != null && !set.isEmpty()) {
            set.removeIf(a -> a.accountId().equals(accountId));
        }

        final var key = AccountCache.Keys.forChart(
            deleted.chartEntryId(), deleted.ownerId(), deleted.currency());
        this.withChartEntryIdOwnerIdCurrency.remove(key);
    }

    @Override
    public AccountData get(final AccountCode accountCode) {

        if (accountCode == null) {
            return null;
        }

        var data = this.withCode.get(accountCode.value());

        if (data == null) {

            var entity = this.accountRepository
                             .findOne(AccountRepository.Filters.withCode(accountCode))
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
    public Set<AccountData> get(final AccountOwnerId ownerId) {

        if (ownerId == null) {
            return Set.of();
        }

        final var set = this.withOwnerId.get(ownerId.getId());

        if (set == null) {

            var set2 = new HashSet<AccountData>();

            var entities = this.accountRepository.findAll(
                AccountRepository.Filters.withOwnerId(ownerId));

            entities.forEach((entity) -> {
                var account = entity.convert();
                this.save(account);
                set2.add(account);
            });

            return set2;
        }

        return set;
    }

    @Override
    public AccountData get(final AccountId accountId) {

        if (accountId == null) {
            return null;
        }

        var data = this.withId.get(accountId.getId());

        if (data == null) {

            var entity = this.accountRepository.findById(accountId).orElse(null);

            if (entity != null) {
                data = entity.convert();
                this.save(data);
            }

            return data;
        }

        return data;
    }

    @Override
    public AccountData get(final ChartEntryId chartEntryId,
                           final AccountOwnerId ownerId,
                           final Currency currency) {

        if (chartEntryId == null || ownerId == null || currency == null) {
            return null;
        }

        final var key = AccountCache.Keys.forChart(chartEntryId, ownerId, currency);

        var data = this.withChartEntryIdOwnerIdCurrency.get(key);

        if (data == null) {

            var entity = this.accountRepository
                             .findOne(AccountRepository.Filters
                                          .withChartEntryId(chartEntryId)
                                          .and(AccountRepository.Filters.withOwnerId(ownerId))
                                          .and(AccountRepository.Filters.withCurrency(currency)))
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
    public Set<AccountData> get(final ChartEntryId chartEntryId) {

        if (chartEntryId == null) {
            return Set.of();
        }

        final var result = this.withChartEntryId.getOrDefault(chartEntryId.getId(), Set.of());

        if (result.isEmpty()) {
            final var entities = this.accountRepository.findAll(
                AccountRepository.Filters.withChartEntryId(chartEntryId));

            entities.forEach(entity -> {
                final var account = entity.convert();
                this.save(account);
                result.add(account);
            });
        }

        return result;
    }

    @PostConstruct
    public void postConstruct() {

        this.clear();

        final var accounts = this.accountRepository.findAll();

        for (final var account : accounts) {
            this.save(account.convert());
        }
    }

    @Override
    public void save(final AccountData account) {

        this.withId.put(account.accountId().getId(), account);
        this.withCode.put(account.code().value(), account);

        final var set = this.withOwnerId.computeIfAbsent(
            account.ownerId().getId(), __ -> Collections.newSetFromMap(new ConcurrentHashMap<>()));
        set.add(account);

        final var set2 = this.withChartEntryId.computeIfAbsent(
            account.chartEntryId().getId(),
            __ -> Collections.newSetFromMap(new ConcurrentHashMap<>()));
        set2.add(account);

        final var key = AccountCache.Keys.forChart(
            account.chartEntryId(), account.ownerId(), account.currency());
        this.withChartEntryIdOwnerIdCurrency.put(key, account);
    }

}
