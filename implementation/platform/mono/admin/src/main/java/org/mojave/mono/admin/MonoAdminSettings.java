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

package org.mojave.mono.admin;

import org.mojave.component.jpa.routing.RoutingDataSourceConfigurer;
import org.mojave.component.jpa.routing.RoutingEntityManagerConfigurer;
import org.mojave.component.openapi.OpenApiConfiguration;
import org.mojave.component.web.spring.security.SpringSecurityConfigurer;
import org.mojave.core.accounting.domain.component.ledger.strategy.MySqlLedger;
import org.mojave.core.wallet.domain.component.mysql.MySqlBalanceUpdater;
import org.mojave.core.wallet.domain.component.mysql.MySqlPositionUpdater;
import org.springframework.context.annotation.Bean;

public class MonoAdminSettings implements MonoAdminConfiguration.RequiredSettings {

    @Bean
    @Override
    public OpenApiConfiguration.ApiSettings apiSettings() {

        return new OpenApiConfiguration.ApiSettings("Mojave - Admin", "1.0.0");
    }

    @Bean
    @Override
    public MySqlBalanceUpdater.BalanceDbSettings balanceDbSettings() {

        return new MySqlBalanceUpdater.BalanceDbSettings(
            new MySqlBalanceUpdater.BalanceDbSettings.Connection(
                System.getenv("MONO_BALANCE_DB_URL"), System.getenv("MONO_BALANCE_DB_USER"),
                System.getenv("MONO_BALANCE_DB_PASSWORD")),
            new MySqlBalanceUpdater.BalanceDbSettings.Pool(
                "wallet-balance", Integer.parseInt(System.getenv("MONO_BALANCE_DB_MIN_POOL_SIZE")),
                Integer.parseInt(System.getenv("MONO_BALANCE_DB_MAX_POOL_SIZE"))));
    }

    @Bean
    @Override
    public MySqlLedger.LedgerDbSettings ledgerDbSettings() {

        return new MySqlLedger.LedgerDbSettings(
            new MySqlLedger.LedgerDbSettings.Connection(
                System.getenv("MONO_LEDGER_DB_URL"), System.getenv("MONO_LEDGER_DB_USER"),
                System.getenv("MONO_LEDGER_DB_PASSWORD")), new MySqlLedger.LedgerDbSettings.Pool(
            "accounting-ledger", Integer.parseInt(System.getenv("MONO_LEDGER_DB_MIN_POOL_SIZE")),
            Integer.parseInt(System.getenv("MONO_LEDGER_DB_MAX_POOL_SIZE"))));
    }

    @Bean
    @Override
    public MySqlPositionUpdater.PositionDbSettings positionDbSettings() {

        return new MySqlPositionUpdater.PositionDbSettings(
            new MySqlPositionUpdater.PositionDbSettings.Connection(
                System.getenv("MONO_POSITION_DB_URL"), System.getenv("MONO_POSITION_DB_USER"),
                System.getenv("MONO_POSITION_DB_PASSWORD")),
            new MySqlPositionUpdater.PositionDbSettings.Pool(
                "wallet-position",
                Integer.parseInt(System.getenv("MONO_POSITION_DB_MIN_POOL_SIZE")),
                Integer.parseInt(System.getenv("MONO_POSITION_DB_MAX_POOL_SIZE"))));
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.ReadSettings routingDataSourceReadSettings() {

        var connection = new RoutingDataSourceConfigurer.ReadSettings.Connection(
            System.getenv("MONO_READ_DB_URL"), System.getenv("MONO_READ_DB_USER"),
            System.getenv("MONO_READ_DB_PASSWORD"), false);

        var pool = new RoutingDataSourceConfigurer.ReadSettings.Pool(
            "mojave-admin-read", Integer.parseInt(System.getenv("MONO_READ_DB_MIN_POOL_SIZE")),
            Integer.parseInt(System.getenv("MONO_READ_DB_MAX_POOL_SIZE")));

        return new RoutingDataSourceConfigurer.ReadSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.WriteSettings routingDataSourceWriteSettings() {

        var connection = new RoutingDataSourceConfigurer.WriteSettings.Connection(
            System.getenv("MONO_WRITE_DB_URL"), System.getenv("MONO_WRITE_DB_USER"),
            System.getenv("MONO_WRITE_DB_PASSWORD"), false);

        var pool = new RoutingDataSourceConfigurer.WriteSettings.Pool(
            "mojave-admin-write", Integer.parseInt(System.getenv("MONO_WRITE_DB_MIN_POOL_SIZE")),
            Integer.parseInt(System.getenv("MONO_WRITE_DB_MAX_POOL_SIZE")));

        return new RoutingDataSourceConfigurer.WriteSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingEntityManagerConfigurer.Settings routingEntityManagerSettings() {

        return new RoutingEntityManagerConfigurer.Settings("mojave-admin", false, false);
    }

    @Bean
    @Override
    public SpringSecurityConfigurer.Settings springSecuritySettings() {

        return new SpringSecurityConfigurer.Settings(null);
    }

    @Bean
    @Override
    public MonoAdminConfiguration.TomcatSettings tomcatSettings() {

        return new MonoAdminConfiguration.TomcatSettings(
            Integer.parseInt(System.getenv("MOJAVE_ADMIN_PORT")));
    }

}
