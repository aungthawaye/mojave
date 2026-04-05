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

package org.mojave.mono.core.admin;

import org.mojave.component.web.spring.security.AuthenticationErrorWriter;
import org.mojave.component.web.spring.security.Authenticator;
import org.mojave.accounting.domain.cache.AccountCache;
import org.mojave.accounting.domain.cache.CoaEntryCache;
import org.mojave.accounting.domain.cache.FlowDefinitionCache;
import org.mojave.accounting.domain.cache.strategy.local.AccountLocalCache;
import org.mojave.accounting.domain.cache.strategy.local.CoaEntryLocalCache;
import org.mojave.accounting.domain.cache.strategy.local.FlowDefinitionLocalCache;
import org.mojave.accounting.contract.ledger.Ledger;
import org.mojave.accounting.ledger.mysql.MySqlLedger;
import org.mojave.accounting.domain.repository.AccountRepository;
import org.mojave.accounting.domain.repository.CoaEntryRepository;
import org.mojave.accounting.domain.repository.FlowDefinitionRepository;
import org.mojave.mono.core.admin.controller.component.EmptyErrorWriter;
import org.mojave.mono.core.admin.controller.component.EmptyGatekeeper;
import org.mojave.wallet.domain.cache.BalanceCache;
import org.mojave.wallet.domain.cache.PositionCache;
import org.mojave.wallet.domain.cache.strategy.local.BalanceLocalCache;
import org.mojave.wallet.domain.cache.strategy.local.PositionLocalCache;
import org.mojave.wallet.domain.component.BalanceUpdater;
import org.mojave.wallet.domain.component.PositionUpdater;
import org.mojave.wallet.domain.component.mysql.MySqlBalanceUpdater;
import org.mojave.wallet.domain.component.mysql.MySqlPositionUpdater;
import org.mojave.wallet.domain.repository.BalanceRepository;
import org.mojave.wallet.domain.repository.PositionRepository;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

public class MonoAdminDependencies implements MonoAdminConfiguration.RequiredDependencies {

    private final AccountCache accountCache;

    private final CoaEntryCache coaEntryCache;

    private final FlowDefinitionCache flowDefinitionCache;

    private final PositionCache positionCache;

    private final BalanceCache balanceCache;

    private final Ledger ledger;

    private final BalanceUpdater balanceUpdater;

    private final PositionUpdater positionUpdater;

    public MonoAdminDependencies(ObjectMapper objectMapper,
                                 AccountRepository accountRepository,
                                 CoaEntryRepository coaEntryRepository,
                                 FlowDefinitionRepository flowDefinitionRepository,
                                 PositionRepository positionRepository,
                                 BalanceRepository balanceRepository) {

        Objects.requireNonNull(objectMapper);
        Objects.requireNonNull(accountRepository);
        Objects.requireNonNull(coaEntryRepository);
        Objects.requireNonNull(flowDefinitionRepository);
        Objects.requireNonNull(positionRepository);
        Objects.requireNonNull(balanceRepository);

        this.accountCache = new AccountLocalCache(accountRepository);
        this.coaEntryCache = new CoaEntryLocalCache(coaEntryRepository);
        this.flowDefinitionCache = new FlowDefinitionLocalCache(flowDefinitionRepository);
        this.positionCache = new PositionLocalCache(positionRepository);
        this.balanceCache = new BalanceLocalCache(balanceRepository);

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
    }

    @Bean
    @Override
    public AccountCache accountCache() {

        return this.accountCache;
    }

    @Bean
    @Override
    public AuthenticationErrorWriter authenticationErrorWriter() {

        return new EmptyErrorWriter();
    }

    @Bean
    @Override
    public Authenticator authenticator() {

        return new EmptyGatekeeper();
    }

    @Bean
    @Override
    public BalanceUpdater balanceUpdater() {

        return this.balanceUpdater;
    }

    @Bean
    @Override
    public CoaEntryCache coaEntryCache() {

        return this.coaEntryCache;
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
