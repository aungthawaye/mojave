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

package io.mojaloop.core.transaction.consumer;

import io.mojaloop.component.jpa.routing.RoutingDataSourceConfigurer;
import io.mojaloop.component.jpa.routing.RoutingEntityManagerConfigurer;
import io.mojaloop.core.transaction.consumer.listener.AddStepListener;
import io.mojaloop.core.transaction.consumer.listener.CloseTransactionListener;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.UUID;

public class TransactionConsumerSettings implements TransactionConsumerConfiguration.RequiredSettings {

    @Bean
    @Override
    public AddStepListener.Settings addStepListenerSettings() {

        return new AddStepListener.Settings(System.getenv().getOrDefault("KAFKA_BROKER_URL", "localhost:9092"), AddStepListener.GROUP_ID, UUID.randomUUID().toString(), "earliest",
            1, 100, false, ContainerProperties.AckMode.MANUAL);
    }

    @Bean
    @Override
    public CloseTransactionListener.Settings closeTransactionListenerSettings() {

        return new CloseTransactionListener.Settings(System.getenv().getOrDefault("KAFKA_BROKER_URL", "localhost:9092"), CloseTransactionListener.GROUP_ID,
            UUID.randomUUID().toString(), "earliest", 1, 100, false, ContainerProperties.AckMode.MANUAL);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.ReadSettings routingDataSourceReadSettings() {

        var connection = new RoutingDataSourceConfigurer.ReadSettings.Connection(
            System.getenv().getOrDefault("TXN_READ_DB_URL", "jdbc:mysql://localhost:3306/ml_transaction?createDatabaseIfNotExist=true"),
            System.getenv().getOrDefault("TXN_READ_DB_USER", "root"), System.getenv().getOrDefault("TXN_READ_DB_PASSWORD", "password"), false);

        var pool = new RoutingDataSourceConfigurer.ReadSettings.Pool("accounting-admin-read", Integer.parseInt(System.getenv().getOrDefault("TXN_READ_DB_MIN_POOL_SIZE", "2")),
            Integer.parseInt(System.getenv().getOrDefault("TXN_READ_DB_MAX_POOL_SIZE", "10")));

        return new RoutingDataSourceConfigurer.ReadSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.WriteSettings routingDataSourceWriteSettings() {

        var connection = new RoutingDataSourceConfigurer.WriteSettings.Connection(
            System.getenv().getOrDefault("TXN_WRITE_DB_URL", "jdbc:mysql://localhost:3306/ml_transaction?createDatabaseIfNotExist=true"),
            System.getenv().getOrDefault("TXN_WRITE_DB_USER", "root"), System.getenv().getOrDefault("TXN_WRITE_DB_PASSWORD", "password"), false);

        var pool = new RoutingDataSourceConfigurer.WriteSettings.Pool("accounting-admin-write", Integer.parseInt(System.getenv().getOrDefault("TXN_WRITE_DB_MIN_POOL_SIZE", "2")),
            Integer.parseInt(System.getenv().getOrDefault("TXN_WRITE_DB_MAX_POOL_SIZE", "10")));

        return new RoutingDataSourceConfigurer.WriteSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingEntityManagerConfigurer.Settings routingEntityManagerSettings() {

        return new RoutingEntityManagerConfigurer.Settings("accounting-admin", false, false);
    }

}
