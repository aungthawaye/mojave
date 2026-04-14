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

package org.mojave.core.wallet.intercom.replier.query;

import io.nats.client.Connection;
import io.nats.client.Message;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.Envelope;
import org.mojave.core.wallet.contract.query.WalletQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class WalletQueryReplier {

    private static final Logger LOGGER = LoggerFactory.getLogger(WalletQueryReplier.class);

    private final WalletQuery walletQuery;

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public WalletQueryReplier(final WalletQuery walletQuery, final Connection connection,
                              final ObjectMapper objectMapper) {

        Objects.requireNonNull(walletQuery);
        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.walletQuery = walletQuery;
        this.connection = connection;
        this.objectMapper = objectMapper;

        this.connection
            .createDispatcher(this::handleGetById)
            .subscribe(WalletQuery.GET_BY_ID_SUBJECT_NAME);
        this.connection
            .createDispatcher(this::handleGetByOwnerIdCurrencyTag)
            .subscribe(WalletQuery.GET_BY_OWNER_ID_CURRENCY_TAG_SUBJECT_NAME);
        this.connection
            .createDispatcher(this::handleGetByOwnerId)
            .subscribe(WalletQuery.GET_BY_OWNER_ID_SUBJECT_NAME);
        this.connection
            .createDispatcher(this::handleGetByOwnerIdCurrency)
            .subscribe(WalletQuery.GET_BY_OWNER_ID_CURRENCY_SUBJECT_NAME);
        this.connection
            .createDispatcher(this::handleGetAll)
            .subscribe(WalletQuery.GET_ALL_SUBJECT_NAME);
    }

    private void handleGetAll(final Message message) {

        this.reply(
            message, WalletQuery.GetAllInput.class, input -> {
                LOGGER.info("WalletQueryReplier.getAll : input: ({})", ObjectLogger.log(input));
                final var output = this.walletQuery.getAll();
                LOGGER.info("WalletQueryReplier.getAll : output : ({})", ObjectLogger.log(output));
                return output;
            });
    }

    private void handleGetById(final Message message) {

        this.reply(
            message, WalletQuery.GetByIdInput.class, input -> {
                LOGGER.info(
                    "WalletQueryReplier.get(WalletId) : input: ({})", ObjectLogger.log(input));
                final var output = this.walletQuery.get(input.walletId());
                LOGGER.info(
                    "WalletQueryReplier.get(WalletId) : output : ({})",
                    ObjectLogger.log(output));
                return output;
            });
    }

    private void handleGetByOwnerId(final Message message) {

        this.reply(
            message, WalletQuery.GetByOwnerIdInput.class, input -> {
                LOGGER.info(
                    "WalletQueryReplier.get(WalletOwnerId) : input: ({})",
                    ObjectLogger.log(input));
                final var output = this.walletQuery.get(input.ownerId());
                LOGGER.info(
                    "WalletQueryReplier.get(WalletOwnerId) : output : ({})",
                    ObjectLogger.log(output));
                return output;
            });
    }

    private void handleGetByOwnerIdCurrency(final Message message) {

        this.reply(
            message, WalletQuery.GetByOwnerIdCurrencyInput.class, input -> {
                LOGGER.info(
                    "WalletQueryReplier.get(WalletOwnerId, Currency) : input: ({})",
                    ObjectLogger.log(input));
                final var output = this.walletQuery.get(input.ownerId(), input.currency());
                LOGGER.info(
                    "WalletQueryReplier.get(WalletOwnerId, Currency) : output : ({})",
                    ObjectLogger.log(output));
                return output;
            });
    }

    private void handleGetByOwnerIdCurrencyTag(final Message message) {

        this.reply(
            message, WalletQuery.GetByOwnerIdCurrencyTagInput.class, input -> {
                LOGGER.info(
                    "WalletQueryReplier.get(WalletOwnerId, Currency, String) : input: ({})",
                    ObjectLogger.log(input));
                final var output = this.walletQuery.get(
                    input.ownerId(), input.currency(), input.tag());
                LOGGER.info(
                    "WalletQueryReplier.get(WalletOwnerId, Currency, String) : output : ({})",
                    ObjectLogger.log(output));
                return output;
            });
    }

    private <I> void reply(final Message message, final Class<I> inputType,
                           final Handler<I> handler) {

        final var replyTo = message.getReplyTo();

        if (replyTo == null || replyTo.isBlank()) {
            LOGGER.warn("WalletQueryReplier : reply subject is missing.");
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
