package org.mojave.wallet.intercom.consumer;

import org.mojave.wallet.contract.engine.WalletEngine;
import org.mojave.wallet.domain.cache.WalletCache;
import org.mojave.wallet.domain.cache.strategy.timer.WalletTimerCache;
import org.mojave.wallet.domain.repository.WalletRepository;
import org.mojave.wallet.engine.mysql.MySqlWalletEngine;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

public class WalletIntercomConsumerDependencies
    implements WalletIntercomConsumerConfiguration.RequiredDependencies {

    private final WalletEngine walletEngine;

    public WalletIntercomConsumerDependencies(final WalletRepository walletRepository,
                                              final ObjectMapper objectMapper) {

        Objects.requireNonNull(walletRepository);
        Objects.requireNonNull(objectMapper);

        this.walletEngine = new MySqlWalletEngine(
            new MySqlWalletEngine.WalletDbSettings(
                new MySqlWalletEngine.WalletDbSettings.Connection(
                    System.getenv("MYSQL_WALLET_DB_URL"),
                    System.getenv("MYSQL_WALLET_DB_USER"),
                    System.getenv("MYSQL_WALLET_DB_PASSWORD"),
                    Long.parseLong(System.getenv("MYSQL_WALLET_DB_CONNECTION_TIMEOUT")),
                    Long.parseLong(System.getenv("MYSQL_WALLET_DB_VALIDATION_TIMEOUT")),
                    Long.parseLong(System.getenv("MYSQL_WALLET_DB_MAX_LIFETIME_TIMEOUT")),
                    Long.parseLong(System.getenv("MYSQL_WALLET_DB_IDLE_TIMEOUT")),
                    Long.parseLong(System.getenv("MYSQL_WALLET_DB_KEEPALIVE_TIMEOUT")), false),
                new MySqlWalletEngine.WalletDbSettings.Pool(
                    "wallet-engine",
                    Integer.parseInt(System.getenv("MYSQL_WALLET_DB_MIN_POOL_SIZE")),
                    Integer.parseInt(System.getenv("MYSQL_WALLET_DB_MAX_POOL_SIZE")))));

    }

    @Bean
    @Override
    public WalletEngine walletEngine() {

        return this.walletEngine;
    }
}
