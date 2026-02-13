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

package org.mojave.core.accounting.producer.publisher;

import org.mojave.core.accounting.contract.command.ledger.PostLedgerFlowCommand;
import org.mojave.core.accounting.contract.constant.TopicNames;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class PostLedgerFlowPublisher {

    public static final String QUALIFIER = "postLedgerFlow";

    private final KafkaTemplate<String, PostLedgerFlowCommand.Input> kafkaTemplate;

    public PostLedgerFlowPublisher(
        @Qualifier(QUALIFIER) KafkaTemplate<String, PostLedgerFlowCommand.Input> kafkaTemplate) {

        Objects.requireNonNull(kafkaTemplate);

        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(PostLedgerFlowCommand.Input input) {

        this.kafkaTemplate.send(
            TopicNames.POST_LEDGER_FLOW, input.transactionId().getId().toString(), input);

    }

}
