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

package org.mojave.rail.fspiop.quoting.service;

import org.mojave.component.jpa.routing.RoutingDataSourceConfigurer;
import org.mojave.component.jpa.routing.RoutingEntityManagerConfigurer;
import org.mojave.component.nats.NatsConfiguration;
import org.mojave.component.web.spring.security.SpringSecurityConfigurer;
import org.mojave.rail.fspiop.component.FspiopComponentConfiguration;
import org.mojave.rail.fspiop.quoting.domain.QuotingDomainConfiguration;
import org.mojave.rail.fspiop.quoting.domain.QuotingKafkaConfiguration;
import org.mojave.rail.fspiop.quoting.domain.kafka.listener.CreateQuotesRequestStepListener;
import org.mojave.rail.fspiop.quoting.domain.kafka.listener.UpdateQuotesErrorStepListener;
import org.mojave.rail.fspiop.quoting.domain.kafka.listener.UpdateQuotesResponseStepListener;
import org.mojave.rail.fspiop.service.FspiopServiceConfiguration;
import org.mojave.rail.fspiop.spec.Currency;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

final class QuotingServiceSettings implements QuotingServiceConfiguration.RequiredSettings {

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
    public CreateQuotesRequestStepListener.Settings createQuotesRequestStepListenerSettings() {

        return new CreateQuotesRequestStepListener.Settings(
            System.getenv("KAFKA_BROKER_URL"), CreateQuotesRequestStepListener.GROUP_ID,
            UUID.randomUUID().toString(), "earliest", 1, 1000, false,
            ContainerProperties.AckMode.MANUAL_IMMEDIATE);
    }

    @Bean
    @Override
    public UpdateQuotesResponseStepListener.Settings updateQuotesResponseStepListenerSettings() {

        return new UpdateQuotesResponseStepListener.Settings(
            System.getenv("KAFKA_BROKER_URL"), UpdateQuotesResponseStepListener.GROUP_ID,
            UUID.randomUUID().toString(), "earliest", 1, 1000, false,
            ContainerProperties.AckMode.MANUAL_IMMEDIATE);
    }

    @Bean
    @Override
    public UpdateQuotesErrorStepListener.Settings updateQuotesErrorStepListenerSettings() {

        return new UpdateQuotesErrorStepListener.Settings(
            System.getenv("KAFKA_BROKER_URL"), UpdateQuotesErrorStepListener.GROUP_ID,
            UUID.randomUUID().toString(), "earliest", 1, 1000, false,
            ContainerProperties.AckMode.MANUAL_IMMEDIATE);
    }

    @Bean
    @Override
    public QuotingKafkaConfiguration.ProducerSettings quotingProducerSettings() {

        return new QuotingKafkaConfiguration.ProducerSettings(
            System.getenv("KAFKA_BOOTSTRAP_SERVERS"), "all");
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
    public FspiopComponentConfiguration.ParticipantSettings participantSettings() {

        var hubCode = System.getenv("FSPIOP_HUB_CODE");
        var fspCode = System.getenv("FSPIOP_FSP_CODE");
        var fspName = System.getenv("FSPIOP_FSP_NAME");

        var currencyNames = System.getenv("FSPIOP_CURRENCIES").split(",", -1);
        var currencies = new ArrayList<Currency>();

        for (var currencyName : currencyNames) {
            currencies.add(Currency.valueOf(currencyName));
        }

        var ilpSecret = System.getenv("FSPIOP_ILP_SECRET");
        var signJws = Boolean.parseBoolean(System.getenv("FSPIOP_SIGN_JWS"));
        var verifyJws = Boolean.parseBoolean(System.getenv("FSPIOP_VERIFY_JWS"));
        var privateKeyPem = System.getenv("FSPIOP_PRIVATE_KEY_PEM");

        var fsps = System.getenv("FSPIOP_FSPS").split(",", -1);
        var fspPublicKeyPem = new HashMap<String, String>();

        for (var fsp : fsps) {

            var env = "FSPIOP_PUBLIC_KEY_PEM_OF_" + fsp.toUpperCase();
            var publicKeyPem = System.getenv(env);

            if (publicKeyPem != null) {
                fspPublicKeyPem.put(fsp, publicKeyPem);
            }
        }

        return new FspiopComponentConfiguration.ParticipantSettings(
            hubCode, fspCode, fspName,
            currencies, ilpSecret, signJws, verifyJws, privateKeyPem, fspPublicKeyPem);

    }

    @Bean
    @Override
    public QuotingDomainConfiguration.QuoteSettings quoteSettings() {

        return new QuotingDomainConfiguration.QuoteSettings(
            Boolean.parseBoolean(System.getenv("QUOTING_STATEFUL")));
    }

    @Bean
    @Override
    public QuotingServiceConfiguration.TomcatSettings quotingServiceTomcatSettings() {

        return new QuotingServiceConfiguration.TomcatSettings(
            Integer.parseInt(System.getenv("QUOTING_SERVICE_PORT")));
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
            "quoting-service-read", Integer.parseInt(System.getenv("READ_DB_MIN_POOL_SIZE")),
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
            "quoting-service-write", Integer.parseInt(System.getenv("WRITE_DB_MIN_POOL_SIZE")),
            Integer.parseInt(System.getenv("WRITE_DB_MAX_POOL_SIZE")));

        return new RoutingDataSourceConfigurer.WriteSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingEntityManagerConfigurer.Settings routingEntityManagerSettings() {

        return new RoutingEntityManagerConfigurer.Settings("quoting-service", false, false);
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

        return new SpringSecurityConfigurer.Settings(new String[]{"/quotes/**"});
    }

}
