package org.mojave.core.accounting.consumer;

import tools.jackson.databind.ObjectMapper;
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
                    System.getenv("ACC_LEDGER_DB_URL"),
                    System.getenv("ACC_LEDGER_DB_USER"), System.getenv("ACC_LEDGER_DB_PASSWORD")),
                new MySqlLedger.LedgerDbSettings.Pool(
                    "accounting-ledger",
                    Integer.parseInt(System.getenv("ACC_LEDGER_DB_MIN_POOL_SIZE")),
                    Integer.parseInt(System.getenv("ACC_LEDGER_DB_MAX_POOL_SIZE")))), objectMapper);

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
