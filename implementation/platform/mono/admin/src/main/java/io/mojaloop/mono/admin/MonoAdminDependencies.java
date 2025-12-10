package io.mojaloop.mono.admin;

import tools.jackson.databind.ObjectMapper;
import io.mojaloop.component.web.spring.security.AuthenticationErrorWriter;
import io.mojaloop.component.web.spring.security.Authenticator;
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
import io.mojaloop.core.wallet.domain.cache.BalanceCache;
import io.mojaloop.core.wallet.domain.cache.PositionCache;
import io.mojaloop.core.wallet.domain.cache.strategy.local.BalanceLocalCache;
import io.mojaloop.core.wallet.domain.cache.strategy.local.PositionLocalCache;
import io.mojaloop.core.wallet.domain.component.BalanceUpdater;
import io.mojaloop.core.wallet.domain.component.PositionUpdater;
import io.mojaloop.core.wallet.domain.component.mysql.MySqlBalanceUpdater;
import io.mojaloop.core.wallet.domain.component.mysql.MySqlPositionUpdater;
import io.mojaloop.core.wallet.domain.repository.BalanceRepository;
import io.mojaloop.core.wallet.domain.repository.PositionRepository;
import io.mojaloop.mono.admin.controller.component.EmptyErrorWriter;
import io.mojaloop.mono.admin.controller.component.EmptyGatekeeper;
import org.springframework.context.annotation.Bean;

public class MonoAdminDependencies implements MonoAdminConfiguration.RequiredDependencies {

    private final AccountCache accountCache;

    private final ChartEntryCache chartEntryCache;

    private final FlowDefinitionCache flowDefinitionCache;

    private final PositionCache positionCache;

    private final BalanceCache balanceCache;

    private final Ledger ledger;

    private final BalanceUpdater balanceUpdater;

    private final PositionUpdater positionUpdater;

    public MonoAdminDependencies(ObjectMapper objectMapper,
                                 AccountRepository accountRepository,
                                 ChartEntryRepository chartEntryRepository,
                                 FlowDefinitionRepository flowDefinitionRepository,
                                 PositionRepository positionRepository,
                                 BalanceRepository balanceRepository,
                                 MySqlLedger.LedgerDbSettings ledgerDbSettings,
                                 MySqlBalanceUpdater.BalanceDbSettings balanceDbSettings,
                                 MySqlPositionUpdater.PositionDbSettings positionDbSettings) {

        assert objectMapper != null;
        assert accountRepository != null;
        assert chartEntryRepository != null;
        assert flowDefinitionRepository != null;
        assert positionRepository != null;
        assert balanceRepository != null;
        assert ledgerDbSettings != null;

        this.accountCache = new AccountLocalCache(accountRepository);
        this.chartEntryCache = new ChartEntryLocalCache(chartEntryRepository);
        this.flowDefinitionCache = new FlowDefinitionLocalCache(flowDefinitionRepository);
        this.positionCache = new PositionLocalCache(positionRepository);
        this.balanceCache = new BalanceLocalCache(balanceRepository);

        this.ledger = new MySqlLedger(ledgerDbSettings, objectMapper);

        this.balanceUpdater = new MySqlBalanceUpdater(balanceDbSettings);

        this.positionUpdater = new MySqlPositionUpdater(positionDbSettings);
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
