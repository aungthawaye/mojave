package org.mojave.accounting.intercom.replier;

import org.mojave.accounting.contract.engine.LedgerEngine;
import org.mojave.accounting.domain.cache.AccountCache;
import org.mojave.accounting.domain.cache.CoaEntryCache;
import org.mojave.accounting.domain.cache.FlowDefinitionCache;
import org.mojave.accounting.domain.cache.strategy.timer.AccountTimerCache;
import org.mojave.accounting.domain.cache.strategy.timer.CoaEntryTimerCache;
import org.mojave.accounting.domain.cache.strategy.timer.FlowDefinitionTimerCache;
import org.mojave.accounting.domain.repository.AccountRepository;
import org.mojave.accounting.domain.repository.CoaEntryRepository;
import org.mojave.accounting.domain.repository.FlowDefinitionRepository;
import org.mojave.accounting.engine.mysql.MySqlLedgerEngine;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

public class AccountingIntercomReplierDependencies
    implements AccountingIntercomReplierConfiguration.RequiredDependencies {

    private final LedgerEngine ledgerEngine;

    private final AccountCache accountCache;

    private final CoaEntryCache coaEntryCache;

    private final FlowDefinitionCache flowDefinitionCache;

    public AccountingIntercomReplierDependencies(final AccountRepository accountRepository,
                                                 final CoaEntryRepository coaEntryRepository,
                                                 final FlowDefinitionRepository flowDefinitionRepository,
                                                 final ObjectMapper objectMapper) {

        Objects.requireNonNull(accountRepository);
        Objects.requireNonNull(coaEntryRepository);
        Objects.requireNonNull(flowDefinitionRepository);
        Objects.requireNonNull(objectMapper);

        this.ledgerEngine = new MySqlLedgerEngine(
            new MySqlLedgerEngine.LedgerDbSettings(
                new MySqlLedgerEngine.LedgerDbSettings.Connection(
                    System.getenv("MYSQL_LEDGER_DB_URL"), System.getenv("MYSQL_LEDGER_DB_USER"),
                    System.getenv("MYSQL_LEDGER_DB_PASSWORD"),
                    Long.parseLong(System.getenv("MYSQL_LEDGER_DB_CONNECTION_TIMEOUT")),
                    Long.parseLong(System.getenv("MYSQL_LEDGER_DB_VALIDATION_TIMEOUT")),
                    Long.parseLong(System.getenv("MYSQL_LEDGER_DB_MAX_LIFETIME_TIMEOUT")),
                    Long.parseLong(System.getenv("MYSQL_LEDGER_DB_IDLE_TIMEOUT")),
                    Long.parseLong(System.getenv("MYSQL_LEDGER_DB_KEEPALIVE_TIMEOUT")), false),
                new MySqlLedgerEngine.LedgerDbSettings.Pool(
                    "accounting-ledgerOperation",
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
            flowDefinitionRepository, Integer.parseInt(System.getenv()
                                                             .getOrDefault(
                                                                 "FLOW_DEFINITION_TIMER_CACHE_REFRESH_INTERVAL_MS",
                                                                 "5000")));
    }

    @Bean
    public AccountCache accountCache() {

        return this.accountCache;
    }

    @Bean
    public CoaEntryCache coaEntryCache() {

        return this.coaEntryCache;
    }

    @Bean
    public FlowDefinitionCache flowDefinitionCache() {

        return this.flowDefinitionCache;
    }

    @Bean
    @Override
    public LedgerEngine ledgerEngine() {

        return this.ledgerEngine;
    }

}
