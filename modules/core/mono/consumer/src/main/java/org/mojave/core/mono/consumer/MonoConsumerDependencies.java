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

package org.mojave.core.mono.consumer;

import org.mojave.core.accounting.domain.cache.AccountCache;
import org.mojave.core.accounting.domain.cache.ChartEntryCache;
import org.mojave.core.accounting.domain.cache.FlowDefinitionCache;
import org.mojave.core.accounting.domain.cache.strategy.timer.AccountTimerCache;
import org.mojave.core.accounting.domain.cache.strategy.timer.ChartEntryTimerCache;
import org.mojave.core.accounting.domain.cache.strategy.timer.FlowDefinitionTimerCache;
import org.mojave.core.accounting.domain.component.ledger.Ledger;
import org.mojave.core.accounting.domain.component.ledger.strategy.MySqlLedger;
import org.mojave.core.accounting.domain.repository.AccountRepository;
import org.mojave.core.accounting.domain.repository.ChartEntryRepository;
import org.mojave.core.accounting.domain.repository.FlowDefinitionRepository;
import org.mojave.core.wallet.domain.cache.BalanceCache;
import org.mojave.core.wallet.domain.cache.PositionCache;
import org.mojave.core.wallet.domain.cache.strategy.timer.BalanceTimerCache;
import org.mojave.core.wallet.domain.cache.strategy.timer.PositionTimerCache;
import org.mojave.core.wallet.domain.component.BalanceUpdater;
import org.mojave.core.wallet.domain.component.PositionUpdater;
import org.mojave.core.wallet.domain.component.mysql.MySqlBalanceUpdater;
import org.mojave.core.wallet.domain.component.mysql.MySqlPositionUpdater;
import org.mojave.core.wallet.domain.repository.BalanceRepository;
import org.mojave.core.wallet.domain.repository.PositionRepository;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.ObjectMapper;

public class MonoConsumerDependencies implements MonoConsumerConfiguration.RequiredDependencies {

    private final Ledger ledger;

    private final AccountCache accountCache;

    private final ChartEntryCache chartEntryCache;

    private final FlowDefinitionCache flowDefinitionCache;

    private final BalanceUpdater balanceUpdater;

    private final PositionUpdater positionUpdater;

    private final BalanceCache balanceCache;

    private final PositionCache positionCache;

    public MonoConsumerDependencies(AccountRepository accountRepository,
                                    ChartEntryRepository chartEntryRepository,
                                    FlowDefinitionRepository flowDefinitionRepository,
                                    BalanceRepository balanceRepository,
                                    PositionRepository positionRepository,
                                    ObjectMapper objectMapper) {

        assert accountRepository != null;
        assert chartEntryRepository != null;
        assert flowDefinitionRepository != null;
        assert objectMapper != null;

        this.ledger = new MySqlLedger(
            new MySqlLedger.LedgerDbSettings(
                new MySqlLedger.LedgerDbSettings.Connection(
                    System.getenv("MYSQL_LEDGER_DB_URL"), System.getenv("MYSQL_LEDGER_DB_USER"),
                    System.getenv("MYSQL_LEDGER_DB_PASSWORD"),
                    Long.parseLong(System.getenv("MYSQL_LEDGER_DB_CONNECTION_TIMEOUT")),
                    Long.parseLong(System.getenv("MYSQL_LEDGER_DB_VALIDATION_TIMEOUT")),
                    Long.parseLong(System.getenv("MYSQL_LEDGER_DB_MAX_LIFETIME_TIMEOUT")),
                    Long.parseLong(System.getenv("MYSQL_LEDGER_DB_IDLE_TIMEOUT")),
                    Long.parseLong(System.getenv("MYSQL_LEDGER_DB_KEEPALIVE_TIMEOUT")), false),
                new MySqlLedger.LedgerDbSettings.Pool(
                    "accounting-ledger",
                    Integer.parseInt(System.getenv("MYSQL_LEDGER_DB_MIN_POOL_SIZE")),
                    Integer.parseInt(System.getenv("MYSQL_LEDGER_DB_MAX_POOL_SIZE")))),
            objectMapper);

        this.accountCache = new AccountTimerCache(
            accountRepository, Integer.parseInt(
            System.getenv().getOrDefault("ACCOUNT_TIMER_CACHE_REFRESH_INTERVAL_MS", "5000")));

        this.chartEntryCache = new ChartEntryTimerCache(
            chartEntryRepository, Integer.parseInt(
            System.getenv().getOrDefault("CHART_ENTRY_TIMER_CACHE_REFRESH_INTERVAL_MS", "5000")));

        this.flowDefinitionCache = new FlowDefinitionTimerCache(
            flowDefinitionRepository,
            Integer.parseInt(System
                                 .getenv()
                                 .getOrDefault(
                                     "FLOW_DEFINITION_TIMER_CACHE_REFRESH_INTERVAL_MS", "5000")));

        this.balanceUpdater = new MySqlBalanceUpdater(new MySqlBalanceUpdater.BalanceDbSettings(
            new MySqlBalanceUpdater.BalanceDbSettings.Connection(
                System.getenv("MYSQL_BALANCE_DB_URL"), System.getenv("MYSQL_BALANCE_DB_USER"),
                System.getenv("MYSQL_BALANCE_DB_PASSWORD"),
                Long.parseLong(System.getenv("MYSQL_BALANCE_DB_CONNECTION_TIMEOUT")),
                Long.parseLong(System.getenv("MYSQL_BALANCE_DB_VALIDATION_TIMEOUT")),
                Long.parseLong(System.getenv("MYSQL_BALANCE_DB_MAX_LIFETIME_TIMEOUT")),
                Long.parseLong(System.getenv("MYSQL_BALANCE_DB_IDLE_TIMEOUT")),
                Long.parseLong(System.getenv("MYSQL_BALANCE_DB_KEEPALIVE_TIMEOUT")), false),
            new MySqlBalanceUpdater.BalanceDbSettings.Pool(
                "wallet-balance", Integer.parseInt(System.getenv("MYSQL_BALANCE_DB_MIN_POOL_SIZE")),
                Integer.parseInt(System.getenv("MYSQL_BALANCE_DB_MAX_POOL_SIZE")))));

        this.positionUpdater = new MySqlPositionUpdater(new MySqlPositionUpdater.PositionDbSettings(
            new MySqlPositionUpdater.PositionDbSettings.Connection(
                System.getenv("MYSQL_POSITION_DB_URL"), System.getenv("MYSQL_POSITION_DB_USER"),
                System.getenv("MYSQL_POSITION_DB_PASSWORD"),
                Long.parseLong(System.getenv("MYSQL_POSITION_DB_CONNECTION_TIMEOUT")),
                Long.parseLong(System.getenv("MYSQL_POSITION_DB_VALIDATION_TIMEOUT")),
                Long.parseLong(System.getenv("MYSQL_POSITION_DB_MAX_LIFETIME_TIMEOUT")),
                Long.parseLong(System.getenv("MYSQL_POSITION_DB_IDLE_TIMEOUT")),
                Long.parseLong(System.getenv("MYSQL_POSITION_DB_KEEPALIVE_TIMEOUT")), false),
            new MySqlPositionUpdater.PositionDbSettings.Pool(
                "wallet-position",
                Integer.parseInt(System.getenv("MYSQL_POSITION_DB_MIN_POOL_SIZE")),
                Integer.parseInt(System.getenv("MYSQL_POSITION_DB_MAX_POOL_SIZE")))));

        this.balanceCache = new BalanceTimerCache(
            balanceRepository, Integer.parseInt(
            System.getenv().getOrDefault("WALLET_TIMER_CACHE_REFRESH_INTERVAL_MS", "5000")));

        this.positionCache = new PositionTimerCache(
            positionRepository, Integer.parseInt(
            System.getenv().getOrDefault("POSITION_TIMER_CACHE_REFRESH_INTERVAL_MS", "5000")));

    }

    @Bean
    @Override
    public AccountCache accountCache() {

        return this.accountCache;
    }

    @Bean
    @Override
    public BalanceUpdater balanceUpdater() {

        return this.balanceUpdater;
    }

    @Bean
    @Override
    public ChartEntryCache chartEntryCache() {

        return this.chartEntryCache;
    }

    @Bean
    @Override
    public FlowDefinitionCache flowDefinitionCache() {

        return this.flowDefinitionCache;
    }

    @Bean
    @Override
    public Ledger ledger() {

        return this.ledger;
    }

    @Bean
    @Override
    public PositionCache positionCache() {

        return this.positionCache;
    }

    @Bean
    @Override
    public PositionUpdater positionUpdater() {

        return this.positionUpdater;
    }

    @Bean
    @Override
    public BalanceCache walletCache() {

        return this.balanceCache;
    }

}
