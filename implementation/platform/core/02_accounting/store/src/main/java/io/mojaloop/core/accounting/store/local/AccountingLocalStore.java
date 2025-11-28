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

package io.mojaloop.core.accounting.store.local;

import io.mojaloop.core.accounting.contract.data.AccountData;
import io.mojaloop.core.accounting.contract.query.AccountQuery;
import io.mojaloop.core.accounting.store.AccountingStore;
import io.mojaloop.core.accounting.store.AccountingStoreConfiguration;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountId;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountOwnerId;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
import io.mojaloop.core.common.datatype.type.accounting.AccountCode;
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
public class AccountingLocalStore implements AccountingStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountingLocalStore.class);

    private final AccountQuery accountQuery;

    private final AccountingStoreConfiguration.Settings accountingStoreSettings;

    private final AtomicReference<Snapshot> snapshotRef = new AtomicReference<>(Snapshot.empty());

    private final Timer timer = new Timer("AccountingLocalStoreRefreshTimer", true);

    public AccountingLocalStore(AccountQuery accountQuery,
                                AccountingStoreConfiguration.Settings accountingStoreSettings) {

        assert accountQuery != null;
        assert accountingStoreSettings != null;

        this.accountQuery = accountQuery;
        this.accountingStoreSettings = accountingStoreSettings;
    }

    @PostConstruct
    public void bootstrap() {

        var interval = this.accountingStoreSettings.refreshIntervalMs();

        LOGGER.info("Bootstrapping AccountingLocalStore");
        this.refreshData();

        this.timer.scheduleAtFixedRate(
            new TimerTask() {

                @Override
                public void run() {

                    AccountingLocalStore.this.refreshData();
                }
            }, interval, interval);
    }

    @Override
    public AccountData get(AccountCode accountCode) {

        if (accountCode == null) {
            return null;
        }

        return this.snapshotRef.get().withAccountCode.get(accountCode);
    }

    @Override
    public Set<AccountData> get(AccountOwnerId ownerId) {

        if (ownerId == null) {
            return Set.of();
        }

        return this.snapshotRef.get().withOwnerId.getOrDefault(ownerId, Set.of());
    }

    @Override
    public AccountData get(AccountId accountId) {

        if (accountId == null) {
            return null;
        }

        return this.snapshotRef.get().withAccountId.get(accountId);
    }

    @Override
    public AccountData get(ChartEntryId chartEntryId, AccountOwnerId ownerId, Currency currency) {

        if (chartEntryId == null || ownerId == null || currency == null) {
            return null;
        }

        var key = chartEntryId.getId().toString() + ":" + ownerId.getId().toString() + ":" +
                      currency.name();

        return this.snapshotRef.get().withChartEntryOwnerCurrency.get(key);
    }

    @Override
    public Set<AccountData> get(ChartEntryId chartEntryId) {

        if (chartEntryId == null) {
            return Set.of();
        }

        return this.snapshotRef.get().withChartEntryId.getOrDefault(chartEntryId, Set.of());
    }

    private void refreshData() {

        LOGGER.info("Start refreshing accounting data");

        List<AccountData> accounts = this.accountQuery.getAll();

        var _withAccountId = accounts
                                 .stream()
                                 .collect(Collectors.toUnmodifiableMap(
                                     AccountData::accountId,
                                     Function.identity(), (a, b) -> a));

        var _withAccountCode = accounts
                                   .stream()
                                   .collect(Collectors.toUnmodifiableMap(
                                       AccountData::code,
                                       Function.identity(), (a, b) -> a));

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

        var _withChartEntryOwnerCurrency = accounts.stream().collect(Collectors.toUnmodifiableMap(
            acc -> acc.chartEntryId().getId().toString() + ":" + acc.ownerId().getId().toString() +
                       ":" + acc.currency().name(), Function.identity(), (a, b) -> a));

        LOGGER.info("Refreshed Account data, count: {}", accounts.size());

        this.snapshotRef.set(new Snapshot(
            _withAccountId, _withAccountCode, _withOwnerId,
            _withChartEntryOwnerCurrency, _withChartEntryId));

    }

    private record Snapshot(Map<AccountId, AccountData> withAccountId,
                            Map<AccountCode, AccountData> withAccountCode,
                            Map<AccountOwnerId, Set<AccountData>> withOwnerId,
                            Map<String, AccountData> withChartEntryOwnerCurrency,
                            Map<ChartEntryId, Set<AccountData>> withChartEntryId) {

        static Snapshot empty() {

            return new Snapshot(Map.of(), Map.of(), Map.of(), Map.of(), Map.of());
        }

    }

}
