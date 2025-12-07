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

package io.mojaloop.core.accounting.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.kafka.KafkaConsumerConfigurer;
import io.mojaloop.core.accounting.consumer.listener.PostLedgerFlowListener;
import io.mojaloop.core.accounting.contract.command.ledger.PostLedgerFlowCommand;
import io.mojaloop.core.accounting.domain.AccountingDomainConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@EnableKafka
@ComponentScan(basePackages = {"io.mojaloop.core.accounting.consumer"})
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
            settings, new KafkaConsumerConfigurer.Deserializer<>() {

                @Override
                public JsonDeserializer<String> forKey() {

                    var deserializer = new JsonDeserializer<>(String.class, objectMapper);

                    deserializer.ignoreTypeHeaders().addTrustedPackages("*");

                    return deserializer;
                }

                @Override
                public JsonDeserializer<PostLedgerFlowCommand.Input> forValue() {

                    var deserializer = new JsonDeserializer<>(
                        PostLedgerFlowCommand.Input.class, objectMapper);

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
