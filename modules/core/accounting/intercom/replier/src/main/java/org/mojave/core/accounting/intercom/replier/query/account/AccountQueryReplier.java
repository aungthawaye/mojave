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

package org.mojave.core.accounting.intercom.replier.query.account;

import io.nats.client.Connection;
import io.nats.client.Message;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.Envelope;
import org.mojave.core.accounting.contract.query.AccountQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class AccountQueryReplier {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountQueryReplier.class);

    private final AccountQuery accountQuery;

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public AccountQueryReplier(final AccountQuery accountQuery,
                               final Connection connection,
                               final ObjectMapper objectMapper) {

        Objects.requireNonNull(accountQuery);
        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.accountQuery = accountQuery;
        this.connection = connection;
        this.objectMapper = objectMapper;

        this.connection.createDispatcher(this::handleFind).subscribe(AccountQuery.FIND_SUBJECT_NAME);
        this.connection.createDispatcher(this::handleGetByCode)
            .subscribe(AccountQuery.GET_BY_CODE_SUBJECT_NAME);
        this.connection.createDispatcher(this::handleGetByOwnerId)
            .subscribe(AccountQuery.GET_BY_OWNER_ID_SUBJECT_NAME);
        this.connection.createDispatcher(this::handleGetById)
            .subscribe(AccountQuery.GET_BY_ID_SUBJECT_NAME);
        this.connection.createDispatcher(this::handleGetAll).subscribe(AccountQuery.GET_ALL_SUBJECT_NAME);
    }

    private void handleFind(final Message message) {

        this.reply(message, AccountQuery.FindInput.class, input -> {
            LOGGER.info("AccountQueryReplier.find : input: ({})", ObjectLogger.log(input));
            final var output = this.accountQuery.find(input.criteria());
            LOGGER.info("AccountQueryReplier.find : output : ({})", ObjectLogger.log(output));
            return output;
        });
    }

    private void handleGetByCode(final Message message) {

        this.reply(message, AccountQuery.GetByCodeInput.class, input -> {
            LOGGER.info("AccountQueryReplier.get(AccountCode) : input: ({})", ObjectLogger.log(input));
            final var output = this.accountQuery.get(input.accountCode());
            LOGGER.info("AccountQueryReplier.get(AccountCode) : output : ({})",
                        ObjectLogger.log(output));
            return output;
        });
    }

    private void handleGetByOwnerId(final Message message) {

        this.reply(message, AccountQuery.GetByOwnerIdInput.class, input -> {
            LOGGER.info("AccountQueryReplier.get(AccountOwnerId) : input: ({})",
                        ObjectLogger.log(input));
            final var output = this.accountQuery.get(input.ownerId());
            LOGGER.info("AccountQueryReplier.get(AccountOwnerId) : output : ({})",
                        ObjectLogger.log(output));
            return output;
        });
    }

    private void handleGetById(final Message message) {

        this.reply(message, AccountQuery.GetByIdInput.class, input -> {
            LOGGER.info("AccountQueryReplier.get(AccountId) : input: ({})", ObjectLogger.log(input));
            final var output = this.accountQuery.get(input.accountId());
            LOGGER.info("AccountQueryReplier.get(AccountId) : output : ({})",
                        ObjectLogger.log(output));
            return output;
        });
    }

    private void handleGetAll(final Message message) {

        this.reply(message, AccountQuery.GetAllInput.class, input -> {
            LOGGER.info("AccountQueryReplier.getAll : input: ({})", ObjectLogger.log(input));
            final var output = this.accountQuery.getAll();
            LOGGER.info("AccountQueryReplier.getAll : output : ({})", ObjectLogger.log(output));
            return output;
        });
    }

    private <I> void reply(final Message message,
                           final Class<I> inputType,
                           final Handler<I> handler) {

        final var replyTo = message.getReplyTo();

        if (replyTo == null || replyTo.isBlank()) {
            LOGGER.warn("AccountQueryReplier : reply subject is missing.");
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
