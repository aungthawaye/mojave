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
import org.mojave.core.settlement.contract.command.record.HandleSettlementCompletionCommand;
import org.mojave.core.settlement.contract.command.record.HandleSettlementPreparationCommand;
import org.mojave.core.settlement.contract.command.record.SendSettlementRequestCommand;
import org.mojave.core.settlement.producer.publisher.CompleteSettlementPublisher;
import org.mojave.core.settlement.producer.publisher.InitiateSettlementProcessPublisher;
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
    public KafkaTemplate<String, HandleSettlementCompletionCommand.Input> completeSettlementKafkaTemplate(
        @Qualifier(
            CompleteSettlementPublisher.QUALIFIER)
        ProducerFactory<String, HandleSettlementCompletionCommand.Input> producerFactory) {

        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @Qualifier(CompleteSettlementPublisher.QUALIFIER)
    public ProducerFactory<String, HandleSettlementCompletionCommand.Input> completeSettlementProducerFactory(
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
                public Serializer<HandleSettlementCompletionCommand.Input> forValue() {

                    return new JacksonJsonSerializer<>((JsonMapper) objectMapper);
                }
            });
    }

    @Bean
    @Qualifier(InitiateSettlementProcessPublisher.QUALIFIER)
    public KafkaTemplate<String, SendSettlementRequestCommand.Input> initiateSettlementProcessKafkaTemplate(
        @Qualifier(
            InitiateSettlementProcessPublisher.QUALIFIER)
        ProducerFactory<String, SendSettlementRequestCommand.Input> producerFactory) {

        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @Qualifier(InitiateSettlementProcessPublisher.QUALIFIER)
    public ProducerFactory<String, SendSettlementRequestCommand.Input> initiateSettlementProcessProducerFactory(
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
                public Serializer<SendSettlementRequestCommand.Input> forValue() {

                    return new JacksonJsonSerializer<>((JsonMapper) objectMapper);
                }
            });
    }

    @Bean
    @Qualifier(UpdatePreparationResultPublisher.QUALIFIER)
    public KafkaTemplate<String, HandleSettlementPreparationCommand.Input> updatePreparationResultKafkaTemplate(
        @Qualifier(
            UpdatePreparationResultPublisher.QUALIFIER)
        ProducerFactory<String, HandleSettlementPreparationCommand.Input> producerFactory) {

        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @Qualifier(UpdatePreparationResultPublisher.QUALIFIER)
    public ProducerFactory<String, HandleSettlementPreparationCommand.Input> updatePreparationResultProducerFactory(
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
                public Serializer<HandleSettlementPreparationCommand.Input> forValue() {

                    return new JacksonJsonSerializer<>((JsonMapper) objectMapper);
                }
            });
    }

    public interface RequiredDependencies { }

    public interface RequiredSettings {

        SettlementProducerConfiguration.ProducerSettings settlementProducerSettings();

    }

    public record ProducerSettings(String bootstrapServers, String ack) { }

}
