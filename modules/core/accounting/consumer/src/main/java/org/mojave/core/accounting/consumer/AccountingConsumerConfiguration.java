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
package org.mojave.core.accounting.consumer;

import org.apache.kafka.common.serialization.Deserializer;
import org.mojave.component.kafka.KafkaConsumerConfigurer;
import org.mojave.core.accounting.consumer.listener.PostLedgerFlowListener;
import org.mojave.core.accounting.contract.command.ledger.PostLedgerFlowCommand;
import org.mojave.core.accounting.domain.AccountingDomainConfiguration;
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
@ComponentScan(basePackages = {"org.mojave.core.accounting.consumer"})
@Import(value = {AccountingDomainConfiguration.class})
public class AccountingConsumerConfiguration {

    public AccountingConsumerConfiguration() {

    }

    @Bean(name = PostLedgerFlowListener.LISTENER_CONTAINER_FACTORY)
    @Qualifier(PostLedgerFlowListener.QUALIFIER)
    public ConcurrentKafkaListenerContainerFactory<String, PostLedgerFlowCommand.Input> addStepListenerContainerFactory(
        PostLedgerFlowListener.Settings settings,
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
                public Deserializer<PostLedgerFlowCommand.Input> forValue() {

                    var deserializer = new JacksonJsonDeserializer<>(
                        PostLedgerFlowCommand.Input.class, (JsonMapper) objectMapper);

                    deserializer.ignoreTypeHeaders().addTrustedPackages("*");

                    return deserializer;
                }
            });
    }

    public interface RequiredDependencies extends AccountingDomainConfiguration.RequiredBeans { }

    public interface RequiredSettings extends AccountingDomainConfiguration.RequiredSettings {

        PostLedgerFlowListener.Settings postLedgerFlowListenerSettings();

    }

}
