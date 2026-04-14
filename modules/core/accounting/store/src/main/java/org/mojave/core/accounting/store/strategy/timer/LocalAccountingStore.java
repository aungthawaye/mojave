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

package org.mojave.core.accounting.store.strategy.timer;

import jakarta.annotation.PostConstruct;
import org.mojave.core.accounting.contract.data.AccountData;
import org.mojave.core.accounting.contract.data.CoaData;
import org.mojave.core.accounting.contract.data.CoaEntryData;
import org.mojave.core.accounting.contract.data.FlowDefinitionData;
import org.mojave.core.accounting.contract.query.AccountQuery;
import org.mojave.core.accounting.contract.query.CoaEntryQuery;
import org.mojave.core.accounting.contract.query.CoaQuery;
import org.mojave.core.accounting.contract.query.FlowDefinitionQuery;
import org.mojave.core.accounting.store.AccountingStore;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.identifier.accounting.AccountId;
import org.mojave.scheme.rule.identifier.accounting.AccountOwnerId;
import org.mojave.scheme.rule.identifier.accounting.CoaEntryId;
import org.mojave.scheme.rule.identifier.accounting.CoaId;
import org.mojave.scheme.rule.identifier.accounting.FlowDefinitionId;
import org.mojave.scheme.rule.scenario.ScenarioType;
import org.mojave.scheme.rule.type.accounting.AccountCode;
import org.mojave.scheme.rule.type.accounting.CoaEntryCode;
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

public final class LocalAccountingStore implements AccountingStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalAccountingStore.class);

    private final AccountQuery accountQuery;

    private final CoaQuery coaQuery;

    private final CoaEntryQuery coaEntryQuery;

    private final FlowDefinitionQuery flowDefinitionQuery;

    private final Settings settings;

    private final AtomicReference<Snapshot> snapshotRef = new AtomicReference<>(Snapshot.empty());

    private final Timer timer = new Timer("LocalAccountingStore", true);

    public LocalAccountingStore(final AccountQuery accountQuery,
                                final CoaQuery coaQuery,
                                final CoaEntryQuery coaEntryQuery,
                                final FlowDefinitionQuery flowDefinitionQuery,
                                final Settings settings) {

        Objects.requireNonNull(accountQuery);
        Objects.requireNonNull(coaQuery);
        Objects.requireNonNull(coaEntryQuery);
        Objects.requireNonNull(flowDefinitionQuery);
        Objects.requireNonNull(settings);

        this.accountQuery = accountQuery;
        this.coaQuery = coaQuery;
        this.coaEntryQuery = coaEntryQuery;
        this.flowDefinitionQuery = flowDefinitionQuery;
        this.settings = settings;
    }

    @PostConstruct
    public void bootstrap() {

        final var interval = this.settings.refreshIntervalMs();

        LOGGER.info("Bootstrapping LocalAccountingStore");
        this.refreshData();

        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                LocalAccountingStore.this.refreshData();
            }
        }, interval, interval);
    }

    @Override
    public AccountData getAccountData(final AccountId accountId) {

        if (accountId == null) {
            return null;
        }

        return this.snapshotRef.get().accountById().get(accountId);
    }

    @Override
    public AccountData getAccountData(final AccountCode accountCode) {

        if (accountCode == null) {
            return null;
        }

        return this.snapshotRef.get().accountByCode().get(accountCode);
    }

    @Override
    public List<AccountData> getAccountData(final AccountOwnerId ownerId) {

        if (ownerId == null) {
            return List.of();
        }

        return this.snapshotRef.get().accountByOwnerId().getOrDefault(ownerId, List.of());
    }

    @Override
    public AccountData getAccountData(final CoaEntryId coaEntryId,
                                      final AccountOwnerId ownerId,
                                      final Currency currency) {

        if (coaEntryId == null || ownerId == null || currency == null) {
            return null;
        }

        return this.snapshotRef.get().accountByCoaEntryOwnerCurrency()
                   .get(accountByCoaEntryOwnerCurrencyKey(coaEntryId, ownerId, currency));
    }

    @Override
    public List<AccountData> getAccountData(final CoaEntryId coaEntryId) {

        if (coaEntryId == null) {
            return List.of();
        }

        return this.snapshotRef.get().accountByCoaEntryId().getOrDefault(coaEntryId, List.of());
    }

    @Override
    public CoaData getCoaData(final CoaId coaId) {

        if (coaId == null) {
            return null;
        }

        return this.snapshotRef.get().coaById().get(coaId);
    }

    @Override
    public CoaEntryData getCoaEntryData(final CoaEntryId coaEntryId) {

        if (coaEntryId == null) {
            return null;
        }

        return this.snapshotRef.get().coaEntryById().get(coaEntryId);
    }

    @Override
    public CoaEntryData getCoaEntryData(final CoaEntryCode coaEntryCode) {

        if (coaEntryCode == null) {
            return null;
        }

        return this.snapshotRef.get().coaEntryByCode().get(coaEntryCode);
    }

    @Override
    public List<CoaEntryData> getCoaEntryData(final CoaId coaId) {

        if (coaId == null) {
            return List.of();
        }

        return this.snapshotRef.get().coaEntryByCoaId().getOrDefault(coaId, List.of());
    }

    @Override
    public List<CoaEntryData> getCoaEntryData(final String category) {

        if (category == null) {
            return List.of();
        }

        return this.snapshotRef.get().coaEntryByCategory().getOrDefault(category, List.of());
    }

    @Override
    public FlowDefinitionData getFlowDefinitionData(final FlowDefinitionId flowDefinitionId) {

        if (flowDefinitionId == null) {
            return null;
        }

        return this.snapshotRef.get().flowDefinitionById().get(flowDefinitionId);
    }

    @Override
    public FlowDefinitionData getFlowDefinitionData(final ScenarioType scenarioType,
                                                    final Currency currency) {

        if (scenarioType == null || currency == null) {
            return null;
        }

        return this.snapshotRef.get().flowDefinitionByScenarioCurrency()
                   .get(flowDefinitionByScenarioCurrencyKey(scenarioType, currency));
    }

    private void refreshData() {

        LOGGER.info("Start refreshing accounting data");

        final var accounts = this.accountQuery.getAll();
        final var coas = this.coaQuery.getAll();
        final var coaEntries = this.coaEntryQuery.getAll();
        final var flowDefinitions = this.flowDefinitionQuery.getAll();

        final var accountById = accounts.stream().collect(
            Collectors.toUnmodifiableMap(AccountData::accountId, Function.identity(), (a, b) -> a));

        final var accountByCode = accounts.stream().collect(
            Collectors.toUnmodifiableMap(AccountData::code, Function.identity(), (a, b) -> a));

        final var accountByOwnerId = groupBy(accounts, AccountData::ownerId);

        final var accountByCoaEntryId = groupBy(accounts, AccountData::coaEntryId);

        final var accountByCoaEntryOwnerCurrency = accounts.stream().collect(
            Collectors.toUnmodifiableMap(
                account -> accountByCoaEntryOwnerCurrencyKey(
                    account.coaEntryId(), account.ownerId(), account.currency()),
                Function.identity(),
                (a, b) -> a));

        final var coaById = coas.stream().collect(
            Collectors.toUnmodifiableMap(CoaData::coaId, Function.identity(), (a, b) -> a));

        final var coaEntryById = coaEntries.stream().collect(
            Collectors.toUnmodifiableMap(
                CoaEntryData::coaEntryId, Function.identity(), (a, b) -> a));

        final var coaEntryByCode = coaEntries.stream().collect(
            Collectors.toUnmodifiableMap(CoaEntryData::code, Function.identity(), (a, b) -> a));

        final var coaEntryByCoaId = groupBy(coaEntries, CoaEntryData::coaId);

        final var coaEntryByCategory = groupBy(coaEntries, CoaEntryData::category);

        final var flowDefinitionById = flowDefinitions.stream().collect(
            Collectors.toUnmodifiableMap(
                FlowDefinitionData::flowDefinitionId, Function.identity(), (a, b) -> a));

        final var flowDefinitionByScenarioCurrency = flowDefinitions.stream().collect(
            Collectors.toUnmodifiableMap(
                flowDefinition -> flowDefinitionByScenarioCurrencyKey(
                    flowDefinition.scenario(), flowDefinition.currency()),
                Function.identity(),
                (a, b) -> a));

        LOGGER.info(
            "Refreshed account count: {} | coa count: {} | coa entry count: {} | flow definition count: {}",
            accounts.size(),
            coas.size(),
            coaEntries.size(),
            flowDefinitions.size());

        this.snapshotRef.set(new Snapshot(
            accountById,
            accountByCode,
            accountByOwnerId,
            accountByCoaEntryId,
            accountByCoaEntryOwnerCurrency,
            coaById,
            coaEntryById,
            coaEntryByCode,
            coaEntryByCoaId,
            coaEntryByCategory,
            flowDefinitionById,
            flowDefinitionByScenarioCurrency));
    }

    private static String accountByCoaEntryOwnerCurrencyKey(final CoaEntryId coaEntryId,
                                                            final AccountOwnerId ownerId,
                                                            final Currency currency) {

        return coaEntryId.getId() + ":" + ownerId.getId() + ":" + currency.name();
    }

    private static String flowDefinitionByScenarioCurrencyKey(final ScenarioType scenarioType,
                                                              final Currency currency) {

        return scenarioType.name() + ":" + currency.name();
    }

    private static <K, V> Map<K, List<V>> groupBy(final List<V> values,
                                                  final Function<V, K> classifier) {

        final var grouped = values.stream().collect(Collectors.groupingBy(classifier));

        return grouped.entrySet().stream().collect(
            Collectors.toUnmodifiableMap(
                Map.Entry::getKey,
                entry -> List.copyOf(entry.getValue())));
    }

    private record Snapshot(Map<AccountId, AccountData> accountById,
                            Map<AccountCode, AccountData> accountByCode,
                            Map<AccountOwnerId, List<AccountData>> accountByOwnerId,
                            Map<CoaEntryId, List<AccountData>> accountByCoaEntryId,
                            Map<String, AccountData> accountByCoaEntryOwnerCurrency,
                            Map<CoaId, CoaData> coaById,
                            Map<CoaEntryId, CoaEntryData> coaEntryById,
                            Map<CoaEntryCode, CoaEntryData> coaEntryByCode,
                            Map<CoaId, List<CoaEntryData>> coaEntryByCoaId,
                            Map<String, List<CoaEntryData>> coaEntryByCategory,
                            Map<FlowDefinitionId, FlowDefinitionData> flowDefinitionById,
                            Map<String, FlowDefinitionData> flowDefinitionByScenarioCurrency) {

        private static Snapshot empty() {

            return new Snapshot(
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of());
        }

    }

    public record Settings(int refreshIntervalMs) {

    }

}
