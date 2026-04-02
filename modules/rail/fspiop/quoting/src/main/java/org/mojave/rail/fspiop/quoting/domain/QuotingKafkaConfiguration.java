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

package org.mojave.rail.fspiop.quoting.domain;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.mojave.component.kafka.KafkaConsumerConfigurer;
import org.mojave.component.kafka.KafkaProducerConfigurer;
import org.mojave.rail.fspiop.quoting.contract.command.step.CreateQuotesRequestStep;
import org.mojave.rail.fspiop.quoting.contract.command.step.UpdateQuotesErrorStep;
import org.mojave.rail.fspiop.quoting.contract.command.step.UpdateQuotesResponseStep;
import org.mojave.rail.fspiop.quoting.domain.kafka.listener.CreateQuotesRequestStepListener;
import org.mojave.rail.fspiop.quoting.domain.kafka.listener.UpdateQuotesErrorStepListener;
import org.mojave.rail.fspiop.quoting.domain.kafka.listener.UpdateQuotesResponseStepListener;
import org.mojave.rail.fspiop.quoting.domain.kafka.publisher.CreateQuotesRequestStepPublisher;
import org.mojave.rail.fspiop.quoting.domain.kafka.publisher.UpdateQuotesErrorStepPublisher;
import org.mojave.rail.fspiop.quoting.domain.kafka.publisher.UpdateQuotesResponseStepPublisher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@EnableKafka
public class QuotingKafkaConfiguration {

    // ── CreateQuotesRequestStep ──────────────────────────────────────────────

    @Bean
    @Qualifier(CreateQuotesRequestStepPublisher.QUALIFIER)
    public KafkaTemplate<String, CreateQuotesRequestStep.Input> createQuotesRequestStepKafkaTemplate(
        @Qualifier(CreateQuotesRequestStepPublisher.QUALIFIER)
        ProducerFactory<String, CreateQuotesRequestStep.Input> producerFactory) {

        return new KafkaTemplate<>(producerFactory);
    }

    @Bean(name = CreateQuotesRequestStepListener.LISTENER_CONTAINER_FACTORY)
    @Qualifier(CreateQuotesRequestStepListener.QUALIFIER)
    public ConcurrentKafkaListenerContainerFactory<String, CreateQuotesRequestStep.Input> createQuotesRequestStepListenerContainerFactory(
        CreateQuotesRequestStepListener.Settings settings,
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
                public Deserializer<CreateQuotesRequestStep.Input> forValue() {

                    var deserializer = new JacksonJsonDeserializer<>(
                        CreateQuotesRequestStep.Input.class, (JsonMapper) objectMapper);

                    deserializer.ignoreTypeHeaders().addTrustedPackages("*");

                    return deserializer;
                }
            });
    }

    @Bean
    @Qualifier(CreateQuotesRequestStepPublisher.QUALIFIER)
    public ProducerFactory<String, CreateQuotesRequestStep.Input> createQuotesRequestStepProducerFactory(
        QuotingKafkaConfiguration.ProducerSettings settings,
        ObjectMapper objectMapper) {

        return KafkaProducerConfigurer.configure(
            settings.bootstrapServers(), settings.ack(),
            new KafkaProducerConfigurer.Serializers<>() {

                @Override
                public Serializer<String> forKey() {

                    return new JacksonJsonSerializer<>((JsonMapper) objectMapper);
                }

                @Override
                public Serializer<CreateQuotesRequestStep.Input> forValue() {

                    return new JacksonJsonSerializer<>((JsonMapper) objectMapper);
                }
            });
    }

    // ── UpdateQuotesResponseStep ─────────────────────────────────────────────

    @Bean
    @Qualifier(UpdateQuotesResponseStepPublisher.QUALIFIER)
    public KafkaTemplate<String, UpdateQuotesResponseStep.Input> updateQuotesResponseStepKafkaTemplate(
        @Qualifier(UpdateQuotesResponseStepPublisher.QUALIFIER)
        ProducerFactory<String, UpdateQuotesResponseStep.Input> producerFactory) {

        return new KafkaTemplate<>(producerFactory);
    }

    @Bean(name = UpdateQuotesResponseStepListener.LISTENER_CONTAINER_FACTORY)
    @Qualifier(UpdateQuotesResponseStepListener.QUALIFIER)
    public ConcurrentKafkaListenerContainerFactory<String, UpdateQuotesResponseStep.Input> updateQuotesResponseStepListenerContainerFactory(
        UpdateQuotesResponseStepListener.Settings settings,
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
                public Deserializer<UpdateQuotesResponseStep.Input> forValue() {

                    var deserializer = new JacksonJsonDeserializer<>(
                        UpdateQuotesResponseStep.Input.class, (JsonMapper) objectMapper);

                    deserializer.ignoreTypeHeaders().addTrustedPackages("*");

                    return deserializer;
                }
            });
    }

    @Bean
    @Qualifier(UpdateQuotesResponseStepPublisher.QUALIFIER)
    public ProducerFactory<String, UpdateQuotesResponseStep.Input> updateQuotesResponseStepProducerFactory(
        QuotingKafkaConfiguration.ProducerSettings settings,
        ObjectMapper objectMapper) {

        return KafkaProducerConfigurer.configure(
            settings.bootstrapServers(), settings.ack(),
            new KafkaProducerConfigurer.Serializers<>() {

                @Override
                public Serializer<String> forKey() {

                    return new JacksonJsonSerializer<>((JsonMapper) objectMapper);
                }

                @Override
                public Serializer<UpdateQuotesResponseStep.Input> forValue() {

                    return new JacksonJsonSerializer<>((JsonMapper) objectMapper);
                }
            });
    }

    // ── UpdateQuotesErrorStep ────────────────────────────────────────────────

    @Bean
    @Qualifier(UpdateQuotesErrorStepPublisher.QUALIFIER)
    public KafkaTemplate<String, UpdateQuotesErrorStep.Input> updateQuotesErrorStepKafkaTemplate(
        @Qualifier(UpdateQuotesErrorStepPublisher.QUALIFIER)
        ProducerFactory<String, UpdateQuotesErrorStep.Input> producerFactory) {

        return new KafkaTemplate<>(producerFactory);
    }

    @Bean(name = UpdateQuotesErrorStepListener.LISTENER_CONTAINER_FACTORY)
    @Qualifier(UpdateQuotesErrorStepListener.QUALIFIER)
    public ConcurrentKafkaListenerContainerFactory<String, UpdateQuotesErrorStep.Input> updateQuotesErrorStepListenerContainerFactory(
        UpdateQuotesErrorStepListener.Settings settings,
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
                public Deserializer<UpdateQuotesErrorStep.Input> forValue() {

                    var deserializer = new JacksonJsonDeserializer<>(
                        UpdateQuotesErrorStep.Input.class, (JsonMapper) objectMapper);

                    deserializer.ignoreTypeHeaders().addTrustedPackages("*");

                    return deserializer;
                }
            });
    }

    @Bean
    @Qualifier(UpdateQuotesErrorStepPublisher.QUALIFIER)
    public ProducerFactory<String, UpdateQuotesErrorStep.Input> updateQuotesErrorStepProducerFactory(
        QuotingKafkaConfiguration.ProducerSettings settings,
        ObjectMapper objectMapper) {

        return KafkaProducerConfigurer.configure(
            settings.bootstrapServers(), settings.ack(),
            new KafkaProducerConfigurer.Serializers<>() {

                @Override
                public Serializer<String> forKey() {

                    return new JacksonJsonSerializer<>((JsonMapper) objectMapper);
                }

                @Override
                public Serializer<UpdateQuotesErrorStep.Input> forValue() {

                    return new JacksonJsonSerializer<>((JsonMapper) objectMapper);
                }
            });
    }

    // ── Shared interfaces ────────────────────────────────────────────────────

    public interface RequiredDependencies { }

    public interface RequiredSettings {

        CreateQuotesRequestStepListener.Settings createQuotesRequestStepListenerSettings();

        UpdateQuotesResponseStepListener.Settings updateQuotesResponseStepListenerSettings();

        UpdateQuotesErrorStepListener.Settings updateQuotesErrorStepListenerSettings();

        QuotingKafkaConfiguration.ProducerSettings quotingProducerSettings();

    }

    public record ProducerSettings(String bootstrapServers, String ack) { }

}
