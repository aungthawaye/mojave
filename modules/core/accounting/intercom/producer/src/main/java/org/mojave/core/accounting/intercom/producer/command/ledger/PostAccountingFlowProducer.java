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

package org.mojave.core.accounting.intercom.producer.command.ledger;

import io.nats.client.Connection;
import jakarta.annotation.PostConstruct;
import org.mojave.core.accounting.contract.command.ledger.PostAccountingFlowCommand;
import org.mojave.core.accounting.contract.constant.AccountingStreamName;
import org.mojave.component.nats.NatsPublisher;
import org.mojave.component.nats.JetStreamConfigurer;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Service
public class PostAccountingFlowProducer {

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public PostAccountingFlowProducer(final Connection connection,
                                      final ObjectMapper objectMapper) {

        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.connection = connection;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void initialize() {

        JetStreamConfigurer.ensureStream(
            this.connection, AccountingStreamName.STREAM_NAME,
            PostAccountingFlowCommand.TOPIC_NAME);
    }

    public void publish(final PostAccountingFlowCommand.Input input) {

        NatsPublisher.publish(
            this.connection, PostAccountingFlowCommand.TOPIC_NAME, input,
            this.objectMapper);
    }

}
