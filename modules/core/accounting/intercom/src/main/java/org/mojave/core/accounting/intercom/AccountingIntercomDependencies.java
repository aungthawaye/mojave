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

package org.mojave.core.accounting.intercom;

import org.mojave.component.web.spring.security.AuthenticationErrorWriter;
import org.mojave.component.web.spring.security.Authenticator;
import org.mojave.core.accounting.domain.cache.AccountCache;
import org.mojave.core.accounting.domain.cache.CoaEntryCache;
import org.mojave.core.accounting.domain.cache.FlowDefinitionCache;
import org.mojave.core.accounting.domain.cache.strategy.timer.AccountTimerCache;
import org.mojave.core.accounting.domain.cache.strategy.timer.CoaEntryTimerCache;
import org.mojave.core.accounting.domain.cache.strategy.timer.FlowDefinitionTimerCache;
import org.mojave.core.accounting.domain.repository.AccountRepository;
import org.mojave.core.accounting.domain.repository.CoaEntryRepository;
import org.mojave.core.accounting.domain.repository.FlowDefinitionRepository;
import org.mojave.core.accounting.intercom.controller.component.EmptyErrorWriter;
import org.mojave.core.accounting.intercom.controller.component.EmptyGatekeeper;
import org.mojave.provider.ledger.contract.Ledger;
import org.mojave.provider.ledger.mysql.MySqlLedger;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

final class AccountingIntercomDependencies
    implements AccountingIntercomConfiguration.RequiredDependencies {

    private final Ledger ledger;

    private final AccountCache accountCache;

    private final CoaEntryCache coaEntryCache;

    private final FlowDefinitionCache flowDefinitionCache;

    public AccountingIntercomDependencies(AccountRepository accountRepository,
                                          CoaEntryRepository coaEntryRepository,
                                          FlowDefinitionRepository flowDefinitionRepository,
                                          ObjectMapper objectMapper) {

        Objects.requireNonNull(accountRepository);
        Objects.requireNonNull(coaEntryRepository);
        Objects.requireNonNull(flowDefinitionRepository);
        Objects.requireNonNull(objectMapper);

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

        this.coaEntryCache = new CoaEntryTimerCache(
            coaEntryRepository, Integer.parseInt(
            System.getenv().getOrDefault("CHART_ENTRY_TIMER_CACHE_REFRESH_INTERVAL_MS", "5000")));

        this.flowDefinitionCache = new FlowDefinitionTimerCache(
            flowDefinitionRepository,
            Integer.parseInt(System
                                 .getenv()
                                 .getOrDefault(
                                     "FLOW_DEFINITION_TIMER_CACHE_REFRESH_INTERVAL_MS", "5000")));

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

}
