package io.mojaloop.mono.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.openapi.OpenApiConfiguration;
import io.mojaloop.component.web.error.RestErrorConfiguration;
import io.mojaloop.component.web.spring.mvc.WebMvcExtension;
import io.mojaloop.component.web.spring.security.AuthenticationErrorWriter;
import io.mojaloop.component.web.spring.security.Authenticator;
import io.mojaloop.component.web.spring.security.SpringSecurityConfiguration;
import io.mojaloop.component.web.spring.security.SpringSecurityConfigurer;
import io.mojaloop.core.accounting.domain.AccountingDomainConfiguration;
import io.mojaloop.core.accounting.domain.cache.AccountCache;
import io.mojaloop.core.accounting.domain.cache.ChartEntryCache;
import io.mojaloop.core.accounting.domain.cache.FlowDefinitionCache;
import io.mojaloop.core.accounting.domain.cache.strategy.local.AccountLocalCache;
import io.mojaloop.core.accounting.domain.cache.strategy.local.ChartEntryLocalCache;
import io.mojaloop.core.accounting.domain.cache.strategy.local.FlowDefinitionLocalCache;
import io.mojaloop.core.accounting.domain.component.ledger.Ledger;
import io.mojaloop.core.accounting.domain.component.ledger.strategy.MySqlLedger;
import io.mojaloop.core.accounting.domain.component.resolver.AccountResolver;
import io.mojaloop.core.accounting.domain.component.resolver.strategy.CacheBasedAccountResolver;
import io.mojaloop.core.accounting.domain.repository.AccountRepository;
import io.mojaloop.core.accounting.domain.repository.ChartEntryRepository;
import io.mojaloop.core.accounting.domain.repository.FlowDefinitionRepository;
import io.mojaloop.core.common.datatype.DatatypeConfiguration;
import io.mojaloop.core.participant.domain.ParticipantDomainConfiguration;
import io.mojaloop.core.wallet.domain.WalletDomainConfiguration;
import io.mojaloop.core.wallet.domain.cache.PositionCache;
import io.mojaloop.core.wallet.domain.cache.WalletCache;
import io.mojaloop.core.wallet.domain.cache.strategy.local.PositionLocalCache;
import io.mojaloop.core.wallet.domain.cache.strategy.local.WalletLocalCache;
import io.mojaloop.core.wallet.domain.component.BalanceUpdater;
import io.mojaloop.core.wallet.domain.component.PositionUpdater;
import io.mojaloop.core.wallet.domain.component.mysql.MySqlBalanceUpdater;
import io.mojaloop.core.wallet.domain.component.mysql.MySqlPositionUpdater;
import io.mojaloop.core.wallet.domain.repository.PositionRepository;
import io.mojaloop.core.wallet.domain.repository.WalletRepository;
import io.mojaloop.mono.admin.controller.component.EmptyErrorWriter;
import io.mojaloop.mono.admin.controller.component.EmptyGatekeeper;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@EnableAsync
@ComponentScan(basePackages = {"io.mojaloop.mono.admin.controller",
                               "io.mojaloop.core.participant.admin.controller.api",
                               "io.mojaloop.core.accounting.admin.controller.api",
                               "io.mojaloop.core.wallet.admin.controller.api"},
               excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                                                       classes = {io.mojaloop.core.participant.admin.controller.api.WelcomeController.class,
                                                                  io.mojaloop.core.accounting.admin.controller.api.WelcomeController.class,
                                                                  io.mojaloop.core.wallet.admin.controller.api.WelcomeController.class})})
@Import(value = {OpenApiConfiguration.class,
                 DatatypeConfiguration.class,
                 RestErrorConfiguration.class,
                 SpringSecurityConfiguration.class,
                 ParticipantDomainConfiguration.class,
                 AccountingDomainConfiguration.class,
                 WalletDomainConfiguration.class})
public class MonoAdminConfiguration extends WebMvcExtension implements
                                                                   SpringSecurityConfiguration.RequiredBeans,
                                                                   ParticipantDomainConfiguration.RequiredBeans,
                                                                   AccountingDomainConfiguration.RequiredBeans,
                                                                   WalletDomainConfiguration.RequiredBeans,
                                                                   SpringSecurityConfiguration.RequiredSettings {

    private final AccountCache accountCache;

    private final ChartEntryCache chartEntryCache;

    private final FlowDefinitionCache flowDefinitionCache;

    private final PositionCache positionCache;

    private final WalletCache walletCache;

    private final AccountResolver accountResolver;

    private final Ledger ledger;

    private final BalanceUpdater balanceUpdater;

    private final PositionUpdater positionUpdater;

    public MonoAdminConfiguration(ObjectMapper objectMapper,
                                  AccountRepository accountRepository,
                                  ChartEntryRepository chartEntryRepository,
                                  FlowDefinitionRepository flowDefinitionRepository,
                                  PositionRepository positionRepository,
                                  WalletRepository walletRepository,
                                  MySqlLedger.LedgerDbSettings ledgerDbSettings,
                                  MySqlBalanceUpdater.BalanceDbSettings balanceDbSettings,
                                  MySqlPositionUpdater.PositionDbSettings positionDbSettings) {

        super(objectMapper);

        assert accountRepository != null;
        assert chartEntryRepository != null;
        assert flowDefinitionRepository != null;
        assert positionRepository != null;
        assert walletRepository != null;
        assert ledgerDbSettings != null;

        this.accountCache = new AccountLocalCache(accountRepository);
        this.chartEntryCache = new ChartEntryLocalCache(chartEntryRepository);
        this.flowDefinitionCache = new FlowDefinitionLocalCache(flowDefinitionRepository);
        this.positionCache = new PositionLocalCache(positionRepository);
        this.walletCache = new WalletLocalCache(walletRepository);

        this.accountResolver = new CacheBasedAccountResolver(this.accountCache);

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
    public AccountResolver accountResolver() {

        return this.accountResolver;
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
    public SpringSecurityConfigurer.Settings springSecuritySettings() {

        return new SpringSecurityConfigurer.Settings(null);
    }

    @Bean
    @Override
    public WalletCache walletCache() {

        return this.walletCache;
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer(
        TomcatSettings settings) {

        return factory -> factory.setPort(settings.portNo());
    }

    public interface RequiredSettings extends OpenApiConfiguration.RequiredSettings,
                                              ParticipantDomainConfiguration.RequiredSettings,
                                              AccountingDomainConfiguration.RequiredSettings,
                                              WalletDomainConfiguration.RequiredSettings {

        MySqlBalanceUpdater.BalanceDbSettings balanceDbSettings();

        MySqlLedger.LedgerDbSettings ledgerDbSettings();

        MySqlPositionUpdater.PositionDbSettings positionDbSettings();

        TomcatSettings tomcatSettings();

    }

    public record TomcatSettings(int portNo) { }

}
