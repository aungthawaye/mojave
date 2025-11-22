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

package io.mojaloop.core.wallet.admin;

import io.mojaloop.component.jpa.routing.RoutingDataSourceConfigurer;
import io.mojaloop.component.jpa.routing.RoutingEntityManagerConfigurer;
import io.mojaloop.component.openapi.OpenApiConfiguration;
import io.mojaloop.core.wallet.domain.component.mysql.MySqlBalanceUpdater;
import io.mojaloop.core.wallet.domain.component.mysql.MySqlPositionUpdater;
import org.springframework.context.annotation.Bean;

final class WalletAdminSettings implements WalletAdminConfiguration.RequiredSettings {

    @Bean
    @Override
    public OpenApiConfiguration.ApiSettings apiSettings() {

        return new OpenApiConfiguration.ApiSettings("Mojave - Wallet - Admin", "1.0.0");
    }

    @Bean
    public MySqlBalanceUpdater.BalanceDbSettings balanceDbSettings() {

        return new MySqlBalanceUpdater.BalanceDbSettings(
            new MySqlBalanceUpdater.BalanceDbSettings.Connection(System.getenv("WLT_MYSQL_BALANCE_DB_URL"), System.getenv("WLT_MYSQL_BALANCE_DB_USER"),
                System.getenv("WLT_MYSQL_BALANCE_DB_PASSWORD")),
            new MySqlBalanceUpdater.BalanceDbSettings.Pool("wallet-balance", Integer.parseInt(System.getenv("WLT_MYSQL_BALANCE_DB_MIN_POOL_SIZE")),
                Integer.parseInt(System.getenv("WLT_MYSQL_BALANCE_DB_MAX_POOL_SIZE"))));
    }

    @Bean
    public MySqlPositionUpdater.PositionDbSettings positionDbSettings() {

        return new MySqlPositionUpdater.PositionDbSettings(
            new MySqlPositionUpdater.PositionDbSettings.Connection(System.getenv("WLT_MYSQL_POSITION_DB_URL"), System.getenv("WLT_MYSQL_POSITION_DB_USER"),
                System.getenv("WLT_MYSQL_POSITION_DB_PASSWORD")),
            new MySqlPositionUpdater.PositionDbSettings.Pool("wallet-position", Integer.parseInt(System.getenv("WLT_MYSQL_POSITION_DB_MIN_POOL_SIZE")),
                Integer.parseInt(System.getenv("WLT_MYSQL_POSITION_DB_MAX_POOL_SIZE"))));
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.ReadSettings routingDataSourceReadSettings() {

        var connection = new RoutingDataSourceConfigurer.ReadSettings.Connection(System.getenv("WLT_READ_DB_URL"), System.getenv("WLT_READ_DB_USER"),
            System.getenv("WLT_READ_DB_PASSWORD"), false);

        var pool = new RoutingDataSourceConfigurer.ReadSettings.Pool("wallet-admin-read", Integer.parseInt(System.getenv("WLT_READ_DB_MIN_POOL_SIZE")),
            Integer.parseInt(System.getenv("WLT_READ_DB_MAX_POOL_SIZE")));

        return new RoutingDataSourceConfigurer.ReadSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.WriteSettings routingDataSourceWriteSettings() {

        var connection = new RoutingDataSourceConfigurer.WriteSettings.Connection(System.getenv("WLT_WRITE_DB_URL"), System.getenv("WLT_WRITE_DB_USER"),
            System.getenv("WLT_WRITE_DB_PASSWORD"), false);

        var pool = new RoutingDataSourceConfigurer.WriteSettings.Pool("wallet-admin-write", Integer.parseInt(System.getenv("WLT_WRITE_DB_MIN_POOL_SIZE")),
            Integer.parseInt(System.getenv("WLT_WRITE_DB_MAX_POOL_SIZE")));

        return new RoutingDataSourceConfigurer.WriteSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingEntityManagerConfigurer.Settings routingEntityManagerSettings() {

        return new RoutingEntityManagerConfigurer.Settings("wallet-admin", false, false);
    }

    @Bean
    @Override
    public WalletAdminConfiguration.TomcatSettings tomcatSettings() {

        return new WalletAdminConfiguration.TomcatSettings(Integer.parseInt(System.getenv("WALLET_ADMIN_PORT")));
    }

}
