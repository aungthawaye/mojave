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

package org.mojave.core.wallet.intercom.requestor.query;

import io.nats.client.Connection;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.NatsRequestor;
import org.mojave.core.wallet.contract.data.WalletData;
import org.mojave.core.wallet.contract.exception.WalletExceptionResolver;
import org.mojave.core.wallet.contract.query.WalletQuery;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.identifier.wallet.WalletId;
import org.mojave.scheme.rule.identifier.wallet.WalletOwnerId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Objects;

@Component
public class WalletQueryRequestor implements WalletQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(WalletQueryRequestor.class);

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public WalletQueryRequestor(final Connection connection,
                                final ObjectMapper objectMapper) {

        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.connection = connection;
        this.objectMapper = objectMapper;
    }

    @Override
    public WalletData get(final WalletId walletId) {

        final var input = new GetByIdInput(walletId);

        LOGGER.info("WalletQueryRequestor.get(WalletId) : input: ({})", ObjectLogger.log(input));

        try {

            final var output = NatsRequestor.request(
                this.connection, GET_BY_ID_SUBJECT_NAME, input,
                WalletData.class, this.objectMapper);

            LOGGER.info("WalletQueryRequestor.get(WalletId) : output : ({})",
                        ObjectLogger.log(output));

            return output;

        } catch (final NatsRequestor.InvocationException exception) {

            throw resolve(exception);
        }
    }

    @Override
    public WalletData get(final WalletOwnerId ownerId,
                          final Currency currency,
                          final String tag) {

        final var input = new GetByOwnerIdCurrencyTagInput(ownerId, currency, tag);

        LOGGER.info("WalletQueryRequestor.get(WalletOwnerId, Currency, String) : input: ({})",
                    ObjectLogger.log(input));

        try {

            final var output = NatsRequestor.request(
                this.connection, GET_BY_OWNER_ID_CURRENCY_TAG_SUBJECT_NAME, input,
                WalletData.class, this.objectMapper);

            LOGGER.info("WalletQueryRequestor.get(WalletOwnerId, Currency, String) : output : ({})",
                        ObjectLogger.log(output));

            return output;

        } catch (final NatsRequestor.InvocationException exception) {

            throw resolve(exception);
        }
    }

    @Override
    public List<WalletData> get(final WalletOwnerId ownerId) {

        final var input = new GetByOwnerIdInput(ownerId);

        LOGGER.info("WalletQueryRequestor.get(WalletOwnerId) : input: ({})",
                    ObjectLogger.log(input));

        try {

            final var output = NatsRequestor.requestList(
                this.connection, GET_BY_OWNER_ID_SUBJECT_NAME, input,
                WalletData.class, this.objectMapper);

            LOGGER.info("WalletQueryRequestor.get(WalletOwnerId) : output : ({})",
                        ObjectLogger.log(output));

            return output;

        } catch (final NatsRequestor.InvocationException exception) {

            throw resolve(exception);
        }
    }

    @Override
    public List<WalletData> get(final WalletOwnerId ownerId, final Currency currency) {

        final var input = new GetByOwnerIdCurrencyInput(ownerId, currency);

        LOGGER.info("WalletQueryRequestor.get(WalletOwnerId, Currency) : input: ({})",
                    ObjectLogger.log(input));

        try {

            final var output = NatsRequestor.requestList(
                this.connection, GET_BY_OWNER_ID_CURRENCY_SUBJECT_NAME, input,
                WalletData.class, this.objectMapper);

            LOGGER.info("WalletQueryRequestor.get(WalletOwnerId, Currency) : output : ({})",
                        ObjectLogger.log(output));

            return output;

        } catch (final NatsRequestor.InvocationException exception) {

            throw resolve(exception);
        }
    }

    @Override
    public List<WalletData> getAll() {

        LOGGER.info("WalletQueryRequestor.getAll : input: ({})", ObjectLogger.log(new GetAllInput()));

        try {

            final var output = NatsRequestor.requestList(
                this.connection, GET_ALL_SUBJECT_NAME, new GetAllInput(),
                WalletData.class, this.objectMapper);

            LOGGER.info("WalletQueryRequestor.getAll : output : ({})", ObjectLogger.log(output));

            return output;

        } catch (final NatsRequestor.InvocationException exception) {

            throw resolve(exception);
        }
    }

    private static RuntimeException resolve(final NatsRequestor.InvocationException exception) {

        final var decodedErrorResponse = exception.getDecodedErrorResponse();

        if (decodedErrorResponse instanceof final MojaveErrorResponse errorResponse) {

            final var throwable = WalletExceptionResolver.resolve(errorResponse);

            if (throwable instanceof final UncheckedDomainException uncheckedDomainException) {
                throw uncheckedDomainException;
            }

            if (throwable instanceof final RuntimeException runtimeException) {
                throw runtimeException;
            }
        }

        return new RuntimeException(exception);
    }

}
