package org.mojave.core.wallet.domain;

import org.mojave.component.jpa.routing.RoutingDataSourceConfigurer;
import org.mojave.component.jpa.routing.RoutingEntityManagerConfigurer;
import org.mojave.component.misc.handy.EnvOrProperty;
import org.springframework.context.annotation.Bean;

public class WalletDomainSettings implements WalletDomainConfiguration.RequiredSettings {

    private static final String READ_DB_URL = "READ_DB_URL";

    private static final String READ_DB_USER = "READ_DB_USER";

    private static final String READ_DB_PASSWORD = "READ_DB_PASSWORD";

    private static final String READ_DB_CONNECTION_TIMEOUT = "READ_DB_CONNECTION_TIMEOUT";

    private static final String READ_DB_VALIDATION_TIMEOUT = "READ_DB_VALIDATION_TIMEOUT";

    private static final String READ_DB_MAX_LIFETIME_TIMEOUT = "READ_DB_MAX_LIFETIME_TIMEOUT";

    private static final String READ_DB_IDLE_TIMEOUT = "READ_DB_IDLE_TIMEOUT";

    private static final String READ_DB_KEEPALIVE_TIMEOUT = "READ_DB_KEEPALIVE_TIMEOUT";

    private static final String READ_DB_MIN_POOL_SIZE = "READ_DB_MIN_POOL_SIZE";

    private static final String READ_DB_MAX_POOL_SIZE = "READ_DB_MAX_POOL_SIZE";

    private static final String WRITE_DB_URL = "WRITE_DB_URL";

    private static final String WRITE_DB_USER = "WRITE_DB_USER";

    private static final String WRITE_DB_PASSWORD = "WRITE_DB_PASSWORD";

    private static final String WRITE_DB_CONNECTION_TIMEOUT = "WRITE_DB_CONNECTION_TIMEOUT";

    private static final String WRITE_DB_VALIDATION_TIMEOUT = "WRITE_DB_VALIDATION_TIMEOUT";

    private static final String WRITE_DB_MAX_LIFETIME_TIMEOUT = "WRITE_DB_MAX_LIFETIME_TIMEOUT";

    private static final String WRITE_DB_IDLE_TIMEOUT = "WRITE_DB_IDLE_TIMEOUT";

    private static final String WRITE_DB_KEEPALIVE_TIMEOUT = "WRITE_DB_KEEPALIVE_TIMEOUT";

    private static final String WRITE_DB_MIN_POOL_SIZE = "WRITE_DB_MIN_POOL_SIZE";

    private static final String WRITE_DB_MAX_POOL_SIZE = "WRITE_DB_MAX_POOL_SIZE";

    @Bean
    @Override
    public RoutingDataSourceConfigurer.ReadSettings routingDataSourceReadSettings() {

        final var connection = new RoutingDataSourceConfigurer.ReadSettings.Connection(
            EnvOrProperty.get(READ_DB_URL), EnvOrProperty.get(READ_DB_USER),
            EnvOrProperty.get(READ_DB_PASSWORD),
            Long.parseLong(EnvOrProperty.get(READ_DB_CONNECTION_TIMEOUT)),
            Long.parseLong(EnvOrProperty.get(READ_DB_VALIDATION_TIMEOUT)),
            Long.parseLong(EnvOrProperty.get(READ_DB_MAX_LIFETIME_TIMEOUT)),
            Long.parseLong(EnvOrProperty.get(READ_DB_IDLE_TIMEOUT)),
            Long.parseLong(EnvOrProperty.get(READ_DB_KEEPALIVE_TIMEOUT)), false);

        final var pool = new RoutingDataSourceConfigurer.ReadSettings.Pool(
            "wallet-domain-read", Integer.parseInt(EnvOrProperty.get(READ_DB_MIN_POOL_SIZE)),
            Integer.parseInt(EnvOrProperty.get(READ_DB_MAX_POOL_SIZE)));

        return new RoutingDataSourceConfigurer.ReadSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.WriteSettings routingDataSourceWriteSettings() {

        final var connection = new RoutingDataSourceConfigurer.WriteSettings.Connection(
            EnvOrProperty.get(WRITE_DB_URL), EnvOrProperty.get(WRITE_DB_USER),
            EnvOrProperty.get(WRITE_DB_PASSWORD),
            Long.parseLong(EnvOrProperty.get(WRITE_DB_CONNECTION_TIMEOUT)),
            Long.parseLong(EnvOrProperty.get(WRITE_DB_VALIDATION_TIMEOUT)),
            Long.parseLong(EnvOrProperty.get(WRITE_DB_MAX_LIFETIME_TIMEOUT)),
            Long.parseLong(EnvOrProperty.get(WRITE_DB_IDLE_TIMEOUT)),
            Long.parseLong(EnvOrProperty.get(WRITE_DB_KEEPALIVE_TIMEOUT)), false);

        final var pool = new RoutingDataSourceConfigurer.WriteSettings.Pool(
            "wallet-domain-write", Integer.parseInt(EnvOrProperty.get(WRITE_DB_MIN_POOL_SIZE)),
            Integer.parseInt(EnvOrProperty.get(WRITE_DB_MAX_POOL_SIZE)));

        return new RoutingDataSourceConfigurer.WriteSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingEntityManagerConfigurer.Settings routingEntityManagerSettings() {

        return new RoutingEntityManagerConfigurer.Settings("wallet-domain", false, false);
    }

}
