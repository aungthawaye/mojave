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
package org.mojave.core.accounting.consumer;

import org.mojave.core.accounting.domain.cache.AccountCache;
import org.mojave.core.accounting.domain.cache.ChartEntryCache;
import org.mojave.core.accounting.domain.cache.FlowDefinitionCache;
import org.mojave.core.accounting.domain.cache.strategy.local.AccountLocalCache;
import org.mojave.core.accounting.domain.cache.strategy.local.ChartEntryLocalCache;
import org.mojave.core.accounting.domain.cache.strategy.local.FlowDefinitionLocalCache;
import org.mojave.core.accounting.domain.component.ledger.Ledger;
import org.mojave.core.accounting.domain.component.ledger.strategy.MySqlLedger;
import org.mojave.core.accounting.domain.repository.AccountRepository;
import org.mojave.core.accounting.domain.repository.ChartEntryRepository;
import org.mojave.core.accounting.domain.repository.FlowDefinitionRepository;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.ObjectMapper;

class AccountingConsumerDependencies
    implements AccountingConsumerConfiguration.RequiredDependencies {

    private final Ledger ledger;

    private final AccountCache accountCache;

    private final ChartEntryCache chartEntryCache;

    private final FlowDefinitionCache flowDefinitionCache;

    public AccountingConsumerDependencies(AccountRepository accountRepository,
                                          ChartEntryRepository chartEntryRepository,
                                          FlowDefinitionRepository flowDefinitionRepository,
                                          ObjectMapper objectMapper) {

        assert accountRepository != null;
        assert chartEntryRepository != null;
        assert flowDefinitionRepository != null;
        assert objectMapper != null;

        this.ledger = new MySqlLedger(
            new MySqlLedger.LedgerDbSettings(
                new MySqlLedger.LedgerDbSettings.Connection(
                    System.getenv("MYSQL_LEDGER_DB_URL"),
                    System.getenv("MYSQL_LEDGER_DB_USER"),
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

        this.accountCache = new AccountLocalCache(accountRepository);

        this.chartEntryCache = new ChartEntryLocalCache(chartEntryRepository);

        this.flowDefinitionCache = new FlowDefinitionLocalCache(flowDefinitionRepository);

    }

    @Bean
    @Override
    public AccountCache accountCache() {

        return this.accountCache;
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

}
