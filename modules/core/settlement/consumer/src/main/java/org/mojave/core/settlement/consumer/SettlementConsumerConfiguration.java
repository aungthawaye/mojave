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

import org.apache.kafka.common.serialization.Deserializer;
import org.mojave.component.kafka.KafkaConsumerConfigurer;
import org.mojave.core.settlement.consumer.listener.CompleteSettlementListener;
import org.mojave.core.settlement.consumer.listener.InitiateSettlementProcessListener;
import org.mojave.core.settlement.consumer.listener.UpdatePreparationResultListener;
import org.mojave.core.settlement.contract.command.record.HandleSettlementCompletionCommand;
import org.mojave.core.settlement.contract.command.record.HandleSettlementPreparationCommand;
import org.mojave.core.settlement.contract.command.record.SendSettlementRequestCommand;
import org.mojave.core.settlement.domain.SettlementDomainConfiguration;
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
@ComponentScan(basePackages = {"org.mojave.core.settlement.consumer"})
@Import(value = {SettlementDomainConfiguration.class})
public class SettlementConsumerConfiguration {

    @Bean(name = CompleteSettlementListener.LISTENER_CONTAINER_FACTORY)
    @Qualifier(CompleteSettlementListener.QUALIFIER)
    public ConcurrentKafkaListenerContainerFactory<String, HandleSettlementCompletionCommand.Input> completeSettlementListenerContainerFactory(
        CompleteSettlementListener.Settings settings,
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
                public Deserializer<HandleSettlementCompletionCommand.Input> forValue() {

                    var deserializer = new JacksonJsonDeserializer<>(
                        HandleSettlementCompletionCommand.Input.class, (JsonMapper) objectMapper);

                    deserializer.ignoreTypeHeaders().addTrustedPackages("*");

                    return deserializer;
                }
            });
    }

    @Bean(name = InitiateSettlementProcessListener.LISTENER_CONTAINER_FACTORY)
    @Qualifier(InitiateSettlementProcessListener.QUALIFIER)
    public ConcurrentKafkaListenerContainerFactory<String, SendSettlementRequestCommand.Input> initiateSettlementProcessListenerContainerFactory(
        InitiateSettlementProcessListener.Settings settings,
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
                public Deserializer<SendSettlementRequestCommand.Input> forValue() {

                    var deserializer = new JacksonJsonDeserializer<>(
                        SendSettlementRequestCommand.Input.class, (JsonMapper) objectMapper);

                    deserializer.ignoreTypeHeaders().addTrustedPackages("*");

                    return deserializer;
                }
            });
    }

    @Bean(name = UpdatePreparationResultListener.LISTENER_CONTAINER_FACTORY)
    @Qualifier(UpdatePreparationResultListener.QUALIFIER)
    public ConcurrentKafkaListenerContainerFactory<String, HandleSettlementPreparationCommand.Input> updatePreparationResultListenerContainerFactory(
        UpdatePreparationResultListener.Settings settings,
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
                public Deserializer<HandleSettlementPreparationCommand.Input> forValue() {

                    var deserializer = new JacksonJsonDeserializer<>(
                        HandleSettlementPreparationCommand.Input.class, (JsonMapper) objectMapper);

                    deserializer.ignoreTypeHeaders().addTrustedPackages("*");

                    return deserializer;
                }
            });
    }

    public interface RequiredDependencies
        extends SettlementDomainConfiguration.RequiredDependencies { }

    public interface RequiredSettings extends SettlementDomainConfiguration.RequiredSettings {

        CompleteSettlementListener.Settings completeSettlementListenerSettings();

        InitiateSettlementProcessListener.Settings initiateSettlementProcessListenerSettings();

        UpdatePreparationResultListener.Settings updatePreparationResultListenerSettings();

    }

}
