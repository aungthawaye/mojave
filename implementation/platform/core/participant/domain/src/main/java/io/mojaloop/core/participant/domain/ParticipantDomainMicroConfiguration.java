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

import io.mojaloop.common.component.ComponentConfiguration;
import io.mojaloop.common.component.persistence.routing.RoutingDataSource;
import io.mojaloop.common.component.persistence.routing.RoutingDataSourceConfigurer;
import io.mojaloop.common.component.persistence.routing.RoutingEntityManagerConfigurer;
import io.mojaloop.common.component.redis.RedisOpsClient;
import io.mojaloop.common.component.redis.RedisOpsConfigurer;
import io.mojaloop.common.component.vault.Vault;
import io.mojaloop.common.component.vault.VaultConfiguration;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableJpaRepositories(basePackages = {"io.mojaloop.core.participant.domain.model.repository"}, considerNestedRepositories = true)
@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackages = {"io.mojaloop.core.participant.domain"})
@Import(value = {ComponentConfiguration.class})
public class ParticipantDomainMicroConfiguration {

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(RoutingDataSource routingDataSource,
                                                                           RoutingEntityManagerConfigurer.SettingsProvider settingsProvider) {

        return (new RoutingEntityManagerConfigurer()).configure(routingDataSource,
                                                                settingsProvider,
                                                                "io.mojaloop.core.participant.domain.model");
    }

    @Bean
    public RedisOpsClient redisOpsClient(RedisOpsConfigurer.SettingsProvider settingsProvider) {

        return new RedisOpsClient(settingsProvider);
    }

    @Bean
    public RoutingDataSource routingDataSource(RoutingDataSourceConfigurer.SettingsProvider settingsProvider) {

        return (new RoutingDataSourceConfigurer()).configure(settingsProvider);
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {

        return new JpaTransactionManager(entityManagerFactory);
    }

    public static class VaultPaths {

        public static final String FLYWAY_PATH = "micro/core/participant/domain/flyway/migration";

        public static final String ROUTING_DATASOURCE_READ_SETTINGS_PATH = "micro/core/participant/domain/routing-datasource/read/settings";

        public static final String ROUTING_DATASOURCE_WRITE_SETTINGS_PATH = "micro/core/participant/domain/routing-datasource/write/settings";

        public static final String ENTITY_MANAGER_SETTINGS_PATH = "micro/core/participant/domain/entity-manager/settings";

    }

    @Import(value = {VaultConfiguration.class})
    public static class VaultBasedSettings {

        @Bean
        public RedisOpsConfigurer.SettingsProvider redisOpsConfigurationSettings(Vault vault) {

            return () -> vault.get("micro/core/participant/domain/redis/ops/settings", RedisOpsConfigurer.Settings.class);
        }

        @Bean
        public RoutingDataSourceConfigurer.SettingsProvider routingDataSourceSettings(Vault vault) {

            return new RoutingDataSourceConfigurer.SettingsProvider() {

                @Override
                public RoutingDataSourceConfigurer.ReadSettings routingDataSourceConfigurerReadSettings() {

                    return vault.get(VaultPaths.ROUTING_DATASOURCE_READ_SETTINGS_PATH, RoutingDataSourceConfigurer.ReadSettings.class);
                }

                @Override
                public RoutingDataSourceConfigurer.WriteSettings routingDataSourceConfigurerWriteSettings() {

                    return vault.get(VaultPaths.ROUTING_DATASOURCE_WRITE_SETTINGS_PATH, RoutingDataSourceConfigurer.WriteSettings.class);
                }
            };
        }

        @Bean
        public RoutingEntityManagerConfigurer.SettingsProvider routingEntityManagerSettings(Vault vault) {

            return () -> vault.get(VaultPaths.ENTITY_MANAGER_SETTINGS_PATH, RoutingEntityManagerConfigurer.Settings.class);
        }

    }

}
