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

package org.mojave.rail.fspiop.lookup.service;

import org.mojave.component.nats.NatsConfiguration;
import org.mojave.component.web.spring.security.SpringSecurityConfigurer;
import org.mojave.rail.fspiop.foundation.FspiopServiceConfiguration;
import org.springframework.context.annotation.Bean;

final class LookUpServiceSettings implements LookUpServiceConfiguration.RequiredSettings {

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
    public LookUpServiceConfiguration.TomcatSettings lookUpServiceTomcatSettings() {

        return new LookUpServiceConfiguration.TomcatSettings(
            Integer.parseInt(System.getenv("LOOKUP_SERVICE_PORT")));
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
    public FspiopServiceConfiguration.ServiceSettings serviceSettings() {

        return new FspiopServiceConfiguration.ServiceSettings(
            Integer.parseInt(System.getenv("FSPIOP_SERVICE_REQUEST_AGE_MS")),
            Boolean.parseBoolean(System.getenv("FSPIOP_SERVICE_REQUEST_AGE_VERIFICATION")));
    }

    @Bean
    @Override
    public SpringSecurityConfigurer.Settings springSecuritySettings() {

        return new SpringSecurityConfigurer.Settings(new String[]{"/parties/**"});
    }

}
