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

package io.mojaloop.core.wallet.intercom;

import io.mojaloop.component.jpa.routing.RoutingDataSourceConfigurer;
import io.mojaloop.component.jpa.routing.RoutingEntityManagerConfigurer;
import io.mojaloop.component.openapi.OpenApiConfiguration;
import io.mojaloop.core.wallet.domain.WalletFlywayConfiguration;
import org.springframework.context.annotation.Bean;

final class WalletIntercomSettings implements WalletIntercomConfiguration.RequiredSettings {

    @Bean
    @Override
    public OpenApiConfiguration.ApiSettings apiSettings() {

        return new OpenApiConfiguration.ApiSettings("Mojave - Wallet - Intercom", "1.0.0");
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.ReadSettings routingDataSourceReadSettings() {

        var connection = new RoutingDataSourceConfigurer.ReadSettings.Connection(
            System.getenv("WLT_READ_DB_URL"), System.getenv("WLT_READ_DB_USER"),
            System.getenv("WLT_READ_DB_PASSWORD"), false);

        var pool = new RoutingDataSourceConfigurer.ReadSettings.Pool(
            "wallet-intercom-read", Integer.parseInt(System.getenv("WLT_READ_DB_MIN_POOL_SIZE")),
            Integer.parseInt(System.getenv("WLT_READ_DB_MAX_POOL_SIZE")));

        return new RoutingDataSourceConfigurer.ReadSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.WriteSettings routingDataSourceWriteSettings() {

        var connection = new RoutingDataSourceConfigurer.WriteSettings.Connection(
            System.getenv("WLT_WRITE_DB_URL"), System.getenv("WLT_WRITE_DB_USER"),
            System.getenv("WLT_WRITE_DB_PASSWORD"), false);

        var pool = new RoutingDataSourceConfigurer.WriteSettings.Pool(
            "wallet-intercom-write", Integer.parseInt(System.getenv("WLT_WRITE_DB_MIN_POOL_SIZE")),
            Integer.parseInt(System.getenv("WLT_WRITE_DB_MAX_POOL_SIZE")));

        return new RoutingDataSourceConfigurer.WriteSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingEntityManagerConfigurer.Settings routingEntityManagerSettings() {

        return new RoutingEntityManagerConfigurer.Settings("wallet-intercom", false, false);
    }

    @Bean
    @Override
    public WalletIntercomConfiguration.TomcatSettings tomcatSettings() {

        return new WalletIntercomConfiguration.TomcatSettings(
            Integer.parseInt(System.getenv("WALLET_INTERCOM_PORT")));
    }

    @Bean
    @Override
    public WalletFlywayConfiguration.Settings walletFlywaySettings() {

        return new WalletFlywayConfiguration.Settings(
            System.getenv("WLT_FLYWAY_DB_URL"), System.getenv("WLT_FLYWAY_DB_USER"),
            System.getenv("WLT_FLYWAY_DB_PASSWORD"), "flyway_wallet_history",
            new String[]{"classpath:migration/wallet"});
    }

}
