package org.mojave.core.wallet.domain;

import org.mojave.component.misc.handy.EnvOrProperty;
import org.mojave.core.wallet.contract.engine.WalletEngine;
import org.mojave.core.wallet.engine.mysql.MySqlWalletEngine;
import org.springframework.context.annotation.Bean;

public class WalletDomainDependencies implements WalletDomainConfiguration.RequiredDependencies {

    private static final String MYSQL_WALLET_DB_URL = "MYSQL_WALLET_DB_URL";

    private static final String MYSQL_WALLET_DB_USER = "MYSQL_WALLET_DB_USER";

    private static final String MYSQL_WALLET_DB_PASSWORD = "MYSQL_WALLET_DB_PASSWORD";

    private static final String MYSQL_WALLET_DB_CONNECTION_TIMEOUT = "MYSQL_WALLET_DB_CONNECTION_TIMEOUT";

    private static final String MYSQL_WALLET_DB_VALIDATION_TIMEOUT = "MYSQL_WALLET_DB_VALIDATION_TIMEOUT";

    private static final String MYSQL_WALLET_DB_MAX_LIFETIME_TIMEOUT = "MYSQL_WALLET_DB_MAX_LIFETIME_TIMEOUT";

    private static final String MYSQL_WALLET_DB_IDLE_TIMEOUT = "MYSQL_WALLET_DB_IDLE_TIMEOUT";

    private static final String MYSQL_WALLET_DB_KEEPALIVE_TIMEOUT = "MYSQL_WALLET_DB_KEEPALIVE_TIMEOUT";

    private static final String MYSQL_WALLET_DB_MIN_POOL_SIZE = "MYSQL_WALLET_DB_MIN_POOL_SIZE";

    private static final String MYSQL_WALLET_DB_MAX_POOL_SIZE = "MYSQL_WALLET_DB_MAX_POOL_SIZE";

    @Bean
    @Override
    public WalletEngine walletEngine() {

        return new MySqlWalletEngine(new MySqlWalletEngine.WalletDbSettings(
            new MySqlWalletEngine.WalletDbSettings.Connection(
                EnvOrProperty.get(MYSQL_WALLET_DB_URL), EnvOrProperty.get(MYSQL_WALLET_DB_USER),
                EnvOrProperty.get(MYSQL_WALLET_DB_PASSWORD),
                Long.parseLong(EnvOrProperty.get(MYSQL_WALLET_DB_CONNECTION_TIMEOUT)),
                Long.parseLong(EnvOrProperty.get(MYSQL_WALLET_DB_VALIDATION_TIMEOUT)),
                Long.parseLong(EnvOrProperty.get(MYSQL_WALLET_DB_MAX_LIFETIME_TIMEOUT)),
                Long.parseLong(EnvOrProperty.get(MYSQL_WALLET_DB_IDLE_TIMEOUT)),
                Long.parseLong(EnvOrProperty.get(MYSQL_WALLET_DB_KEEPALIVE_TIMEOUT)), false),
            new MySqlWalletEngine.WalletDbSettings.Pool(
                "accounting-ledgerOperation",
                Integer.parseInt(EnvOrProperty.get(MYSQL_WALLET_DB_MIN_POOL_SIZE)),
                Integer.parseInt(EnvOrProperty.get(MYSQL_WALLET_DB_MAX_POOL_SIZE)))));
    }

}
