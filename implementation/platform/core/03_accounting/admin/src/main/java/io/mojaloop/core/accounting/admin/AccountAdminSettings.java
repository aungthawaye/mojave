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

package io.mojaloop.core.accounting.admin;

import io.mojaloop.component.jpa.routing.RoutingDataSourceConfigurer;
import io.mojaloop.component.jpa.routing.RoutingEntityManagerConfigurer;
import io.mojaloop.component.redis.RedissonOpsClientConfigurer;
import io.mojaloop.core.accounting.domain.component.ledger.strategy.MySqlLedger;
import org.springframework.context.annotation.Bean;

public class AccountAdminSettings implements AccountAdminConfiguration.RequiredSettings {

    @Bean
    @Override
    public MySqlLedger.LedgerDbSettings ledgerDbSettings() {

        var connection = new MySqlLedger.LedgerDbSettings.Connection(System.getenv()
                                                                           .getOrDefault("ACC_LEDGER_DB_URL",
                                                                                         "jdbc:mysql://localhost:3306/ml_account?createDatabaseIfNotExist=true"),
                                                                     System.getenv().getOrDefault("ACC_LEDGER_DB_USER", "root"),
                                                                     System.getenv().getOrDefault("ACC_LEDGER_DB_PASSWORD", "password"));

        var pool = new MySqlLedger.LedgerDbSettings.Pool("account-admin-ledger",
                                                         Integer.parseInt(System.getenv().getOrDefault("ACC_LEDGER_DB_MIN_POOL_SIZE", "2")),
                                                         Integer.parseInt(System.getenv().getOrDefault("ACC_LEDGER_DB_MAX_POOL_SIZE", "10")));

        return new MySqlLedger.LedgerDbSettings(connection, pool);
    }

    @Bean
    @Override
    public RedissonOpsClientConfigurer.Settings redissonOpsClientSettings() {

        var hosts = System.getenv().getOrDefault("ACC_REDIS_HOSTS", "redis://localhost:6379");

        var cluster = Boolean.parseBoolean(System.getenv().getOrDefault("ACC_REDIS_CLUSTER", "false"));

        var executorCount = Integer.parseInt(System.getenv().getOrDefault("ACC_REDIS_EXECUTOR_COUNT", "10"));

        var connectionPoolSize = Integer.parseInt(System.getenv().getOrDefault("ACC_REDIS_CONNECTION_POOL_SIZE", "10"));

        var connectionMinimumIdleSize = Integer.parseInt(System.getenv().getOrDefault("ACC_REDIS_CONNECTION_MINIMUM_IDLE_SIZE", "10"));

        return new RedissonOpsClientConfigurer.Settings(hosts.split(",", -1), cluster, null, executorCount, connectionPoolSize, connectionMinimumIdleSize);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.ReadSettings routingDataSourceReadSettings() {

        var connection = new RoutingDataSourceConfigurer.ReadSettings.Connection(System.getenv()
                                                                                       .getOrDefault("ACC_READ_DB_URL",
                                                                                                     "jdbc:mysql://localhost:3306/ml_account?createDatabaseIfNotExist=true"),
                                                                                 System.getenv().getOrDefault("ACC_READ_DB_USER", "root"),
                                                                                 System.getenv().getOrDefault("ACC_READ_DB_PASSWORD", "password"),
                                                                                 false);

        var pool = new RoutingDataSourceConfigurer.ReadSettings.Pool("account-admin-read",
                                                                     Integer.parseInt(System.getenv().getOrDefault("ACC_READ_DB_MIN_POOL_SIZE", "2")),
                                                                     Integer.parseInt(System.getenv().getOrDefault("ACC_READ_DB_MAX_POOL_SIZE", "10")));

        return new RoutingDataSourceConfigurer.ReadSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.WriteSettings routingDataSourceWriteSettings() {

        var connection = new RoutingDataSourceConfigurer.WriteSettings.Connection(System.getenv()
                                                                                        .getOrDefault("ACC_WRITE_DB_URL",
                                                                                                      "jdbc:mysql://localhost:3306/ml_account?createDatabaseIfNotExist=true"),
                                                                                  System.getenv().getOrDefault("ACC_WRITE_DB_USER", "root"),
                                                                                  System.getenv().getOrDefault("ACC_WRITE_DB_PASSWORD", "password"),
                                                                                  false);

        var pool = new RoutingDataSourceConfigurer.WriteSettings.Pool("account-admin-write",
                                                                      Integer.parseInt(System.getenv().getOrDefault("ACC_WRITE_DB_MIN_POOL_SIZE", "2")),
                                                                      Integer.parseInt(System.getenv().getOrDefault("ACC_WRITE_DB_MAX_POOL_SIZE", "10")));

        return new RoutingDataSourceConfigurer.WriteSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingEntityManagerConfigurer.Settings routingEntityManagerSettings() {

        return new RoutingEntityManagerConfigurer.Settings("account-admin", false, false);
    }

    @Bean
    @Override
    public AccountAdminConfiguration.TomcatSettings tomcatSettings() {

        return new AccountAdminConfiguration.TomcatSettings(Integer.parseInt(System.getenv().getOrDefault("ACCOUNT_ADMIN_PORT", "4201")));
    }

}
