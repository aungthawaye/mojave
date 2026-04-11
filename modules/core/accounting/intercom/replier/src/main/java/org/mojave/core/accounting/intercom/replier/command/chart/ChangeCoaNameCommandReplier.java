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

package org.mojave.core.accounting.intercom.replier.command.chart;

import io.nats.client.Connection;
import io.nats.client.Message;
import org.mojave.core.accounting.contract.command.chart.ChangeCoaNameCommand;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.CommandResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class ChangeCoaNameCommandReplier {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeCoaNameCommandReplier.class);

    private final ChangeCoaNameCommand changeCoaNameCommand;

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public ChangeCoaNameCommandReplier(final ChangeCoaNameCommand changeCoaNameCommand,
                                final Connection connection,
                                final ObjectMapper objectMapper) {

        Objects.requireNonNull(changeCoaNameCommand);
        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.changeCoaNameCommand = changeCoaNameCommand;
        this.connection = connection;
        this.objectMapper = objectMapper;

        this.connection.createDispatcher(this::handle).subscribe(ChangeCoaNameCommand.SUBJECT_NAME);
    }

    private void handle(final Message message) {

        final var replyTo = message.getReplyTo();

        if (replyTo == null || replyTo.isBlank()) {
            LOGGER.warn("ChangeCoaNameCommandReplier : reply subject is missing.");
            return;
        }

        try {

            final var input = this.objectMapper.readValue(
                message.getData(), ChangeCoaNameCommand.Input.class);

            LOGGER.info("ChangeCoaNameCommandReplier : input: ({})", ObjectLogger.log(input));

            final var output = this.changeCoaNameCommand.execute(input);
            final var response = CommandResponse.success(this.objectMapper.valueToTree(output));
            final var responseData = this.objectMapper.writeValueAsBytes(response);

            this.connection.publish(replyTo, responseData);

            LOGGER.info("ChangeCoaNameCommandReplier : output : ({})", ObjectLogger.log(output));

        } catch (final Exception exception) {

            final var output = MojaveErrorResponse.from(exception);
            final var response = CommandResponse.failure(this.objectMapper.valueToTree(output));
            final var responseData = this.objectMapper.writeValueAsBytes(response);

            this.connection.publish(replyTo, responseData);
        }
    }

}
