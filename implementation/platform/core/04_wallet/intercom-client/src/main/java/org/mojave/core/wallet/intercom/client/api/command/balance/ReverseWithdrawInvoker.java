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
package org.mojave.core.wallet.intercom.client.api.command.balance;

import org.mojave.component.misc.error.RestErrorResponse;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.component.retrofit.RetrofitService;
import org.mojave.core.wallet.contract.command.balance.ReverseWithdrawCommand;
import org.mojave.core.wallet.contract.exception.WalletExceptionResolver;
import org.mojave.core.wallet.contract.exception.balance.ReversalFailedInWalletException;
import org.mojave.core.wallet.intercom.client.service.WalletIntercomService;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class ReverseWithdrawInvoker implements ReverseWithdrawCommand {

    private final WalletIntercomService.BalanceCommand balanceCommand;

    private final ObjectMapper objectMapper;

    public ReverseWithdrawInvoker(final WalletIntercomService.BalanceCommand balanceCommand,
                                  final ObjectMapper objectMapper) {

        assert balanceCommand != null;
        assert objectMapper != null;

        this.balanceCommand = balanceCommand;
        this.objectMapper = objectMapper;
    }

    @Override
    public Output execute(final Input input) throws ReversalFailedInWalletException {

        try {

            return RetrofitService.invoke(
                this.balanceCommand.reverseWithdraw(input),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            final var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse errorResponse) {

                final var throwable = WalletExceptionResolver.resolve(errorResponse);

                switch (throwable) {
                    case ReversalFailedInWalletException e1 -> throw e1;
                    case UncheckedDomainException ude -> throw ude;
                    default -> {
                    }
                }
            }

            throw new RuntimeException(e);
        }
    }

}
