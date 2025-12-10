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

package io.mojaloop.component.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;

public class KafkaConsumerConfigurer {

    public static <K, V> ConcurrentKafkaListenerContainerFactory<K, V> configure(ConsumerSettings opts,
                                                                                 Deserializers<K, V> deserializer) {

        var props = new HashMap<String, Object>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, opts.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, opts.getGroupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, opts.getAutoOffsetReset());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.toString(opts.isAutoCommit()));
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, opts.getClientId());

        var consumerFactory = new DefaultKafkaConsumerFactory<>(
            props, deserializer.forKey(), deserializer.forValue());

        var listenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<K, V>();

        listenerContainerFactory.setConsumerFactory(consumerFactory);

        if (opts.getConcurrency() > 0) {
            listenerContainerFactory.setConcurrency(opts.getConcurrency());
        }

        if (opts.getPollTimeoutMs() > 0) {
            listenerContainerFactory
                .getContainerProperties()
                .setPollTimeout(opts.getPollTimeoutMs());
        }

        // Ack mode is only meaningful when auto-commit is false
        if (!opts.isAutoCommit() && opts.getAckMode() != null) {
            listenerContainerFactory.getContainerProperties().setAckMode(opts.getAckMode());
        }

        return listenerContainerFactory;
    }

    public interface Deserializers<K, V> {

        Deserializer<K> forKey();

        Deserializer<V> forValue();

    }

    @Getter
    @AllArgsConstructor
    public static class ConsumerSettings {

        private String bootstrapServers;

        private String groupId;

        private String clientId;

        private String autoOffsetReset;

        private int concurrency;

        private int pollTimeoutMs;

        private boolean autoCommit;

        private ContainerProperties.AckMode ackMode;

    }

}
