/*-
 * ================================================================================
 * Mojave
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

package io.mojaloop.core.accounting.admin;

import io.mojaloop.component.jpa.routing.RoutingDataSourceConfigurer;
import io.mojaloop.component.jpa.routing.RoutingEntityManagerConfigurer;
import io.mojaloop.component.openapi.OpenApiConfiguration;
import io.mojaloop.core.accounting.domain.AccountingFlywayConfiguration;
import org.springframework.context.annotation.Bean;

final class AccountingAdminSettings implements AccountingAdminConfiguration.RequiredSettings {

    @Bean
    @Override
    public AccountingFlywayConfiguration.Settings accountingFlywaySettings() {

        return new AccountingFlywayConfiguration.Settings(
            System.getenv("ACC_FLYWAY_DB_URL"), System.getenv("ACC_FLYWAY_DB_USER"),
            System.getenv("ACC_FLYWAY_DB_PASSWORD"), "flyway_accounting_history",
            new String[]{"classpath:migration/accounting"});

    }

    @Bean
    @Override
    public OpenApiConfiguration.ApiSettings apiSettings() {

        return new OpenApiConfiguration.ApiSettings("Mojave - Accounting - Admin", "1.0.0");
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.ReadSettings routingDataSourceReadSettings() {

        var connection = new RoutingDataSourceConfigurer.ReadSettings.Connection(
            System.getenv("ACC_READ_DB_URL"), System.getenv("ACC_READ_DB_USER"),
            System.getenv("ACC_READ_DB_PASSWORD"), false);

        var pool = new RoutingDataSourceConfigurer.ReadSettings.Pool(
            "accounting-admin-read", Integer.parseInt(System.getenv("ACC_READ_DB_MIN_POOL_SIZE")),
            Integer.parseInt(System.getenv("ACC_READ_DB_MAX_POOL_SIZE")));

        return new RoutingDataSourceConfigurer.ReadSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.WriteSettings routingDataSourceWriteSettings() {

        var connection = new RoutingDataSourceConfigurer.WriteSettings.Connection(
            System.getenv("ACC_WRITE_DB_URL"), System.getenv("ACC_WRITE_DB_USER"),
            System.getenv("ACC_WRITE_DB_PASSWORD"), false);

        var pool = new RoutingDataSourceConfigurer.WriteSettings.Pool(
            "accounting-admin-write", Integer.parseInt(System.getenv("ACC_WRITE_DB_MIN_POOL_SIZE")),
            Integer.parseInt(System.getenv("ACC_WRITE_DB_MAX_POOL_SIZE")));

        return new RoutingDataSourceConfigurer.WriteSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingEntityManagerConfigurer.Settings routingEntityManagerSettings() {

        return new RoutingEntityManagerConfigurer.Settings("accounting-admin", false, false);
    }

    @Bean
    @Override
    public AccountingAdminConfiguration.TomcatSettings tomcatSettings() {

        return new AccountingAdminConfiguration.TomcatSettings(
            Integer.parseInt(System.getenv("ACCOUNTING_ADMIN_PORT")));
    }

}
