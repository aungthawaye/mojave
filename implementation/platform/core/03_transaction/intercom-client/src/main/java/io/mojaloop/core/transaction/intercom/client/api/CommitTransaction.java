/*-
 * ==============================================================================
 * Mojaloop OSS
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
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
 * ==============================================================================
 */

package io.mojaloop.core.transaction.intercom.client.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.error.RestErrorResponse;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.transaction.contract.command.CommitTransactionCommand;
import io.mojaloop.core.transaction.intercom.client.exception.TransactionIntercomClientException;
import io.mojaloop.core.transaction.intercom.client.service.TransactionIntercomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CommitTransaction {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommitTransaction.class);

    private final TransactionIntercomService transactionIntercomService;

    private final ObjectMapper objectMapper;

    public CommitTransaction(final TransactionIntercomService transactionIntercomService, final ObjectMapper objectMapper) {

        assert transactionIntercomService != null;
        assert objectMapper != null;

        this.transactionIntercomService = transactionIntercomService;
        this.objectMapper = objectMapper;
    }

    public CommitTransactionCommand.Output execute(final CommitTransactionCommand.Input input) throws TransactionIntercomClientException {

        try {

            return RetrofitService.invoke(this.transactionIntercomService.commit(input),
                (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            LOGGER.error("Error invoking commit transaction : {}", e.getMessage());

            final var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse(String code, String message)) {

                throw new TransactionIntercomClientException(code, message);
            }

            throw new TransactionIntercomClientException("INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }
}
