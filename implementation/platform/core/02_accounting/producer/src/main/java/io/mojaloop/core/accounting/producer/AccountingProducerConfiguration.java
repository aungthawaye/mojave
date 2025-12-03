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
package io.mojaloop.core.accounting.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.kafka.KafkaProducerConfigurer;
import io.mojaloop.component.misc.MiscConfiguration;
import io.mojaloop.core.accounting.contract.command.ledger.PostLedgerFlowCommand;
import io.mojaloop.core.accounting.producer.publisher.PostLedgerFlowPublisher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@ComponentScan(basePackages = {"io.mojaloop.core.accounting.producer"})
@Import(value = {MiscConfiguration.class})
public class AccountingProducerConfiguration {

    @Bean
    @Qualifier(PostLedgerFlowPublisher.QUALIFIER)
    public KafkaTemplate<String, PostLedgerFlowCommand.Input> postLedgerFlowKafkaTemplate(@Qualifier(
        PostLedgerFlowPublisher.QUALIFIER) ProducerFactory<String, PostLedgerFlowCommand.Input> producerFactory) {

        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @Qualifier(PostLedgerFlowPublisher.QUALIFIER)
    public ProducerFactory<String, PostLedgerFlowCommand.Input> postLedgerFlowProducerFactory(
        KafkaProducerConfigurer.ProducerSettings settings,
        ObjectMapper objectMapper) {

        return KafkaProducerConfigurer.configure(
            settings.bootstrapServers(), settings.ack(),
            new KafkaProducerConfigurer.Serializer<>() {

                @Override
                public JsonSerializer<String> forKey() {

                    return new JsonSerializer<>(objectMapper);
                }

                @Override
                public JsonSerializer<PostLedgerFlowCommand.Input> forValue() {

                    return new JsonSerializer<>(objectMapper);
                }
            });
    }

    public interface RequiredBeans { }

    public interface RequiredSettings {

        KafkaProducerConfigurer.ProducerSettings accountingProducerSettings();

    }

}
