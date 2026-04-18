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

package org.mojave.core.accounting.intercom.replier.query.definition;

import io.nats.client.Connection;
import io.nats.client.Message;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.Envelope;
import org.mojave.core.accounting.contract.query.FlowDefinitionQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class FlowDefinitionQueryReplier {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(FlowDefinitionQueryReplier.class);

    private final FlowDefinitionQuery flowDefinitionQuery;

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public FlowDefinitionQueryReplier(final FlowDefinitionQuery flowDefinitionQuery,
                                      final Connection connection,
                                      final ObjectMapper objectMapper) {

        Objects.requireNonNull(flowDefinitionQuery);
        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.flowDefinitionQuery = flowDefinitionQuery;
        this.connection = connection;
        this.objectMapper = objectMapper;

        this.connection.createDispatcher(this::handleGetById)
            .subscribe(FlowDefinitionQuery.GET_BY_ID_SUBJECT_NAME);
        this.connection.createDispatcher(this::handleGetAll)
            .subscribe(FlowDefinitionQuery.GET_ALL_SUBJECT_NAME);
        this.connection.createDispatcher(this::handleGetByNameContains)
            .subscribe(FlowDefinitionQuery.GET_BY_NAME_CONTAINS_SUBJECT_NAME);
    }

    private void handleGetById(final Message message) {

        this.reply(message, FlowDefinitionQuery.GetByIdInput.class, input -> {
            LOGGER.info("FlowDefinitionQueryReplier.get(FlowDefinitionId) : input: ({})",
                        ObjectLogger.log(input));
            final var output = this.flowDefinitionQuery.get(input.flowDefinitionId());
            LOGGER.info("FlowDefinitionQueryReplier.get(FlowDefinitionId) : output : ({})",
                        ObjectLogger.log(output));
            return output;
        });
    }

    private void handleGetAll(final Message message) {

        this.reply(message, FlowDefinitionQuery.GetAllInput.class, input -> {
            LOGGER.info("FlowDefinitionQueryReplier.getAll : input: ({})", ObjectLogger.log(input));
            final var output = this.flowDefinitionQuery.getAll();
            LOGGER.info("FlowDefinitionQueryReplier.getAll : output : ({})", ObjectLogger.log(output));
            return output;
        });
    }

    private void handleGetByNameContains(final Message message) {

        this.reply(message, FlowDefinitionQuery.GetByNameContainsInput.class, input -> {
            LOGGER.info("FlowDefinitionQueryReplier.getByNameContains : input: ({})",
                        ObjectLogger.log(input));
            final var output = this.flowDefinitionQuery.getByNameContains(input.name());
            LOGGER.info("FlowDefinitionQueryReplier.getByNameContains : output : ({})",
                        ObjectLogger.log(output));
            return output;
        });
    }

    private <I> void reply(final Message message,
                           final Class<I> inputType,
                           final Handler<I> handler) {

        final var replyTo = message.getReplyTo();

        if (replyTo == null || replyTo.isBlank()) {
            LOGGER.warn("FlowDefinitionQueryReplier : reply subject is missing.");
            return;
        }

        try {

            final var input = this.objectMapper.readValue(message.getData(), inputType);
            final var output = handler.handle(input);
            final var response = Envelope.success(this.objectMapper.valueToTree(output));
            final var responseData = this.objectMapper.writeValueAsBytes(response);

            this.connection.publish(replyTo, responseData);

        } catch (final Exception exception) {

            final var output = MojaveErrorResponse.from(exception);
            final var response = Envelope.failure(this.objectMapper.valueToTree(output));
            final var responseData = this.objectMapper.writeValueAsBytes(response);

            this.connection.publish(replyTo, responseData);
        }
    }

    @FunctionalInterface
    private interface Handler<I> {

        Object handle(I input) throws Exception;

    }

}
