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

public class TestSettings implements TransactionConsumerConfiguration.RequiredSettings {

    @Bean
    @Override
    public AddStepListener.Settings addStepListenerSettings() {

        return new AddStepListener.Settings(
            "localhost:9092", AddStepListener.GROUP_ID, UUID.randomUUID().toString(), "earliest", 1, 100, false,
            ContainerProperties.AckMode.MANUAL);
    }

    @Bean
    @Override
    public CloseTransactionListener.Settings closeTransactionListenerSettings() {

        return new CloseTransactionListener.Settings(
            "localhost:9092", CloseTransactionListener.GROUP_ID, UUID.randomUUID().toString(), "earliest", 1, 100, false,
            ContainerProperties.AckMode.MANUAL);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.ReadSettings routingDataSourceReadSettings() {

        return new RoutingDataSourceConfigurer.ReadSettings(
            new RoutingDataSourceConfigurer.ReadSettings.Connection(
                "jdbc:mysql://localhost:3306/ml_transaction?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true", "root", "password", false),
            new RoutingDataSourceConfigurer.ReadSettings.Pool("transaction-read", 2, 4));
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.WriteSettings routingDataSourceWriteSettings() {

        return new RoutingDataSourceConfigurer.WriteSettings(
            new RoutingDataSourceConfigurer.WriteSettings.Connection(
                "jdbc:mysql://localhost:3306/ml_transaction?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true", "root", "password", false),
            new RoutingDataSourceConfigurer.WriteSettings.Pool("transaction-write", 2, 4));
    }

    @Bean
    @Override
    public RoutingEntityManagerConfigurer.Settings routingEntityManagerSettings() {

        return new RoutingEntityManagerConfigurer.Settings("transaction-domain", false, true);
    }

}
