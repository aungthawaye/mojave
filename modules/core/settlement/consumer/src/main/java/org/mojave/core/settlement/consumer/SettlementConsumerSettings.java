/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
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
 * ===
 */

package org.mojave.core.settlement.consumer;

import org.mojave.component.jpa.routing.RoutingDataSourceConfigurer;
import org.mojave.component.jpa.routing.RoutingEntityManagerConfigurer;
import org.mojave.core.settlement.consumer.listener.CompleteSettlementListener;
import org.mojave.core.settlement.consumer.listener.InitiateSettlementProcessListener;
import org.mojave.core.settlement.consumer.listener.RequestSettlementInitiationListener;
import org.mojave.core.settlement.consumer.listener.UpdatePreparationResultListener;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.UUID;

class SettlementConsumerSettings implements SettlementConsumerConfiguration.RequiredSettings {

    @Bean
    @Override
    public CompleteSettlementListener.Settings completeSettlementListenerSettings() {

        return new CompleteSettlementListener.Settings(
            System.getenv("KAFKA_BROKER_URL"), CompleteSettlementListener.GROUP_ID,
            UUID.randomUUID().toString(), "earliest", 1, 100, false,
            ContainerProperties.AckMode.MANUAL);
    }

    @Bean
    @Override
    public InitiateSettlementProcessListener.Settings initiateSettlementProcessListenerSettings() {

        return new InitiateSettlementProcessListener.Settings(
            System.getenv("KAFKA_BROKER_URL"), InitiateSettlementProcessListener.GROUP_ID,
            UUID.randomUUID().toString(), "earliest", 1, 100, false,
            ContainerProperties.AckMode.MANUAL);
    }

    @Bean
    @Override
    public RequestSettlementInitiationListener.Settings requestSettlementInitiationListenerSettings() {

        return new RequestSettlementInitiationListener.Settings(
            System.getenv("KAFKA_BROKER_URL"), RequestSettlementInitiationListener.GROUP_ID,
            UUID.randomUUID().toString(), "earliest", 1, 100, false,
            ContainerProperties.AckMode.MANUAL);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.ReadSettings routingDataSourceReadSettings() {

        var connection = new RoutingDataSourceConfigurer.ReadSettings.Connection(
            System.getenv("READ_DB_URL"), System.getenv("READ_DB_USER"),
            System.getenv("READ_DB_PASSWORD"),
            Long.parseLong(System.getenv("READ_DB_CONNECTION_TIMEOUT")),
            Long.parseLong(System.getenv("READ_DB_VALIDATION_TIMEOUT")),
            Long.parseLong(System.getenv("READ_DB_MAX_LIFETIME_TIMEOUT")),
            Long.parseLong(System.getenv("READ_DB_IDLE_TIMEOUT")),
            Long.parseLong(System.getenv("READ_DB_KEEPALIVE_TIMEOUT")), false);

        var pool = new RoutingDataSourceConfigurer.ReadSettings.Pool(
            "settlement-consumer-read", Integer.parseInt(System.getenv("READ_DB_MIN_POOL_SIZE")),
            Integer.parseInt(System.getenv("READ_DB_MAX_POOL_SIZE")));

        return new RoutingDataSourceConfigurer.ReadSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.WriteSettings routingDataSourceWriteSettings() {

        var connection = new RoutingDataSourceConfigurer.WriteSettings.Connection(
            System.getenv("WRITE_DB_URL"), System.getenv("WRITE_DB_USER"),
            System.getenv("WRITE_DB_PASSWORD"),
            Long.parseLong(System.getenv("WRITE_DB_CONNECTION_TIMEOUT")),
            Long.parseLong(System.getenv("WRITE_DB_VALIDATION_TIMEOUT")),
            Long.parseLong(System.getenv("WRITE_DB_MAX_LIFETIME_TIMEOUT")),
            Long.parseLong(System.getenv("WRITE_DB_IDLE_TIMEOUT")),
            Long.parseLong(System.getenv("WRITE_DB_KEEPALIVE_TIMEOUT")), false);

        var pool = new RoutingDataSourceConfigurer.WriteSettings.Pool(
            "settlement-consumer-write", Integer.parseInt(System.getenv("WRITE_DB_MIN_POOL_SIZE")),
            Integer.parseInt(System.getenv("WRITE_DB_MAX_POOL_SIZE")));

        return new RoutingDataSourceConfigurer.WriteSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingEntityManagerConfigurer.Settings routingEntityManagerSettings() {

        return new RoutingEntityManagerConfigurer.Settings("settlement-consumer", false, false);
    }

    @Bean
    @Override
    public UpdatePreparationResultListener.Settings updatePreparationResultListenerSettings() {

        return new UpdatePreparationResultListener.Settings(
            System.getenv("KAFKA_BROKER_URL"), UpdatePreparationResultListener.GROUP_ID,
            UUID.randomUUID().toString(), "earliest", 1, 100, false,
            ContainerProperties.AckMode.MANUAL);
    }

}
