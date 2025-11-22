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

package io.mojaloop.core.accounting.domain.cache.strategy.timer;

import io.mojaloop.core.accounting.contract.data.AccountData;
import io.mojaloop.core.accounting.domain.cache.AccountCache;
import io.mojaloop.core.accounting.domain.model.Account;
import io.mojaloop.core.accounting.domain.repository.AccountRepository;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountId;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountOwnerId;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
import io.mojaloop.core.common.datatype.type.accounting.AccountCode;
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

public class AccountTimerCache implements AccountCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountTimerCache.class);

    private final AccountRepository accountRepository;

    private final AtomicReference<Snapshot> snapshotRef = new AtomicReference<>(Snapshot.empty());

    private final Timer timer = new Timer("AccountLocalCacheRefreshTimer", true);

    private final int interval;

    public AccountTimerCache(final AccountRepository accountRepository, int interval) {

        assert accountRepository != null;
        assert interval > 0;

        this.accountRepository = accountRepository;
        this.interval = interval;
    }

    @Override
    public void clear() {

        this.snapshotRef.set(Snapshot.empty());
    }

    @Override
    public void delete(final AccountId accountId) {

    }

    @Override
    public AccountData get(final AccountCode accountCode) {

        if (accountCode == null) {
            return null;
        }

        return this.snapshotRef.get().withCode.get(accountCode);
    }

    @Override
    public Set<AccountData> get(final AccountOwnerId ownerId) {

        if (ownerId == null) {
            return Set.of();
        }

        return this.snapshotRef.get().withOwnerId.getOrDefault(ownerId, Set.of());
    }

    @Override
    public AccountData get(final AccountId accountId) {

        if (accountId == null) {
            return null;
        }

        return this.snapshotRef.get().withId.get(accountId);
    }

    @Override
    public AccountData get(final ChartEntryId chartEntryId,
                           final AccountOwnerId ownerId,
                           final Currency currency) {

        if (chartEntryId == null || ownerId == null || currency == null) {
            return null;
        }

        final var key = AccountCache.Keys.forChart(chartEntryId, ownerId, currency);
        return this.snapshotRef.get().withChartEntryIdOwnerIdCurrency.get(key);
    }

    @Override
    public Set<AccountData> get(final ChartEntryId chartEntryId) {

        if (chartEntryId == null) {
            return Set.of();
        }

        return this.snapshotRef.get().withChartEntryId.getOrDefault(chartEntryId, Set.of());
    }

    @PostConstruct
    public void postConstruct() {

        LOGGER.info("Bootstrapping AccountTimerCache");

        this.refreshData();

        this.timer.scheduleAtFixedRate(
            new TimerTask() {

                @Override
                public void run() {

                    AccountTimerCache.this.refreshData();
                }
            }, this.interval, this.interval);
    }

    @Override
    public void save(final AccountData account) {

    }

    private void refreshData() {

        LOGGER.info("Start refreshing account cache data");

        final var entities = this.accountRepository.findAll();
        final var accounts = entities.stream().map(Account::convert).toList();

        var _withId = accounts
                          .stream()
                          .collect(Collectors.toUnmodifiableMap(
                              AccountData::accountId,
                              Function.identity(), (a, b) -> a));

        var _withCode = accounts
                            .stream()
                            .collect(
                                Collectors.toUnmodifiableMap(
                                    AccountData::code, Function.identity(),
                                    (a, b) -> a));

        var _withOwnerId = Collections.unmodifiableMap(accounts
                                                           .stream()
                                                           .collect(Collectors.groupingBy(
                                                               AccountData::ownerId,
                                                               Collectors.collectingAndThen(
                                                                   Collectors.toSet(),
                                                                   Collections::unmodifiableSet))));

        var _withChartEntryId = Collections.unmodifiableMap(accounts
                                                                .stream()
                                                                .collect(Collectors.groupingBy(
                                                                    AccountData::chartEntryId,
                                                                    Collectors.collectingAndThen(
                                                                        Collectors.toSet(),
                                                                        Collections::unmodifiableSet))));

        var _withChartEntryIdOwnerIdCurrency = accounts
                                                   .stream()
                                                   .collect(Collectors.toUnmodifiableMap(
                                                       acc -> AccountCache.Keys.forChart(
                                                           acc.chartEntryId(), acc.ownerId(),
                                                           acc.currency()), Function.identity(),
                                                       (a, b) -> a));

        LOGGER.info("Refreshed Account cache data, count: {}", accounts.size());

        this.snapshotRef.set(
            new Snapshot(
                _withId, _withCode, _withOwnerId, _withChartEntryIdOwnerIdCurrency,
                _withChartEntryId));
    }

    private record Snapshot(Map<AccountId, AccountData> withId,
                            Map<AccountCode, AccountData> withCode,
                            Map<AccountOwnerId, Set<AccountData>> withOwnerId,
                            Map<String, AccountData> withChartEntryIdOwnerIdCurrency,
                            Map<ChartEntryId, Set<AccountData>> withChartEntryId) {

        static Snapshot empty() {

            return new Snapshot(Map.of(), Map.of(), Map.of(), Map.of(), Map.of());
        }

    }

}
