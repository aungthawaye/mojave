package org.mojave.accounting.domain;

import org.mojave.component.jpa.routing.RoutingDataSourceConfigurer;
import org.mojave.component.jpa.routing.RoutingEntityManagerConfigurer;
import org.springframework.context.annotation.Bean;

public class AccountingDomainTestSettings implements AccountingDomainConfiguration.RequiredSettings {

    private static final String READ_DB_URL = "jdbc:mysql://localhost:3306/mv_mojave?createDatabaseIfNotExist=true";

    private static final String READ_DB_USER = "root";

    private static final String READ_DB_PASSWORD = "password";

    private static final long READ_DB_CONNECTION_TIMEOUT = 30000L;

    private static final long READ_DB_VALIDATION_TIMEOUT = 5000L;

    private static final long READ_DB_MAX_LIFETIME_TIMEOUT = 1800000L;

    private static final long READ_DB_IDLE_TIMEOUT = 600000L;

    private static final long READ_DB_KEEPALIVE_TIMEOUT = 300000L;

    private static final int READ_DB_MIN_POOL_SIZE = 2;

    private static final int READ_DB_MAX_POOL_SIZE = 2;

    private static final String WRITE_DB_URL = "jdbc:mysql://localhost:3306/mv_mojave?createDatabaseIfNotExist=true";

    private static final String WRITE_DB_USER = "root";

    private static final String WRITE_DB_PASSWORD = "password";

    private static final long WRITE_DB_CONNECTION_TIMEOUT = 30000L;

    private static final long WRITE_DB_VALIDATION_TIMEOUT = 5000L;

    private static final long WRITE_DB_MAX_LIFETIME_TIMEOUT = 1800000L;

    private static final long WRITE_DB_IDLE_TIMEOUT = 600000L;

    private static final long WRITE_DB_KEEPALIVE_TIMEOUT = 300000L;

    private static final int WRITE_DB_MIN_POOL_SIZE = 2;

    private static final int WRITE_DB_MAX_POOL_SIZE = 2;

    @Bean
    @Override
    public RoutingDataSourceConfigurer.ReadSettings routingDataSourceReadSettings() {

        final var connection = new RoutingDataSourceConfigurer.ReadSettings.Connection(
            READ_DB_URL, READ_DB_USER, READ_DB_PASSWORD, READ_DB_CONNECTION_TIMEOUT,
            READ_DB_VALIDATION_TIMEOUT, READ_DB_MAX_LIFETIME_TIMEOUT, READ_DB_IDLE_TIMEOUT,
            READ_DB_KEEPALIVE_TIMEOUT, false);

        final var pool = new RoutingDataSourceConfigurer.ReadSettings.Pool(
            "mojave-accounting-read", READ_DB_MIN_POOL_SIZE, READ_DB_MAX_POOL_SIZE);

        return new RoutingDataSourceConfigurer.ReadSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.WriteSettings routingDataSourceWriteSettings() {

        final var connection = new RoutingDataSourceConfigurer.WriteSettings.Connection(
            WRITE_DB_URL, WRITE_DB_USER, WRITE_DB_PASSWORD, WRITE_DB_CONNECTION_TIMEOUT,
            WRITE_DB_VALIDATION_TIMEOUT, WRITE_DB_MAX_LIFETIME_TIMEOUT, WRITE_DB_IDLE_TIMEOUT,
            WRITE_DB_KEEPALIVE_TIMEOUT, false);

        final var pool = new RoutingDataSourceConfigurer.WriteSettings.Pool(
            "mojave-accounting-write", WRITE_DB_MIN_POOL_SIZE, WRITE_DB_MAX_POOL_SIZE);

        return new RoutingDataSourceConfigurer.WriteSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingEntityManagerConfigurer.Settings routingEntityManagerSettings() {

        return new RoutingEntityManagerConfigurer.Settings("accounting-domain", false, false);
    }

}
