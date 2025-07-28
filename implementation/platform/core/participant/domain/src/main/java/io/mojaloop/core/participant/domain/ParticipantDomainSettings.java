/*-
 * ================================================================================
 * Mojaloop OSS
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
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
 * ================================================================================
 */

package io.mojaloop.core.participant.domain;

import io.mojaloop.component.jpa.routing.RoutingDataSourceConfigurer;
import io.mojaloop.component.jpa.routing.RoutingEntityManagerConfigurer;
import io.mojaloop.component.redis.RedissonOpsClientConfigurer;
import io.mojaloop.component.vault.Vault;
import io.mojaloop.component.vault.VaultConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(value = {VaultConfiguration.class})
public class ParticipantDomainSettings implements ParticipantDomainConfiguration.RequiredSettings {

    private final Vault vault;

    public ParticipantDomainSettings(Vault vault) {

        assert vault != null;

        this.vault = vault;
    }

    @Bean
    @Override
    public RedissonOpsClientConfigurer.Settings redissonOpsClientSettings() {

        return this.vault.get(VaultPaths.REDIS_OPS_SETTINGS_PATH, RedissonOpsClientConfigurer.Settings.class);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.ReadSettings routingDataSourceReadSettings() {

        return this.vault.get(VaultPaths.ROUTING_DATASOURCE_READ_SETTINGS_PATH, RoutingDataSourceConfigurer.ReadSettings.class);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.WriteSettings routingDataSourceWriteSettings() {

        return this.vault.get(VaultPaths.ROUTING_DATASOURCE_WRITE_SETTINGS_PATH, RoutingDataSourceConfigurer.WriteSettings.class);
    }

    @Bean
    @Override
    public RoutingEntityManagerConfigurer.Settings routingEntityManagerSettings() {

        return this.vault.get(VaultPaths.ENTITY_MANAGER_SETTINGS_PATH, RoutingEntityManagerConfigurer.Settings.class);
    }

    public static class VaultPaths {

        public static final String FLYWAY_PATH = "micro/core/participant/domain/flyway/migration/settings";

        public static final String ROUTING_DATASOURCE_READ_SETTINGS_PATH = "micro/core/participant/domain/routing-datasource/read/settings";

        public static final String ROUTING_DATASOURCE_WRITE_SETTINGS_PATH = "micro/core/participant/domain/routing-datasource/write/settings";

        public static final String ENTITY_MANAGER_SETTINGS_PATH = "micro/core/participant/domain/entity-manager/settings";

        public static final String REDIS_OPS_SETTINGS_PATH = "micro/core/participant/domain/redis/ops/settings";

    }

}
