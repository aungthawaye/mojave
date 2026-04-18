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

package org.mojave.core.accounting.intercom.requestor.command.account;

import io.nats.client.Connection;
import org.mojave.core.accounting.contract.command.account.TerminateAccountCommand;
import org.mojave.core.accounting.contract.exception.AccountingExceptionResolver;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.NatsRequestor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class TerminateAccountCommandRequestor implements TerminateAccountCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminateAccountCommandRequestor.class);

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public TerminateAccountCommandRequestor(final Connection connection,
                                     final ObjectMapper objectMapper) {

        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.connection = connection;
        this.objectMapper = objectMapper;
    }

    @Override
    public Output execute(final Input input) {

        LOGGER.info("TerminateAccountCommandRequestor : input: ({})", ObjectLogger.log(input));

        try {

            final var output = NatsRequestor.request(
                this.connection, TerminateAccountCommand.SUBJECT_NAME,
                input, Output.class, this.objectMapper);

            LOGGER.info("TerminateAccountCommandRequestor : output : ({})", ObjectLogger.log(output));

            return output;

        } catch (final NatsRequestor.InvocationException exception) {

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

            throw new RuntimeException(exception);
        }
    }

}
