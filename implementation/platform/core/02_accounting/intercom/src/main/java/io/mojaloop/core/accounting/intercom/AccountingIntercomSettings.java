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

package io.mojaloop.core.accounting.intercom;

import io.mojaloop.component.jpa.routing.RoutingDataSourceConfigurer;
import io.mojaloop.component.jpa.routing.RoutingEntityManagerConfigurer;
import io.mojaloop.core.accounting.domain.component.ledger.strategy.MySqlLedger;
import org.springframework.context.annotation.Bean;

final class AccountingIntercomSettings implements AccountingIntercomConfiguration.RequiredSettings {

    @Bean
    public MySqlLedger.LedgerDbSettings ledgerDbSettings() {

        return new MySqlLedger.LedgerDbSettings(new MySqlLedger.LedgerDbSettings.Connection(
            System.getenv().getOrDefault("ACC_LEDGER_DB_URL", "jdbc:mysql://localhost:3306/ml_accounting?createDatabaseIfNotExist=true"),
            System.getenv().getOrDefault("ACC_LEDGER_DB_USER", "root"), System.getenv().getOrDefault("ACC_LEDGER_DB_PASSWORD", "password")),
            new MySqlLedger.LedgerDbSettings.Pool("accounting-ledger", Integer.parseInt(System.getenv().getOrDefault("ACC_LEDGER_DB_MIN_POOL_SIZE", "2")),
                Integer.parseInt(System.getenv().getOrDefault("ACC_LEDGER_DB_MAX_POOL_SIZE", "10"))));
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.ReadSettings routingDataSourceReadSettings() {

        var connection = new RoutingDataSourceConfigurer.ReadSettings.Connection(
            System.getenv().getOrDefault("ACC_READ_DB_URL", "jdbc:mysql://localhost:3306/ml_accounting?createDatabaseIfNotExist=true"),
            System.getenv().getOrDefault("ACC_READ_DB_USER", "root"), System.getenv().getOrDefault("ACC_READ_DB_PASSWORD", "password"), false);

        var pool = new RoutingDataSourceConfigurer.ReadSettings.Pool("accounting-intercom-read", Integer.parseInt(System.getenv().getOrDefault("ACC_READ_DB_MIN_POOL_SIZE", "2")),
            Integer.parseInt(System.getenv().getOrDefault("ACC_READ_DB_MAX_POOL_SIZE", "10")));

        return new RoutingDataSourceConfigurer.ReadSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.WriteSettings routingDataSourceWriteSettings() {

        var connection = new RoutingDataSourceConfigurer.WriteSettings.Connection(
            System.getenv().getOrDefault("ACC_WRITE_DB_URL", "jdbc:mysql://localhost:3306/ml_accounting?createDatabaseIfNotExist=true"),
            System.getenv().getOrDefault("ACC_WRITE_DB_USER", "root"), System.getenv().getOrDefault("ACC_WRITE_DB_PASSWORD", "password"), false);

        var pool = new RoutingDataSourceConfigurer.WriteSettings.Pool("accounting-intercom-write",
            Integer.parseInt(System.getenv().getOrDefault("ACC_WRITE_DB_MIN_POOL_SIZE", "2")), Integer.parseInt(System.getenv().getOrDefault("ACC_WRITE_DB_MAX_POOL_SIZE", "10")));

        return new RoutingDataSourceConfigurer.WriteSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingEntityManagerConfigurer.Settings routingEntityManagerSettings() {

        return new RoutingEntityManagerConfigurer.Settings("accounting-intercom", false, false);
    }

    @Bean
    @Override
    public AccountingIntercomConfiguration.TomcatSettings tomcatSettings() {

        return new AccountingIntercomConfiguration.TomcatSettings(Integer.parseInt(System.getenv().getOrDefault("ACCOUNTING_INTERCOM_PORT", "4202")));
    }

}
