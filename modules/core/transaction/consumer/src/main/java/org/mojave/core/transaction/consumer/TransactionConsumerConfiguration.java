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

package org.mojave.core.transaction.consumer;

import org.apache.kafka.common.serialization.Deserializer;
import org.mojave.component.kafka.KafkaConsumerConfigurer;
import org.mojave.core.transaction.consumer.listener.AddStepListener;
import org.mojave.core.transaction.consumer.listener.CloseTransactionListener;
import org.mojave.core.transaction.contract.command.AddStepCommand;
import org.mojave.core.transaction.contract.command.CloseTransactionCommand;
import org.mojave.core.transaction.domain.TransactionDomainConfiguration;
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
@ComponentScan(basePackages = {"org.mojave.core.transaction.consumer"})
@Import(value = {TransactionDomainConfiguration.class})
public final class TransactionConsumerConfiguration {

    @Bean(name = AddStepListener.LISTENER_CONTAINER_FACTORY)
    @Qualifier(AddStepListener.QUALIFIER)
    public ConcurrentKafkaListenerContainerFactory<String, AddStepCommand.Input> addStepListenerContainerFactory(
        AddStepListener.Settings settings,
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
                public Deserializer<AddStepCommand.Input> forValue() {

                    var deserializer = new JacksonJsonDeserializer<>(
                        AddStepCommand.Input.class, (JsonMapper) objectMapper);

                    deserializer.ignoreTypeHeaders().addTrustedPackages("*");

                    return deserializer;
                }
            });
    }

    @Bean(name = CloseTransactionListener.LISTENER_CONTAINER_FACTORY)
    @Qualifier(CloseTransactionListener.QUALIFIER)
    public ConcurrentKafkaListenerContainerFactory<String, CloseTransactionCommand.Input> closeTransactionListenerContainerFactory(
        CloseTransactionListener.Settings settings,
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
                public Deserializer<CloseTransactionCommand.Input> forValue() {

                    var deserializer = new JacksonJsonDeserializer<>(
                        CloseTransactionCommand.Input.class, (JsonMapper) objectMapper);

                    deserializer.ignoreTypeHeaders().addTrustedPackages("*");

                    return deserializer;
                }
            });
    }

    public interface RequiredDependencies
        extends TransactionDomainConfiguration.RequiredDependencies { }

    public interface RequiredSettings extends TransactionDomainConfiguration.RequiredSettings {

        AddStepListener.Settings addStepListenerSettings();

        CloseTransactionListener.Settings closeTransactionListenerSettings();

    }

}
