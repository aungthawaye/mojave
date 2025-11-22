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

package io.mojaloop.core.accounting.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.flyway.FlywayMigration;
import io.mojaloop.core.accounting.domain.cache.AccountCache;
import io.mojaloop.core.accounting.domain.cache.ChartEntryCache;
import io.mojaloop.core.accounting.domain.cache.FlowDefinitionCache;
import io.mojaloop.core.accounting.domain.cache.strategy.timer.AccountTimerCache;
import io.mojaloop.core.accounting.domain.cache.strategy.timer.ChartEntryTimerCache;
import io.mojaloop.core.accounting.domain.cache.strategy.timer.FlowDefinitionTimerCache;
import io.mojaloop.core.accounting.domain.component.ledger.Ledger;
import io.mojaloop.core.accounting.domain.component.ledger.strategy.MySqlLedger;
import io.mojaloop.core.accounting.domain.component.resolver.AccountResolver;
import io.mojaloop.core.accounting.domain.component.resolver.strategy.CacheBasedAccountResolver;
import io.mojaloop.core.accounting.domain.repository.AccountRepository;
import io.mojaloop.core.accounting.domain.repository.ChartEntryRepository;
import io.mojaloop.core.accounting.domain.repository.FlowDefinitionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(value = {AccountingDomainConfiguration.class, TestSettings.class})
public class TestConfiguration implements AccountingDomainConfiguration.RequiredBeans {

    static {

        var flywaySettings = new FlywayMigration.Settings(
            "jdbc:mysql://localhost:3306/ml_accounting?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true", "root", "password",
            "classpath:migration/accounting");

        FlywayMigration.migrate(flywaySettings);
    }

    private final Ledger ledger;

    private final AccountCache accountCache;

    private final AccountResolver accountResolver;

    private final ChartEntryCache chartEntryCache;

    private final FlowDefinitionCache flowDefinitionCache;

    public TestConfiguration(MySqlLedger.LedgerDbSettings ledgerDbSettings,
                             AccountRepository accountRepository,
                             ChartEntryRepository chartEntryRepository,
                             FlowDefinitionRepository flowDefinitionRepository,
                             ObjectMapper objectMapper) {

        this.ledger = new MySqlLedger(ledgerDbSettings, objectMapper);

        this.accountCache = new AccountTimerCache(accountRepository, Integer.parseInt(System.getenv().getOrDefault("ACCOUNT_TIMER_CACHE_REFRESH_INTERVAL_MS", "5000")));

        this.accountResolver = new CacheBasedAccountResolver(this.accountCache);

        this.chartEntryCache = new ChartEntryTimerCache(chartEntryRepository,
            Integer.parseInt(System.getenv().getOrDefault("CHART_ENTRY_TIMER_CACHE_REFRESH_INTERVAL_MS", "5000")));

        this.flowDefinitionCache = new FlowDefinitionTimerCache(flowDefinitionRepository,
            Integer.parseInt(System.getenv().getOrDefault("FLOW_DEFINITION_TIMER_CACHE_REFRESH_INTERVAL_MS", "5000")));
    }

    @Bean
    @Override
    public AccountCache accountCache() {

        return this.accountCache;
    }

    @Bean
    @Override
    public AccountResolver accountResolver() {

        return this.accountResolver;
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
