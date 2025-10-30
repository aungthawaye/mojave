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

package io.mojaloop.core.wallet.domain;

import io.mojaloop.component.jpa.routing.RoutingDataSourceConfigurer;
import io.mojaloop.component.jpa.routing.RoutingEntityManagerConfigurer;
import io.mojaloop.core.wallet.domain.component.mysql.MySqlBalanceUpdater;
import io.mojaloop.core.wallet.domain.component.mysql.MySqlPositionUpdater;
import org.springframework.context.annotation.Bean;

public class TestSettings implements WalletDomainConfiguration.RequiredSettings {

    @Bean
    public MySqlBalanceUpdater.BalanceDbSettings balanceDbSettings() {

        return new MySqlBalanceUpdater.BalanceDbSettings(new MySqlBalanceUpdater.BalanceDbSettings.Connection(
            "jdbc:mysql://localhost:3306/ml_wallet?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true",
            "root",
            "password"), new MySqlBalanceUpdater.BalanceDbSettings.Pool("wallet-balance", 2, 12));
    }

    @Bean
    public MySqlPositionUpdater.PositionDbSettings positionDbSettings() {

        return new MySqlPositionUpdater.PositionDbSettings(new MySqlPositionUpdater.PositionDbSettings.Connection(
            "jdbc:mysql://localhost:3306/ml_wallet?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true",
            "root",
            "password"), new MySqlPositionUpdater.PositionDbSettings.Pool("wallet-balance", 2, 12));
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.ReadSettings routingDataSourceReadSettings() {

        return new RoutingDataSourceConfigurer.ReadSettings(new RoutingDataSourceConfigurer.ReadSettings.Connection(
            "jdbc:mysql://localhost:3306/ml_wallet?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true",
            "root",
            "password",
            false), new RoutingDataSourceConfigurer.ReadSettings.Pool("wallet-read", 2, 4));
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.WriteSettings routingDataSourceWriteSettings() {

        return new RoutingDataSourceConfigurer.WriteSettings(new RoutingDataSourceConfigurer.WriteSettings.Connection(
            "jdbc:mysql://localhost:3306/ml_wallet?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true",
            "root",
            "password",
            false), new RoutingDataSourceConfigurer.WriteSettings.Pool("wallet-write", 2, 4));
    }

    @Bean
    @Override
    public RoutingEntityManagerConfigurer.Settings routingEntityManagerSettings() {

        return new RoutingEntityManagerConfigurer.Settings("wallet-domain", false, false);
    }

}
