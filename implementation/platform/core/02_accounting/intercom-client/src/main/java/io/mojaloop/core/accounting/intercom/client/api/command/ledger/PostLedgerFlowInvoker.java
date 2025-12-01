/*-
 * ================================================================================
 * Mojave
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
 * ================================================================================
 */

package io.mojaloop.core.accounting.intercom.client.api.command.ledger;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.error.RestErrorResponse;
import io.mojaloop.component.misc.exception.UncheckedDomainException;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.accounting.contract.command.ledger.PostLedgerFlowCommand;
import io.mojaloop.core.accounting.contract.exception.AccountingExceptionResolver;
import io.mojaloop.core.accounting.contract.exception.ledger.DuplicatePostingInLedgerException;
import io.mojaloop.core.accounting.contract.exception.ledger.InsufficientBalanceInAccountException;
import io.mojaloop.core.accounting.contract.exception.ledger.OverdraftLimitReachedInAccountException;
import io.mojaloop.core.accounting.contract.exception.ledger.RestoreFailedInAccountException;
import io.mojaloop.core.accounting.intercom.client.service.AccountingIntercomService;
import org.springframework.stereotype.Component;

@Component
public class PostLedgerFlowInvoker implements PostLedgerFlowCommand {

    private final AccountingIntercomService.LedgerCommand ledgerCommand;

    private final ObjectMapper objectMapper;

    public PostLedgerFlowInvoker(final AccountingIntercomService.LedgerCommand ledgerCommand,
                                 final ObjectMapper objectMapper) {

        assert ledgerCommand != null;
        assert objectMapper != null;

        this.ledgerCommand = ledgerCommand;
        this.objectMapper = objectMapper;
    }

    @Override
    public Output execute(Input input) throws
                                       InsufficientBalanceInAccountException,
                                       OverdraftLimitReachedInAccountException,
                                       DuplicatePostingInLedgerException,
                                       RestoreFailedInAccountException {

        try {

            return RetrofitService
                       .invoke(
                           this.ledgerCommand.postLedgerFlow(input),
                           (status, errorResponseBody) -> RestErrorResponse.decode(
                               errorResponseBody, this.objectMapper))
                       .body();

        } catch (RetrofitService.InvocationException e) {

            var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse errorResponse) {

                var throwable = AccountingExceptionResolver.resolve(errorResponse);

                switch (throwable) {
                    case InsufficientBalanceInAccountException e1 -> throw e1;
                    case OverdraftLimitReachedInAccountException e2 -> throw e2;
                    case DuplicatePostingInLedgerException e3 -> throw e3;
                    case RestoreFailedInAccountException e4 -> throw e4;
                    case UncheckedDomainException ude -> throw ude;
                    default -> throw new RuntimeException(e);
                }
            }

            throw new RuntimeException(e);
        }
    }

}
