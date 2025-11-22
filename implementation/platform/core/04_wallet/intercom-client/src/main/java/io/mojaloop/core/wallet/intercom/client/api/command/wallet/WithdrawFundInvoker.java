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

package io.mojaloop.core.wallet.intercom.client.api.command.wallet;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.error.RestErrorResponse;
import io.mojaloop.component.misc.exception.UncheckedDomainException;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.wallet.contract.command.wallet.WithdrawFundCommand;
import io.mojaloop.core.wallet.contract.exception.WalletExceptionResolver;
import io.mojaloop.core.wallet.contract.exception.wallet.InsufficientBalanceInWalletException;
import io.mojaloop.core.wallet.contract.exception.wallet.NoBalanceUpdateForTransactionException;
import io.mojaloop.core.wallet.intercom.client.service.WalletIntercomService;
import org.springframework.stereotype.Component;

@Component
public class WithdrawFundInvoker implements WithdrawFundCommand {

    private final WalletIntercomService.WalletCommand walletCommand;

    private final ObjectMapper objectMapper;

    public WithdrawFundInvoker(final WalletIntercomService.WalletCommand walletCommand,
                               final ObjectMapper objectMapper) {

        assert walletCommand != null;
        assert objectMapper != null;

        this.walletCommand = walletCommand;
        this.objectMapper = objectMapper;
    }

    @Override
    public Output execute(final Input input)
        throws NoBalanceUpdateForTransactionException, InsufficientBalanceInWalletException {

        try {

            return RetrofitService
                       .invoke(
                           this.walletCommand.withdrawFund(input),
                           (status, errorResponseBody) -> RestErrorResponse.decode(
                               errorResponseBody, this.objectMapper))
                       .body();

        } catch (RetrofitService.InvocationException e) {

            final var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse errorResponse) {

                final var throwable = WalletExceptionResolver.resolve(errorResponse);

                switch (throwable) {
                    case NoBalanceUpdateForTransactionException e1 -> throw e1;
                    case InsufficientBalanceInWalletException e2 -> throw e2;
                    case UncheckedDomainException ude -> throw ude;
                    default -> {
                    }
                }
            }

            throw new RuntimeException(e);
        }
    }

}
