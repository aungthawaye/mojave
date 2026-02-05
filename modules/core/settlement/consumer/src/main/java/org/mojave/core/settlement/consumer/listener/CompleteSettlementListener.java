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

package org.mojave.core.settlement.consumer.listener;

import org.mojave.component.kafka.KafkaConsumerConfigurer;
import org.mojave.core.settlement.contract.command.record.CompleteSettlementCommand;
import org.mojave.core.settlement.contract.constant.TopicNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CompleteSettlementListener {

    public static final String QUALIFIER = "completeSettlement";

    public static final String LISTENER_CONTAINER_FACTORY = "completeSettlementListenerContainerFactory";

    public static final String GROUP_ID = "primary-consumer";

    private static final Logger LOGGER = LoggerFactory.getLogger(CompleteSettlementListener.class);

    private final CompleteSettlementCommand completeSettlementCommand;

    public CompleteSettlementListener(CompleteSettlementCommand completeSettlementCommand) {

        Objects.requireNonNull(completeSettlementCommand);
        this.completeSettlementCommand = completeSettlementCommand;
    }

    @KafkaListener(
        topics = TopicNames.COMPLETE_SETTLEMENT,
        containerFactory = LISTENER_CONTAINER_FACTORY,
        groupId = GROUP_ID)
    public void handle(CompleteSettlementCommand.Input input, Acknowledgment ack) {

        try {

            this.completeSettlementCommand.execute(input);
            ack.acknowledge();

        } catch (Exception e) {

            LOGGER.error("Error:", e);
        }
    }

    public static class Settings extends KafkaConsumerConfigurer.ConsumerSettings {

        public Settings(String bootstrapServers,
                        String groupId,
                        String clientId,
                        String autoOffsetReset,
                        int concurrency,
                        int pollTimeoutMs,
                        boolean autoCommit,
                        ContainerProperties.AckMode ackMode) {

            super(
                bootstrapServers, groupId, clientId, autoOffsetReset, 1, concurrency,
                pollTimeoutMs, autoCommit, ackMode);
        }

    }

}
