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

package org.mojave.accounting.intercom.requestor.command.ledger;

import io.nats.client.Connection;
import org.mojave.accounting.contract.command.ledger.PostAccountingFlowCommand;
import org.mojave.accounting.contract.exception.AccountingExceptionResolver;
import org.mojave.accounting.contract.exception.ledger.DuplicatePostingInLedgerException;
import org.mojave.accounting.contract.exception.ledger.InsufficientBalanceInAccountException;
import org.mojave.accounting.contract.exception.ledger.OverdraftLimitReachedInAccountException;
import org.mojave.accounting.contract.exception.ledger.PostingAccountNotFoundException;
import org.mojave.accounting.contract.exception.ledger.RestoreFailedInAccountException;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.CommandRequestor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class PostAccountingFlowCommandRequestor implements PostAccountingFlowCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostAccountingFlowCommandRequestor.class);

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public PostAccountingFlowCommandRequestor(final Connection connection,
                                       final ObjectMapper objectMapper) {

        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.connection = connection;
        this.objectMapper = objectMapper;
    }

    @Override
    public Output execute(final Input input)
        throws InsufficientBalanceInAccountException,
               OverdraftLimitReachedInAccountException,
               DuplicatePostingInLedgerException,
               PostingAccountNotFoundException,
               RestoreFailedInAccountException {

        LOGGER.info("PostAccountingFlowCommandRequestor : input: ({})", ObjectLogger.log(input));

        try {

            final var output = CommandRequestor.request(
                this.connection, PostAccountingFlowCommand.SUBJECT_NAME,
                input, Output.class, this.objectMapper);

            LOGGER.info("PostAccountingFlowCommandRequestor : output : ({})", ObjectLogger.log(output));

            return output;

        } catch (final CommandRequestor.InvocationException exception) {

            final var decodedErrorResponse = exception.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof final MojaveErrorResponse errorResponse) {

                final var throwable = AccountingExceptionResolver.resolve(errorResponse);

                if (throwable instanceof final InsufficientBalanceInAccountException insufficientBalanceInAccountException) {
                    throw insufficientBalanceInAccountException;
                }

                if (throwable instanceof final OverdraftLimitReachedInAccountException overdraftLimitReachedInAccountException) {
                    throw overdraftLimitReachedInAccountException;
                }

                if (throwable instanceof final DuplicatePostingInLedgerException duplicatePostingInLedgerException) {
                    throw duplicatePostingInLedgerException;
                }

                if (throwable instanceof final PostingAccountNotFoundException postingAccountNotFoundException) {
                    throw postingAccountNotFoundException;
                }

                if (throwable instanceof final RestoreFailedInAccountException restoreFailedInAccountException) {
                    throw restoreFailedInAccountException;
                }

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
