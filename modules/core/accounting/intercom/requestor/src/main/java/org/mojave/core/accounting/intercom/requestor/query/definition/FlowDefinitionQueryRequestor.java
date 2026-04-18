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

package org.mojave.core.accounting.intercom.requestor.query.definition;

import io.nats.client.Connection;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.NatsRequestor;
import org.mojave.core.accounting.contract.data.FlowDefinitionData;
import org.mojave.core.accounting.contract.exception.AccountingExceptionResolver;
import org.mojave.core.accounting.contract.query.FlowDefinitionQuery;
import org.mojave.scheme.rule.identifier.accounting.FlowDefinitionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Objects;

@Component
public class FlowDefinitionQueryRequestor implements FlowDefinitionQuery {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(FlowDefinitionQueryRequestor.class);

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public FlowDefinitionQueryRequestor(final Connection connection,
                                        final ObjectMapper objectMapper) {

        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.connection = connection;
        this.objectMapper = objectMapper;
    }

    @Override
    public FlowDefinitionData get(final FlowDefinitionId flowDefinitionId) {

        final var input = new GetByIdInput(flowDefinitionId);

        LOGGER.info("FlowDefinitionQueryRequestor.get(FlowDefinitionId) : input: ({})",
                    ObjectLogger.log(input));

        try {

            final var output = NatsRequestor.request(
                this.connection, GET_BY_ID_SUBJECT_NAME, input,
                FlowDefinitionData.class, this.objectMapper);

            LOGGER.info("FlowDefinitionQueryRequestor.get(FlowDefinitionId) : output : ({})",
                        ObjectLogger.log(output));

            return output;

        } catch (final NatsRequestor.InvocationException exception) {

            throw resolve(exception);
        }
    }

    @Override
    public List<FlowDefinitionData> getAll() {

        LOGGER.info("FlowDefinitionQueryRequestor.getAll : input: ({})",
                    ObjectLogger.log(new GetAllInput()));

        try {

            final var output = NatsRequestor.requestList(
                this.connection, GET_ALL_SUBJECT_NAME, new GetAllInput(),
                FlowDefinitionData.class, this.objectMapper);

            LOGGER.info("FlowDefinitionQueryRequestor.getAll : output : ({})",
                        ObjectLogger.log(output));

            return output;

        } catch (final NatsRequestor.InvocationException exception) {

            throw resolve(exception);
        }
    }

    @Override
    public List<FlowDefinitionData> getByNameContains(final String name) {

        final var input = new GetByNameContainsInput(name);

        LOGGER.info("FlowDefinitionQueryRequestor.getByNameContains : input: ({})",
                    ObjectLogger.log(input));

        try {

            final var output = NatsRequestor.requestList(
                this.connection, GET_BY_NAME_CONTAINS_SUBJECT_NAME, input,
                FlowDefinitionData.class, this.objectMapper);

            LOGGER.info("FlowDefinitionQueryRequestor.getByNameContains : output : ({})",
                        ObjectLogger.log(output));

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
