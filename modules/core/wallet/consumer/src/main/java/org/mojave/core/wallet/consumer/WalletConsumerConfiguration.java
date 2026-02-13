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

package org.mojave.core.wallet.consumer;

import org.apache.kafka.common.serialization.Deserializer;
import org.mojave.component.kafka.KafkaConsumerConfigurer;
import org.mojave.core.wallet.consumer.listener.FulfilPositionsListener;
import org.mojave.core.wallet.consumer.listener.RollbackReservationListener;
import org.mojave.core.wallet.contract.command.position.FulfilPositionsCommand;
import org.mojave.core.wallet.contract.command.position.RollbackReservationCommand;
import org.mojave.core.wallet.domain.WalletDomainConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@EnableKafka
@ComponentScan(basePackages = {"org.mojave.core.wallet.consumer"})
@Import(value = {WalletDomainConfiguration.class})
public class WalletConsumerConfiguration {

    public WalletConsumerConfiguration() {

    }

    @Bean(name = FulfilPositionsListener.LISTENER_CONTAINER_FACTORY)
    @Qualifier(FulfilPositionsListener.QUALIFIER)
    public ConcurrentKafkaListenerContainerFactory<String, FulfilPositionsCommand.Input> fulfilPositionsListenerContainerFactory(
        FulfilPositionsListener.Settings settings,
        ObjectMapper objectMapper) {

        return KafkaConsumerConfigurer.configure(
            settings, new KafkaConsumerConfigurer.Deserializers<>() {

                @Override
                public Deserializer<String> forKey() {

                    var deserializer = new JacksonJsonDeserializer<>(
                        String.class, (JsonMapper) objectMapper);

                    deserializer.ignoreTypeHeaders().addTrustedPackages("*");

                    return deserializer;
                }

                @Override
                public Deserializer<FulfilPositionsCommand.Input> forValue() {

                    var deserializer = new JacksonJsonDeserializer<>(
                        FulfilPositionsCommand.Input.class, (JsonMapper) objectMapper);

                    deserializer.ignoreTypeHeaders().addTrustedPackages("*");

                    return deserializer;
                }
            });
    }

    @Bean(name = RollbackReservationListener.LISTENER_CONTAINER_FACTORY)
    @Qualifier(RollbackReservationListener.QUALIFIER)
    public ConcurrentKafkaListenerContainerFactory<String, RollbackReservationCommand.Input> rollbackReservationListenerContainerFactory(
        RollbackReservationListener.Settings settings,
        ObjectMapper objectMapper) {

        return KafkaConsumerConfigurer.configure(
            settings, new KafkaConsumerConfigurer.Deserializers<>() {

                @Override
                public Deserializer<String> forKey() {

                    var deserializer = new JacksonJsonDeserializer<>(
                        String.class, (JsonMapper) objectMapper);

                    deserializer.ignoreTypeHeaders().addTrustedPackages("*");

                    return deserializer;
                }

                @Override
                public Deserializer<RollbackReservationCommand.Input> forValue() {

                    var deserializer = new JacksonJsonDeserializer<>(
                        RollbackReservationCommand.Input.class, (JsonMapper) objectMapper);

                    deserializer.ignoreTypeHeaders().addTrustedPackages("*");

                    return deserializer;
                }
            });
    }

    public interface RequiredDependencies extends WalletDomainConfiguration.RequiredDependencies { }

    public interface RequiredSettings extends WalletDomainConfiguration.RequiredSettings {

        FulfilPositionsListener.Settings fulfilPositionsListenerSettings();

        RollbackReservationListener.Settings rollbackReservationListenerSettings();

    }

}
