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

package org.mojave.core.accounting.intercom.replier.query.chart;

import io.nats.client.Connection;
import io.nats.client.Message;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.Envelope;
import org.mojave.core.accounting.contract.query.CoaEntryQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class CoaEntryQueryReplier {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoaEntryQueryReplier.class);

    private final CoaEntryQuery coaEntryQuery;

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public CoaEntryQueryReplier(final CoaEntryQuery coaEntryQuery,
                                final Connection connection,
                                final ObjectMapper objectMapper) {

        Objects.requireNonNull(coaEntryQuery);
        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.coaEntryQuery = coaEntryQuery;
        this.connection = connection;
        this.objectMapper = objectMapper;

        this.connection.createDispatcher(this::handleGetById)
            .subscribe(CoaEntryQuery.GET_BY_ID_SUBJECT_NAME);
        this.connection.createDispatcher(this::handleGetByCoaId)
            .subscribe(CoaEntryQuery.GET_BY_COA_ID_SUBJECT_NAME);
        this.connection.createDispatcher(this::handleGetByCategory)
            .subscribe(CoaEntryQuery.GET_BY_CATEGORY_SUBJECT_NAME);
        this.connection.createDispatcher(this::handleGetAll)
            .subscribe(CoaEntryQuery.GET_ALL_SUBJECT_NAME);
    }

    private void handleGetById(final Message message) {

        this.reply(message, CoaEntryQuery.GetByIdInput.class, input -> {
            LOGGER.info("CoaEntryQueryReplier.get(CoaEntryId) : input: ({})",
                        ObjectLogger.log(input));
            final var output = this.coaEntryQuery.get(input.coaEntryId());
            LOGGER.info("CoaEntryQueryReplier.get(CoaEntryId) : output : ({})",
                        ObjectLogger.log(output));
            return output;
        });
    }

    private void handleGetByCoaId(final Message message) {

        this.reply(message, CoaEntryQuery.GetByCoaIdInput.class, input -> {
            LOGGER.info("CoaEntryQueryReplier.get(CoaId) : input: ({})", ObjectLogger.log(input));
            final var output = this.coaEntryQuery.get(input.coaId());
            LOGGER.info("CoaEntryQueryReplier.get(CoaId) : output : ({})",
                        ObjectLogger.log(output));
            return output;
        });
    }

    private void handleGetByCategory(final Message message) {

        this.reply(message, CoaEntryQuery.GetByCategoryInput.class, input -> {
            LOGGER.info("CoaEntryQueryReplier.get(String) : input: ({})", ObjectLogger.log(input));
            final var output = this.coaEntryQuery.get(input.category());
            LOGGER.info("CoaEntryQueryReplier.get(String) : output : ({})",
                        ObjectLogger.log(output));
            return output;
        });
    }

    private void handleGetAll(final Message message) {

        this.reply(message, CoaEntryQuery.GetAllInput.class, input -> {
            LOGGER.info("CoaEntryQueryReplier.getAll : input: ({})", ObjectLogger.log(input));
            final var output = this.coaEntryQuery.getAll();
            LOGGER.info("CoaEntryQueryReplier.getAll : output : ({})", ObjectLogger.log(output));
            return output;
        });
    }

    private <I> void reply(final Message message,
                           final Class<I> inputType,
                           final Handler<I> handler) {

        final var replyTo = message.getReplyTo();

        if (replyTo == null || replyTo.isBlank()) {
            LOGGER.warn("CoaEntryQueryReplier : reply subject is missing.");
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
