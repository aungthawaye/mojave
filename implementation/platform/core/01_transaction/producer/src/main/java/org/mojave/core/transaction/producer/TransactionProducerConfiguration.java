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
package org.mojave.core.transaction.producer;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.common.serialization.Serializer;
import org.mojave.component.kafka.KafkaProducerConfigurer;
import org.mojave.component.misc.MiscConfiguration;
import org.mojave.core.transaction.contract.command.AddStepCommand;
import org.mojave.core.transaction.contract.command.CloseTransactionCommand;
import org.mojave.core.transaction.producer.publisher.AddStepPublisher;
import org.mojave.core.transaction.producer.publisher.CloseTransactionPublisher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.HashMap;

@ComponentScan(basePackages = {"org.mojave.core.transaction.producer"})
@Import(value = {MiscConfiguration.class})
public class TransactionProducerConfiguration {

    @Bean
    @Qualifier(AddStepPublisher.QUALIFIER)
    public KafkaTemplate<String, AddStepCommand.Input> addStepKafkaTemplate(@Qualifier(
        AddStepPublisher.QUALIFIER) ProducerFactory<String, AddStepCommand.Input> producerFactory) {

        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @Qualifier(AddStepPublisher.QUALIFIER)
    public ProducerFactory<String, AddStepCommand.Input> addStepProducerFactory(
        TransactionProducerConfiguration.ProducerSettings settings,
        ObjectMapper objectMapper) {

        return KafkaProducerConfigurer.configure(
            settings.bootstrapServers(), settings.ack(),
            new KafkaProducerConfigurer.Serializers<>() {

                @Override
                public JacksonJsonSerializer<String> forKey() {

                    return new JacksonJsonSerializer<>((JsonMapper) objectMapper);
                }

                @Override
                public JacksonJsonSerializer<AddStepCommand.Input> forValue() {

                    return new JacksonJsonSerializer<>((JsonMapper) objectMapper);
                }
            });
    }

    @Bean
    @Qualifier(CloseTransactionPublisher.QUALIFIER)
    public KafkaTemplate<String, CloseTransactionCommand.Input> commitTransactionKafkaTemplate(
        @Qualifier(
            CloseTransactionPublisher.QUALIFIER)
        ProducerFactory<String, CloseTransactionCommand.Input> producerFactory) {

        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @Qualifier(CloseTransactionPublisher.QUALIFIER)
    public ProducerFactory<String, CloseTransactionCommand.Input> commitTransactionProducerFactory(
        TransactionProducerConfiguration.ProducerSettings settings,
        ObjectMapper objectMapper) {

        return KafkaProducerConfigurer.configure(
            settings.bootstrapServers(), settings.ack(),
            new KafkaProducerConfigurer.Serializers<>() {

                @Override
                public Serializer<String> forKey() {

                    return new JacksonJsonSerializer<>((JsonMapper) objectMapper);
                }

                @Override
                public Serializer<CloseTransactionCommand.Input> forValue() {

                    return new JacksonJsonSerializer<>((JsonMapper) objectMapper);
                }
            });
    }

    @Bean
    public KafkaAdmin kafkaAdmin(TransactionProducerConfiguration.ProducerSettings settings) {

        var cfg = new HashMap<String, Object>();

        cfg.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, settings.bootstrapServers());

        return new KafkaAdmin(cfg);
    }

    public interface RequiredBeans { }

    public interface RequiredSettings {

        TransactionProducerConfiguration.ProducerSettings transactionProducerSettings();

    }

    public record ProducerSettings(String bootstrapServers, String ack) { }

}
