package org.mojave.core.accounting.intercom.consumer;

import org.mojave.component.jpa.routing.RoutingDataSourceConfigurer;
import org.mojave.component.jpa.routing.RoutingEntityManagerConfigurer;
import org.mojave.component.nats.NatsConfiguration;
import org.springframework.context.annotation.Bean;

public class AccountingIntercomConsumerSettings
    implements AccountingIntercomConsumerConfiguration.RequiredSettings {

    private static String[] splitCsv(final String value) {

        return value.trim().split("\\s*,\\s*");
    }

    private static String toNullIfBlank(final String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value;
    }

    @Bean
    @Override
    public NatsConfiguration.NatsSettings natsSettings() {

        final var servers = splitCsv(System.getenv("NATS_SERVERS"));
        final var connectionName = System.getenv("NATS_CONNECTION_NAME");
        final var username = toNullIfBlank(System.getenv("NATS_USERNAME"));
        final var password = toNullIfBlank(System.getenv("NATS_PASSWORD"));
        final var token = toNullIfBlank(System.getenv("NATS_TOKEN"));
        final var connectionTimeoutMs = Integer.parseInt(
            System.getenv("NATS_CONNECTION_TIMEOUT_MS"));
        final var maxReconnects = Integer.parseInt(System.getenv("NATS_MAX_RECONNECTS"));
        final var reconnectWaitMs = Integer.parseInt(System.getenv("NATS_RECONNECT_WAIT_MS"));
        final var noEcho = Boolean.parseBoolean(System.getenv("NATS_NO_ECHO"));

        return new NatsConfiguration.NatsSettings(
            servers, connectionName, username, password,
            token, connectionTimeoutMs, maxReconnects, reconnectWaitMs, noEcho);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.ReadSettings routingDataSourceReadSettings() {

        var connection = new RoutingDataSourceConfigurer.ReadSettings.Connection(
            System.getenv("READ_DB_URL"), System.getenv("READ_DB_USER"),
            System.getenv("READ_DB_PASSWORD"),
            Long.parseLong(System.getenv("READ_DB_CONNECTION_TIMEOUT")),
            Long.parseLong(System.getenv("READ_DB_VALIDATION_TIMEOUT")),
            Long.parseLong(System.getenv("READ_DB_MAX_LIFETIME_TIMEOUT")),
            Long.parseLong(System.getenv("READ_DB_IDLE_TIMEOUT")),
            Long.parseLong(System.getenv("READ_DB_KEEPALIVE_TIMEOUT")), false);

        var pool = new RoutingDataSourceConfigurer.ReadSettings.Pool(
            "accounting-intercom-consumer-read",
            Integer.parseInt(System.getenv("READ_DB_MIN_POOL_SIZE")),
            Integer.parseInt(System.getenv("READ_DB_MAX_POOL_SIZE")));

        return new RoutingDataSourceConfigurer.ReadSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.WriteSettings routingDataSourceWriteSettings() {

        var connection = new RoutingDataSourceConfigurer.WriteSettings.Connection(
            System.getenv("WRITE_DB_URL"), System.getenv("WRITE_DB_USER"),
            System.getenv("WRITE_DB_PASSWORD"),
            Long.parseLong(System.getenv("WRITE_DB_CONNECTION_TIMEOUT")),
            Long.parseLong(System.getenv("WRITE_DB_VALIDATION_TIMEOUT")),
            Long.parseLong(System.getenv("WRITE_DB_MAX_LIFETIME_TIMEOUT")),
            Long.parseLong(System.getenv("WRITE_DB_IDLE_TIMEOUT")),
            Long.parseLong(System.getenv("WRITE_DB_KEEPALIVE_TIMEOUT")), false);

        var pool = new RoutingDataSourceConfigurer.WriteSettings.Pool(
            "accounting-intercom-consumer-write",
            Integer.parseInt(System.getenv("WRITE_DB_MIN_POOL_SIZE")),
            Integer.parseInt(System.getenv("WRITE_DB_MAX_POOL_SIZE")));

        return new RoutingDataSourceConfigurer.WriteSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingEntityManagerConfigurer.Settings routingEntityManagerSettings() {

        return new RoutingEntityManagerConfigurer.Settings(
            "accounting-intercom-consumer", false, false);
    }

}
