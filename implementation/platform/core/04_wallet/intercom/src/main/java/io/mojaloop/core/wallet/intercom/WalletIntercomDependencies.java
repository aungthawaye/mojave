package io.mojaloop.core.wallet.intercom;

import io.mojaloop.component.web.spring.security.AuthenticationErrorWriter;
import io.mojaloop.component.web.spring.security.Authenticator;
import io.mojaloop.core.wallet.domain.cache.BalanceCache;
import io.mojaloop.core.wallet.domain.cache.PositionCache;
import io.mojaloop.core.wallet.domain.cache.strategy.timer.BalanceTimerCache;
import io.mojaloop.core.wallet.domain.cache.strategy.timer.PositionTimerCache;
import io.mojaloop.core.wallet.domain.component.BalanceUpdater;
import io.mojaloop.core.wallet.domain.component.PositionUpdater;
import io.mojaloop.core.wallet.domain.component.mysql.MySqlBalanceUpdater;
import io.mojaloop.core.wallet.domain.component.mysql.MySqlPositionUpdater;
import io.mojaloop.core.wallet.domain.repository.BalanceRepository;
import io.mojaloop.core.wallet.domain.repository.PositionRepository;
import io.mojaloop.core.wallet.intercom.controller.component.EmptyErrorWriter;
import io.mojaloop.core.wallet.intercom.controller.component.EmptyGatekeeper;
import org.springframework.context.annotation.Bean;

final class WalletIntercomDependencies implements WalletIntercomConfiguration.RequiredDependencies {

    private final BalanceUpdater balanceUpdater;

    private final PositionUpdater positionUpdater;

    private final BalanceCache balanceCache;

    private final PositionCache positionCache;

    public WalletIntercomDependencies(BalanceRepository balanceRepository,
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

        this.balanceCache = new BalanceTimerCache(
            balanceRepository, Integer.parseInt(
            System.getenv().getOrDefault("WALLET_TIMER_CACHE_REFRESH_INTERVAL_MS", "5000")));

        this.positionCache = new PositionTimerCache(
            positionRepository, Integer.parseInt(
            System.getenv().getOrDefault("POSITION_TIMER_CACHE_REFRESH_INTERVAL_MS", "5000")));
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
