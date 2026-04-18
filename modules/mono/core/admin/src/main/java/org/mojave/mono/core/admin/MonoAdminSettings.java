package org.mojave.mono.core.admin;

import org.mojave.component.nats.NatsConfiguration;
import org.mojave.component.openapi.OpenApiConfiguration;
import org.mojave.component.web.spring.security.SpringSecurityConfigurer;
import org.springframework.context.annotation.Bean;

public class MonoAdminSettings implements MonoAdminConfiguration.RequiredSettings {

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
    public OpenApiConfiguration.ApiSettings apiSettings() {

        return new OpenApiConfiguration.ApiSettings("Mojave - Core Admin", "1.0.0");
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
    public SpringSecurityConfigurer.Settings springSecuritySettings() {

        return new SpringSecurityConfigurer.Settings(null);
    }

    @Bean
    @Override
    public MonoAdminConfiguration.TomcatSettings tomcatSettings() {

        final var portNo = Integer.parseInt(System.getenv("MOJAVE_ADMIN_PORT"));

        return new MonoAdminConfiguration.TomcatSettings(portNo);
    }

}
