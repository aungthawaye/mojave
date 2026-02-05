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
import org.mojave.core.settlement.consumer.listener.RequestSettlementInitiationListener;
import org.mojave.core.settlement.consumer.listener.UpdatePreparationResultListener;
import org.mojave.core.settlement.contract.command.record.CompleteSettlementCommand;
import org.mojave.core.settlement.contract.command.record.InitiateSettlementProcessCommand;
import org.mojave.core.settlement.contract.command.record.RequestSettlementInitiationCommand;
import org.mojave.core.settlement.contract.command.record.UpdatePreparationResultCommand;
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
    public ConcurrentKafkaListenerContainerFactory<String, CompleteSettlementCommand.Input> completeSettlementListenerContainerFactory(
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
                public Deserializer<CompleteSettlementCommand.Input> forValue() {

                    var deserializer = new JacksonJsonDeserializer<>(
                        CompleteSettlementCommand.Input.class, (JsonMapper) objectMapper);

                    deserializer.ignoreTypeHeaders().addTrustedPackages("*");

                    return deserializer;
                }
            });
    }

    @Bean(name = InitiateSettlementProcessListener.LISTENER_CONTAINER_FACTORY)
    @Qualifier(InitiateSettlementProcessListener.QUALIFIER)
    public ConcurrentKafkaListenerContainerFactory<String, InitiateSettlementProcessCommand.Input> initiateSettlementProcessListenerContainerFactory(
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
                public Deserializer<InitiateSettlementProcessCommand.Input> forValue() {

                    var deserializer = new JacksonJsonDeserializer<>(
                        InitiateSettlementProcessCommand.Input.class, (JsonMapper) objectMapper);

                    deserializer.ignoreTypeHeaders().addTrustedPackages("*");

                    return deserializer;
                }
            });
    }

    @Bean(name = RequestSettlementInitiationListener.LISTENER_CONTAINER_FACTORY)
    @Qualifier(RequestSettlementInitiationListener.QUALIFIER)
    public ConcurrentKafkaListenerContainerFactory<String, RequestSettlementInitiationCommand.Input> requestSettlementInitiationListenerContainerFactory(
        RequestSettlementInitiationListener.Settings settings,
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
                public Deserializer<RequestSettlementInitiationCommand.Input> forValue() {

                    var deserializer = new JacksonJsonDeserializer<>(
                        RequestSettlementInitiationCommand.Input.class, (JsonMapper) objectMapper);

                    deserializer.ignoreTypeHeaders().addTrustedPackages("*");

                    return deserializer;
                }
            });
    }

    @Bean(name = UpdatePreparationResultListener.LISTENER_CONTAINER_FACTORY)
    @Qualifier(UpdatePreparationResultListener.QUALIFIER)
    public ConcurrentKafkaListenerContainerFactory<String, UpdatePreparationResultCommand.Input> updatePreparationResultListenerContainerFactory(
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
                public Deserializer<UpdatePreparationResultCommand.Input> forValue() {

                    var deserializer = new JacksonJsonDeserializer<>(
                        UpdatePreparationResultCommand.Input.class, (JsonMapper) objectMapper);

                    deserializer.ignoreTypeHeaders().addTrustedPackages("*");

                    return deserializer;
                }
            });
    }

    public interface RequiredDependencies extends SettlementDomainConfiguration.RequiredBeans { }

    public interface RequiredSettings extends SettlementDomainConfiguration.RequiredSettings {

        CompleteSettlementListener.Settings completeSettlementListenerSettings();

        InitiateSettlementProcessListener.Settings initiateSettlementProcessListenerSettings();

        RequestSettlementInitiationListener.Settings requestSettlementInitiationListenerSettings();

        UpdatePreparationResultListener.Settings updatePreparationResultListenerSettings();

    }

}
