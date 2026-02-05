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

package org.mojave.core.settlement.producer;

import org.apache.kafka.common.serialization.Serializer;
import org.mojave.component.kafka.KafkaProducerConfigurer;
import org.mojave.component.misc.MiscConfiguration;
import org.mojave.core.settlement.contract.command.record.CompleteSettlementCommand;
import org.mojave.core.settlement.contract.command.record.InitiateSettlementProcessCommand;
import org.mojave.core.settlement.contract.command.record.RequestSettlementInitiationCommand;
import org.mojave.core.settlement.contract.command.record.UpdatePreparationResultCommand;
import org.mojave.core.settlement.producer.publisher.CompleteSettlementPublisher;
import org.mojave.core.settlement.producer.publisher.InitiateSettlementProcessPublisher;
import org.mojave.core.settlement.producer.publisher.RequestSettlementInitiationPublisher;
import org.mojave.core.settlement.producer.publisher.UpdatePreparationResultPublisher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@ComponentScan(basePackages = {"org.mojave.core.settlement.producer"})
@Import(value = {MiscConfiguration.class})
public class SettlementProducerConfiguration {

    @Bean
    @Qualifier(CompleteSettlementPublisher.QUALIFIER)
    public KafkaTemplate<String, CompleteSettlementCommand.Input> completeSettlementKafkaTemplate(
        @Qualifier(
            CompleteSettlementPublisher.QUALIFIER)
        ProducerFactory<String, CompleteSettlementCommand.Input> producerFactory) {

        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @Qualifier(CompleteSettlementPublisher.QUALIFIER)
    public ProducerFactory<String, CompleteSettlementCommand.Input> completeSettlementProducerFactory(
        SettlementProducerConfiguration.ProducerSettings settings,
        ObjectMapper objectMapper) {

        return KafkaProducerConfigurer.configure(
            settings.bootstrapServers(), settings.ack(),
            new KafkaProducerConfigurer.Serializers<>() {

                @Override
                public Serializer<String> forKey() {

                    return new JacksonJsonSerializer<>((JsonMapper) objectMapper);
                }

                @Override
                public Serializer<CompleteSettlementCommand.Input> forValue() {

                    return new JacksonJsonSerializer<>((JsonMapper) objectMapper);
                }
            });
    }

    @Bean
    @Qualifier(InitiateSettlementProcessPublisher.QUALIFIER)
    public KafkaTemplate<String, InitiateSettlementProcessCommand.Input> initiateSettlementProcessKafkaTemplate(
        @Qualifier(
            InitiateSettlementProcessPublisher.QUALIFIER)
        ProducerFactory<String, InitiateSettlementProcessCommand.Input> producerFactory) {

        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @Qualifier(InitiateSettlementProcessPublisher.QUALIFIER)
    public ProducerFactory<String, InitiateSettlementProcessCommand.Input> initiateSettlementProcessProducerFactory(
        SettlementProducerConfiguration.ProducerSettings settings,
        ObjectMapper objectMapper) {

        return KafkaProducerConfigurer.configure(
            settings.bootstrapServers(), settings.ack(),
            new KafkaProducerConfigurer.Serializers<>() {

                @Override
                public Serializer<String> forKey() {

                    return new JacksonJsonSerializer<>((JsonMapper) objectMapper);
                }

                @Override
                public Serializer<InitiateSettlementProcessCommand.Input> forValue() {

                    return new JacksonJsonSerializer<>((JsonMapper) objectMapper);
                }
            });
    }

    @Bean
    @Qualifier(RequestSettlementInitiationPublisher.QUALIFIER)
    public KafkaTemplate<String, RequestSettlementInitiationCommand.Input> requestSettlementInitiationKafkaTemplate(
        @Qualifier(
            RequestSettlementInitiationPublisher.QUALIFIER)
        ProducerFactory<String, RequestSettlementInitiationCommand.Input> producerFactory) {

        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @Qualifier(RequestSettlementInitiationPublisher.QUALIFIER)
    public ProducerFactory<String, RequestSettlementInitiationCommand.Input> requestSettlementInitiationProducerFactory(
        SettlementProducerConfiguration.ProducerSettings settings,
        ObjectMapper objectMapper) {

        return KafkaProducerConfigurer.configure(
            settings.bootstrapServers(), settings.ack(),
            new KafkaProducerConfigurer.Serializers<>() {

                @Override
                public Serializer<String> forKey() {

                    return new JacksonJsonSerializer<>((JsonMapper) objectMapper);
                }

                @Override
                public Serializer<RequestSettlementInitiationCommand.Input> forValue() {

                    return new JacksonJsonSerializer<>((JsonMapper) objectMapper);
                }
            });
    }

    @Bean
    @Qualifier(UpdatePreparationResultPublisher.QUALIFIER)
    public KafkaTemplate<String, UpdatePreparationResultCommand.Input> updatePreparationResultKafkaTemplate(
        @Qualifier(
            UpdatePreparationResultPublisher.QUALIFIER)
        ProducerFactory<String, UpdatePreparationResultCommand.Input> producerFactory) {

        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @Qualifier(UpdatePreparationResultPublisher.QUALIFIER)
    public ProducerFactory<String, UpdatePreparationResultCommand.Input> updatePreparationResultProducerFactory(
        SettlementProducerConfiguration.ProducerSettings settings,
        ObjectMapper objectMapper) {

        return KafkaProducerConfigurer.configure(
            settings.bootstrapServers(), settings.ack(),
            new KafkaProducerConfigurer.Serializers<>() {

                @Override
                public Serializer<String> forKey() {

                    return new JacksonJsonSerializer<>((JsonMapper) objectMapper);
                }

                @Override
                public Serializer<UpdatePreparationResultCommand.Input> forValue() {

                    return new JacksonJsonSerializer<>((JsonMapper) objectMapper);
                }
            });
    }

    public interface RequiredBeans { }

    public interface RequiredSettings {

        SettlementProducerConfiguration.ProducerSettings settlementProducerSettings();

    }

    public record ProducerSettings(String bootstrapServers, String ack) { }

}
