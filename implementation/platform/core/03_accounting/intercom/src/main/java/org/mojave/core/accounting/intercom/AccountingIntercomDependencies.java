package org.mojave.core.accounting.intercom;

import tools.jackson.databind.ObjectMapper;
import org.mojave.component.web.spring.security.AuthenticationErrorWriter;
import org.mojave.component.web.spring.security.Authenticator;
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
import org.mojave.core.accounting.intercom.controller.component.EmptyErrorWriter;
import org.mojave.core.accounting.intercom.controller.component.EmptyGatekeeper;
import org.springframework.context.annotation.Bean;

final class AccountingIntercomDependencies
    implements AccountingIntercomConfiguration.RequiredDependencies {

    private final Ledger ledger;

    private final AccountCache accountCache;

    private final ChartEntryCache chartEntryCache;

    private final FlowDefinitionCache flowDefinitionCache;

    public AccountingIntercomDependencies(AccountRepository accountRepository,
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
                    System.getenv("ACC_MYSQL_LEDGER_DB_URL"),
                    System.getenv("ACC_MYSQL_LEDGER_DB_USER"),
                    System.getenv("ACC_MYSQL_LEDGER_DB_PASSWORD")),
                new MySqlLedger.LedgerDbSettings.Pool(
                    "accounting-ledger",
                    Integer.parseInt(System.getenv("ACC_MYSQL_LEDGER_DB_MIN_POOL_SIZE")),
                    Integer.parseInt(System.getenv("ACC_MYSQL_LEDGER_DB_MAX_POOL_SIZE")))),
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
