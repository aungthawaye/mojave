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
package org.mojave.component.jpa.routing;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAspectJAutoProxy(
    proxyTargetClass = true,
    exposeProxy = true)
@EnableJpaRepositories(
    basePackages = {"org.mojave"},
    considerNestedRepositories = true)
@EnableTransactionManagement
@ComponentScan(basePackages = {"org.mojave.component.jpa.routing"})
public class RoutingJpaConfiguration {

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(RoutingDataSource routingDataSource,
                                                                       RoutingEntityManagerConfigurer.Settings settings) {

        return RoutingEntityManagerConfigurer.configure(routingDataSource, settings, "org.mojave");
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

    public interface RequiredBeans { }

    public interface RequiredSettings {

        RoutingDataSourceConfigurer.ReadSettings routingDataSourceReadSettings();

        RoutingDataSourceConfigurer.WriteSettings routingDataSourceWriteSettings();

        RoutingEntityManagerConfigurer.Settings routingEntityManagerSettings();

    }

}
