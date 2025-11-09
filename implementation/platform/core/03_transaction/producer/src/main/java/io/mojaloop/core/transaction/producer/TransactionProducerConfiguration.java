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
package io.mojaloop.core.transaction.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.kafka.KafkaProducerConfigurer;
import io.mojaloop.component.misc.MiscConfiguration;
import io.mojaloop.core.transaction.contract.command.AddStepCommand;
import io.mojaloop.core.transaction.contract.command.CloseTransactionCommand;
import io.mojaloop.core.transaction.producer.publisher.AddStepPublisher;
import io.mojaloop.core.transaction.producer.publisher.CommitTransactionPublisher;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;

@ComponentScan(basePackages = {"io.mojaloop.core.transaction.producer"})
@Import(value = {MiscConfiguration.class})
public class TransactionProducerConfiguration {

    @Bean
    @Qualifier(AddStepPublisher.QUALIFIER)
    public KafkaTemplate<String, AddStepCommand.Input> addStepKafkaTemplate(@Qualifier(AddStepPublisher.QUALIFIER) ProducerFactory<String, AddStepCommand.Input> producerFactory) {

        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @Qualifier(AddStepPublisher.QUALIFIER)
    public ProducerFactory<String, AddStepCommand.Input> addStepProducerFactory(KafkaProducerConfigurer.ProducerSettings settings, ObjectMapper objectMapper) {

        return KafkaProducerConfigurer.configure(
            settings.bootstrapServers(), settings.ack(), new KafkaProducerConfigurer.Serializer<>() {

                @Override
                public JsonSerializer<String> forKey() {

                    return new JsonSerializer<>(objectMapper);
                }

                @Override
                public JsonSerializer<AddStepCommand.Input> forValue() {

                    return new JsonSerializer<>(objectMapper);
                }
            });
    }

    @Bean
    @Qualifier(CommitTransactionPublisher.QUALIFIER)
    public KafkaTemplate<String, CloseTransactionCommand.Input> commitTransactionKafkaTemplate(@Qualifier(CommitTransactionPublisher.QUALIFIER) ProducerFactory<String, CloseTransactionCommand.Input> producerFactory) {

        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @Qualifier(CommitTransactionPublisher.QUALIFIER)
    public ProducerFactory<String, CloseTransactionCommand.Input> commitTransactionProducerFactory(KafkaProducerConfigurer.ProducerSettings settings, ObjectMapper objectMapper) {

        return KafkaProducerConfigurer.configure(
            settings.bootstrapServers(), settings.ack(), new KafkaProducerConfigurer.Serializer<>() {

                @Override
                public JsonSerializer<String> forKey() {

                    return new JsonSerializer<>(objectMapper);
                }

                @Override
                public JsonSerializer<CloseTransactionCommand.Input> forValue() {

                    return new JsonSerializer<>(objectMapper);
                }
            });
    }

    @Bean
    public KafkaAdmin kafkaAdmin(KafkaProducerConfigurer.ProducerSettings settings) {

        var cfg = new HashMap<String, Object>();

        cfg.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, settings.bootstrapServers());

        return new KafkaAdmin(cfg);
    }

    public interface RequiredBeans { }

    public interface RequiredSettings {

        KafkaProducerConfigurer.ProducerSettings producerSettings();

    }

}
