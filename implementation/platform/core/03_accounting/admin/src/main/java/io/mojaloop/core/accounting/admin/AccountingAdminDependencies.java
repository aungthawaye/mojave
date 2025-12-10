package io.mojaloop.core.accounting.admin;

import tools.jackson.databind.ObjectMapper;
import io.mojaloop.component.web.spring.security.AuthenticationErrorWriter;
import io.mojaloop.component.web.spring.security.Authenticator;
import io.mojaloop.core.accounting.admin.controller.component.EmptyErrorWriter;
import io.mojaloop.core.accounting.admin.controller.component.EmptyGatekeeper;
import io.mojaloop.core.accounting.domain.cache.AccountCache;
import io.mojaloop.core.accounting.domain.cache.ChartEntryCache;
import io.mojaloop.core.accounting.domain.cache.FlowDefinitionCache;
import io.mojaloop.core.accounting.domain.cache.strategy.local.AccountLocalCache;
import io.mojaloop.core.accounting.domain.cache.strategy.local.ChartEntryLocalCache;
import io.mojaloop.core.accounting.domain.cache.strategy.local.FlowDefinitionLocalCache;
import io.mojaloop.core.accounting.domain.component.ledger.Ledger;
import io.mojaloop.core.accounting.domain.component.ledger.strategy.MySqlLedger;
import io.mojaloop.core.accounting.domain.repository.AccountRepository;
import io.mojaloop.core.accounting.domain.repository.ChartEntryRepository;
import io.mojaloop.core.accounting.domain.repository.FlowDefinitionRepository;
import org.springframework.context.annotation.Bean;

final class AccountingAdminDependencies
    implements AccountingAdminConfiguration.RequiredDependencies {

    private final AccountCache accountCache;

    private final ChartEntryCache chartEntryCache;

    private final FlowDefinitionCache flowDefinitionCache;

    private final Ledger ledger;

    public AccountingAdminDependencies(AccountRepository accountRepository,
                                       ChartEntryRepository chartEntryRepository,
                                       FlowDefinitionRepository flowDefinitionRepository,
                                       ObjectMapper objectMapper) {

        assert accountRepository != null;
        assert chartEntryRepository != null;
        assert flowDefinitionRepository != null;
        assert objectMapper != null;

        this.accountCache = new AccountLocalCache(accountRepository);
        this.chartEntryCache = new ChartEntryLocalCache(chartEntryRepository);
        this.flowDefinitionCache = new FlowDefinitionLocalCache(flowDefinitionRepository);

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
