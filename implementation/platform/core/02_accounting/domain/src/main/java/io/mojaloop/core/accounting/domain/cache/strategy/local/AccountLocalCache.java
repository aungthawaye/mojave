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

package io.mojaloop.core.accounting.domain.cache.strategy.local;

import io.mojaloop.core.accounting.contract.data.AccountData;
import io.mojaloop.core.accounting.domain.cache.AccountCache;
import io.mojaloop.core.accounting.domain.repository.AccountRepository;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountId;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountOwnerId;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
import io.mojaloop.core.common.datatype.type.accounting.AccountCode;
import io.mojaloop.fspiop.spec.core.Currency;
import jakarta.annotation.PostConstruct;

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

    private final Map<String, AccountData> withChartEntryIdOwnerIdCurrency;

    public AccountLocalCache(final AccountRepository accountRepository) {

        assert accountRepository != null;

        this.accountRepository = accountRepository;

        this.withId = new ConcurrentHashMap<>();
        this.withCode = new ConcurrentHashMap<>();
        this.withOwnerId = new ConcurrentHashMap<>();
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

        return this.withCode.get(accountCode.value());
    }

    @Override
    public Set<AccountData> get(final AccountOwnerId ownerId) {

        final var set = this.withOwnerId.get(ownerId.getId());
        return set == null ? Set.of() : new HashSet<>(set);
    }

    @Override
    public AccountData get(final AccountId accountId) {

        return this.withId.get(accountId.getId());
    }

    @Override
    public AccountData get(final ChartEntryId chartEntryId,
                           final AccountOwnerId ownerId,
                           final Currency currency) {

        final var key = AccountCache.Keys.forChart(chartEntryId, ownerId, currency);
        return this.withChartEntryIdOwnerIdCurrency.get(key);
    }

    @Override
    public Set<AccountData> get(final ChartEntryId chartEntryId) {

        final var result = new HashSet<AccountData>();

        for (final var account : this.withId.values()) {
            if (account.chartEntryId().equals(chartEntryId)) {
                result.add(account);
            }
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

        final var key = AccountCache.Keys.forChart(
            account.chartEntryId(), account.ownerId(), account.currency());
        this.withChartEntryIdOwnerIdCurrency.put(key, account);
    }

}
