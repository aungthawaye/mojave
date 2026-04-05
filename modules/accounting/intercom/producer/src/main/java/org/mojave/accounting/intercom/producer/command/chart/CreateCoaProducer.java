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

package org.mojave.accounting.intercom.producer.command.chart;

import io.nats.client.Connection;
import org.mojave.accounting.contract.command.chart.CreateCoaCommand;
import org.mojave.accounting.contract.constant.AccountingStreamName;
import org.mojave.component.nats.CommandPublisher;
import org.mojave.component.nats.JetStreamConfigurer;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Service
public class CreateCoaProducer {

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public CreateCoaProducer(final Connection connection, final ObjectMapper objectMapper) {

        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.connection = connection;
        this.objectMapper = objectMapper;
    }

    public void publish(final CreateCoaCommand.Input input) {

        JetStreamConfigurer.ensureStream(
            this.connection, AccountingStreamName.STREAM_NAME,
            CreateCoaCommand.TOPIC_NAME);

        CommandPublisher.publish(
            this.connection, CreateCoaCommand.TOPIC_NAME, input,
            this.objectMapper);
    }

}
