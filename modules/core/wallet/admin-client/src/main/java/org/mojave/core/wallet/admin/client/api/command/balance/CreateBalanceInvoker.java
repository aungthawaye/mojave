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
package org.mojave.core.wallet.admin.client.api.command.balance;

import org.mojave.component.misc.error.RestErrorResponse;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.component.retrofit.RetrofitService;
import org.mojave.core.wallet.admin.client.service.WalletAdminService;
import org.mojave.core.wallet.contract.command.balance.CreateBalanceCommand;
import org.mojave.core.wallet.contract.exception.WalletExceptionResolver;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import java.util.Objects;

@Component
public class CreateBalanceInvoker implements CreateBalanceCommand {

    private final WalletAdminService.BalanceCommand balanceCommand;

    private final ObjectMapper objectMapper;

    public CreateBalanceInvoker(final WalletAdminService.BalanceCommand balanceCommand,
                                final ObjectMapper objectMapper) {

        Objects.requireNonNull(balanceCommand);
        Objects.requireNonNull(objectMapper);

        this.balanceCommand = balanceCommand;
        this.objectMapper = objectMapper;
    }

    @Override
    public Output execute(final Input input) {

        try {

            return RetrofitService.invoke(
                this.balanceCommand.create(input),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            final var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse errorResponse) {

                final var throwable = WalletExceptionResolver.resolve(errorResponse);

                if (throwable instanceof UncheckedDomainException ude) {
                    throw ude;
                }
            }

            throw new RuntimeException(e);
        }
    }

}
