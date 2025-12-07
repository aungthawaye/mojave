package io.mojaloop.core.wallet.admin;

import io.mojaloop.component.web.spring.security.AuthenticationErrorWriter;
import io.mojaloop.component.web.spring.security.Authenticator;
import io.mojaloop.core.wallet.admin.controller.component.EmptyErrorWriter;
import io.mojaloop.core.wallet.admin.controller.component.EmptyGatekeeper;
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
import org.springframework.context.annotation.Bean;

final class WalletAdminDependencies implements WalletAdminConfiguration.RequiredDependencies {

    private final BalanceUpdater balanceUpdater;

    private final PositionUpdater positionUpdater;

    private final BalanceCache balanceCache;

    private final PositionCache positionCache;

    public WalletAdminDependencies(BalanceRepository balanceRepository,
                                   PositionRepository positionRepository) {

        assert balanceRepository != null;
        assert positionRepository != null;

        this.balanceUpdater = new MySqlBalanceUpdater(new MySqlBalanceUpdater.BalanceDbSettings(
            new MySqlBalanceUpdater.BalanceDbSettings.Connection(
                System.getenv("WLT_MYSQL_BALANCE_DB_URL"),
                System.getenv("WLT_MYSQL_BALANCE_DB_USER"),
                System.getenv("WLT_MYSQL_BALANCE_DB_PASSWORD")),
            new MySqlBalanceUpdater.BalanceDbSettings.Pool(
                "wallet-balance",
                Integer.parseInt(System.getenv("WLT_MYSQL_BALANCE_DB_MIN_POOL_SIZE")),
                Integer.parseInt(System.getenv("WLT_MYSQL_BALANCE_DB_MAX_POOL_SIZE")))));

        this.positionUpdater = new MySqlPositionUpdater(new MySqlPositionUpdater.PositionDbSettings(
            new MySqlPositionUpdater.PositionDbSettings.Connection(
                System.getenv("WLT_MYSQL_POSITION_DB_URL"),
                System.getenv("WLT_MYSQL_POSITION_DB_USER"),
                System.getenv("WLT_MYSQL_POSITION_DB_PASSWORD")),
            new MySqlPositionUpdater.PositionDbSettings.Pool(
                "wallet-position",
                Integer.parseInt(System.getenv("WLT_MYSQL_POSITION_DB_MIN_POOL_SIZE")),
                Integer.parseInt(System.getenv("WLT_MYSQL_POSITION_DB_MAX_POOL_SIZE")))));

        this.balanceCache = new BalanceLocalCache(balanceRepository);
        this.positionCache = new PositionLocalCache(positionRepository);
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
