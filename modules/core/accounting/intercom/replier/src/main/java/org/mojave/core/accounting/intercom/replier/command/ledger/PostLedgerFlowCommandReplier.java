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

package org.mojave.core.accounting.intercom.replier.command.ledger;

import io.nats.client.Connection;
import io.nats.client.Message;
import org.mojave.core.accounting.contract.command.ledger.PostAccountingFlowCommand;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class PostLedgerFlowCommandReplier {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostLedgerFlowCommandReplier.class);

    private final PostAccountingFlowCommand postAccountingFlowCommand;

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public PostLedgerFlowCommandReplier(final PostAccountingFlowCommand postAccountingFlowCommand,
                                 final Connection connection,
                                 final ObjectMapper objectMapper) {

        Objects.requireNonNull(postAccountingFlowCommand);
        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.postAccountingFlowCommand = postAccountingFlowCommand;
        this.connection = connection;
        this.objectMapper = objectMapper;

        this.connection.createDispatcher(this::handle).subscribe(
            PostAccountingFlowCommand.SUBJECT_NAME);
    }

    private void handle(final Message message) {

        final var replyTo = message.getReplyTo();

        if (replyTo == null || replyTo.isBlank()) {
            LOGGER.warn("PostLedgerFlowCommandReplier : reply subject is missing.");
            return;
        }

        try {

            final var input = this.objectMapper.readValue(
                message.getData(), PostAccountingFlowCommand.Input.class);

            LOGGER.info("PostLedgerFlowCommandReplier : input: ({})", ObjectLogger.log(input));

            final var output = this.postAccountingFlowCommand.execute(input);
            final var response = Envelope.success(this.objectMapper.valueToTree(output));
            final var responseData = this.objectMapper.writeValueAsBytes(response);

            this.connection.publish(replyTo, responseData);

            LOGGER.info("PostLedgerFlowCommandReplier : output : ({})", ObjectLogger.log(output));

        } catch (final Exception exception) {

            final var output = MojaveErrorResponse.from(exception);
            final var response = Envelope.failure(this.objectMapper.valueToTree(output));
            final var responseData = this.objectMapper.writeValueAsBytes(response);

            this.connection.publish(replyTo, responseData);
        }
    }

}
