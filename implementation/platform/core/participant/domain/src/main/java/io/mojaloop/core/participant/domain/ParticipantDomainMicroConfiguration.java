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
import io.mojaloop.common.component.redis.RedissonOpsClient;
import io.mojaloop.common.component.redis.RedissonOpsClientConfigurer;
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
                                                                           RoutingEntityManagerConfigurer.Settings settings) {

        return RoutingEntityManagerConfigurer.configure(routingDataSource, settings, "io.mojaloop.core.participant.domain.model");
    }

    @Bean
    public RedissonOpsClient redissonOpsClient(RedissonOpsClientConfigurer.Settings settings) {

        return RedissonOpsClientConfigurer.configure(settings);
    }

    @Bean
    public RoutingDataSource routingDataSource(RoutingDataSourceConfigurer.ReadSettings readSettings,
                                               RoutingDataSourceConfigurer.WriteSettings writeSettings) {

        return RoutingDataSourceConfigurer.configure(readSettings, writeSettings);
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {

        return new JpaTransactionManager(entityManagerFactory);
    }

}
