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

package io.mojaloop.core.accounting.consumer;

import io.mojaloop.component.jpa.routing.RoutingDataSourceConfigurer;
import io.mojaloop.component.jpa.routing.RoutingEntityManagerConfigurer;
import io.mojaloop.core.accounting.consumer.listener.PostLedgerFlowListener;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.UUID;

class AccountingConsumerSettings implements AccountingConsumerConfiguration.RequiredSettings {

    @Bean
    @Override
    public PostLedgerFlowListener.Settings postLedgerFlowListenerSettings() {

        return new PostLedgerFlowListener.Settings(
            System.getenv("KAFKA_BROKER_URL"), PostLedgerFlowListener.GROUP_ID,
            UUID.randomUUID().toString(), "earliest", 1, 100, false,
            ContainerProperties.AckMode.MANUAL);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.ReadSettings routingDataSourceReadSettings() {

        var connection = new RoutingDataSourceConfigurer.ReadSettings.Connection(
            System.getenv("ACC_READ_DB_URL"), System.getenv("ACC_READ_DB_USER"),
            System.getenv("ACC_READ_DB_PASSWORD"), false);

        var pool = new RoutingDataSourceConfigurer.ReadSettings.Pool(
            "accounting-consumer-read",
            Integer.parseInt(System.getenv("ACC_READ_DB_MIN_POOL_SIZE")),
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
            "accounting-consumer-write",
            Integer.parseInt(System.getenv("ACC_WRITE_DB_MIN_POOL_SIZE")),
            Integer.parseInt(System.getenv("ACC_WRITE_DB_MAX_POOL_SIZE")));

        return new RoutingDataSourceConfigurer.WriteSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingEntityManagerConfigurer.Settings routingEntityManagerSettings() {

        return new RoutingEntityManagerConfigurer.Settings("accounting-consumer", false, false);
    }

}
