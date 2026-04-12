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

package org.mojave.core.accounting.intercom.requestor.query.account;

import io.nats.client.Connection;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.misc.query.PagedResult;
import org.mojave.component.nats.NatsRequestor;
import org.mojave.core.accounting.contract.data.AccountData;
import org.mojave.core.accounting.contract.exception.AccountingExceptionResolver;
import org.mojave.core.accounting.contract.query.AccountQuery;
import org.mojave.scheme.rule.identifier.accounting.AccountId;
import org.mojave.scheme.rule.identifier.accounting.AccountOwnerId;
import org.mojave.scheme.rule.type.accounting.AccountCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Objects;

@Component
public class AccountQueryRequestor implements AccountQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountQueryRequestor.class);

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public AccountQueryRequestor(final Connection connection,
                                 final ObjectMapper objectMapper) {

        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.connection = connection;
        this.objectMapper = objectMapper;
    }

    @Override
    public PagedResult<AccountData> find(final Criteria criteria) {

        final var input = new FindInput(criteria);

        LOGGER.info("AccountQueryRequestor.find : input: ({})", ObjectLogger.log(input));

        try {

            final var outputType =
                this.objectMapper.getTypeFactory().constructParametricType(
                    PagedResult.class, AccountData.class);
            final var output = NatsRequestor.<FindInput, PagedResult<AccountData>>request(
                this.connection, FIND_SUBJECT_NAME, input, outputType, this.objectMapper);

            LOGGER.info("AccountQueryRequestor.find : output : ({})", ObjectLogger.log(output));

            return output;

        } catch (final NatsRequestor.InvocationException exception) {

            throw resolve(exception);
        }
    }

    @Override
    public AccountData get(final AccountCode accountCode) {

        final var input = new GetByCodeInput(accountCode);

        LOGGER.info("AccountQueryRequestor.get(AccountCode) : input: ({})", ObjectLogger.log(input));

        try {

            final var output = NatsRequestor.request(
                this.connection, GET_BY_CODE_SUBJECT_NAME, input,
                AccountData.class, this.objectMapper);

            LOGGER.info("AccountQueryRequestor.get(AccountCode) : output : ({})",
                        ObjectLogger.log(output));

            return output;

        } catch (final NatsRequestor.InvocationException exception) {

            throw resolve(exception);
        }
    }

    @Override
    public List<AccountData> get(final AccountOwnerId ownerId) {

        final var input = new GetByOwnerIdInput(ownerId);

        LOGGER.info(
            "AccountQueryRequestor.get(AccountOwnerId) : input: ({})", ObjectLogger.log(input));

        try {

            final var output = NatsRequestor.requestList(
                this.connection, GET_BY_OWNER_ID_SUBJECT_NAME, input,
                AccountData.class, this.objectMapper);

            LOGGER.info("AccountQueryRequestor.get(AccountOwnerId) : output : ({})",
                        ObjectLogger.log(output));

            return output;

        } catch (final NatsRequestor.InvocationException exception) {

            throw resolve(exception);
        }
    }

    @Override
    public AccountData get(final AccountId accountId) {

        final var input = new GetByIdInput(accountId);

        LOGGER.info("AccountQueryRequestor.get(AccountId) : input: ({})", ObjectLogger.log(input));

        try {

            final var output = NatsRequestor.request(
                this.connection, GET_BY_ID_SUBJECT_NAME, input,
                AccountData.class, this.objectMapper);

            LOGGER.info("AccountQueryRequestor.get(AccountId) : output : ({})",
                        ObjectLogger.log(output));

            return output;

        } catch (final NatsRequestor.InvocationException exception) {

            throw resolve(exception);
        }
    }

    @Override
    public List<AccountData> getAll() {

        LOGGER.info("AccountQueryRequestor.getAll : input: ({})", ObjectLogger.log(new GetAllInput()));

        try {

            final var output = NatsRequestor.requestList(
                this.connection, GET_ALL_SUBJECT_NAME, new GetAllInput(),
                AccountData.class, this.objectMapper);

            LOGGER.info("AccountQueryRequestor.getAll : output : ({})", ObjectLogger.log(output));

            return output;

        } catch (final NatsRequestor.InvocationException exception) {

            throw resolve(exception);
        }
    }

    private static RuntimeException resolve(final NatsRequestor.InvocationException exception) {

        final var decodedErrorResponse = exception.getDecodedErrorResponse();

        if (decodedErrorResponse instanceof final MojaveErrorResponse errorResponse) {

            final var throwable = AccountingExceptionResolver.resolve(errorResponse);

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
