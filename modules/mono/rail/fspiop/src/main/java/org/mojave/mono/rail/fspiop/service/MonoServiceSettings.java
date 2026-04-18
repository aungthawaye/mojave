/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===
 */

package org.mojave.mono.rail.fspiop.service;

import org.mojave.component.jpa.routing.RoutingDataSourceConfigurer;
import org.mojave.component.jpa.routing.RoutingEntityManagerConfigurer;
import org.mojave.component.nats.NatsConfiguration;
import org.mojave.component.openapi.OpenApiConfiguration;
import org.mojave.component.web.spring.security.SpringSecurityConfigurer;
import org.mojave.rail.fspiop.quoting.domain.QuotingDomainConfiguration;
import org.mojave.rail.fspiop.service.FspiopServiceConfiguration;
import org.mojave.rail.fspiop.transfer.domain.TransferDomainConfiguration;
import org.springframework.context.annotation.Bean;

public class MonoServiceSettings implements MonoServiceConfiguration.RequiredSettings {

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

        return new OpenApiConfiguration.ApiSettings("Mojave - FSPIOP Rail", "1.0.0");
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
    public QuotingDomainConfiguration.QuoteSettings quoteSettings() {

        return new QuotingDomainConfiguration.QuoteSettings(
            Boolean.parseBoolean(System.getenv("QUOTING_STATEFUL")));
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
            "mojave-service-read",
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
            "mojave-service-write", Integer.parseInt(System.getenv("WRITE_DB_MIN_POOL_SIZE")),
            Integer.parseInt(System.getenv("WRITE_DB_MAX_POOL_SIZE")));

        return new RoutingDataSourceConfigurer.WriteSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingEntityManagerConfigurer.Settings routingEntityManagerSettings() {

        return new RoutingEntityManagerConfigurer.Settings("mojave-service", false, false);
    }

    @Bean
    @Override
    public FspiopServiceConfiguration.ServiceSettings serviceSettings() {

        return new FspiopServiceConfiguration.ServiceSettings(
            Integer.parseInt(System.getenv("FSPIOP_SERVICE_REQUEST_AGE_MS")),
            Boolean.parseBoolean(System.getenv("FSPIOP_SERVICE_REQUEST_AGE_VERIFICATION")));
    }

    @Bean
    @Override
    public SpringSecurityConfigurer.Settings springSecuritySettings() {

        return new SpringSecurityConfigurer.Settings(new String[]{
            "/parties/**",
            "/quotes/**",
            "/transfers/**"});
    }

    @Bean
    @Override
    public MonoServiceConfiguration.TomcatSettings tomcatSettings() {

        return new MonoServiceConfiguration.TomcatSettings(
            Integer.parseInt(System.getenv("MOJAVE_SERVICE_PORT")));
    }

    @Bean
    @Override
    public TransferDomainConfiguration.TransferSettings transferSettings() {

        return new TransferDomainConfiguration.TransferSettings(
            Integer.parseInt(System.getenv("TRANSFER_RESERVATION_TIMEOUT_MS")),
            Integer.parseInt(System.getenv("TRANSFER_EXPIRY_TIMEOUT_MS")));
    }

}
