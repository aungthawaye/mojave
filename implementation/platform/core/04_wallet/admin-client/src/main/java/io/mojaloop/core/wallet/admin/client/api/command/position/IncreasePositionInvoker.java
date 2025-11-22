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

package io.mojaloop.core.wallet.admin.client.api.command.position;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.error.RestErrorResponse;
import io.mojaloop.component.misc.exception.UncheckedDomainException;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.wallet.admin.client.service.WalletAdminService;
import io.mojaloop.core.wallet.contract.command.position.IncreasePositionCommand;
import io.mojaloop.core.wallet.contract.exception.WalletExceptionResolver;
import io.mojaloop.core.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import io.mojaloop.core.wallet.contract.exception.position.PositionLimitExceededException;
import org.springframework.stereotype.Component;

@Component
public class IncreasePositionInvoker implements IncreasePositionCommand {

    private final WalletAdminService.PositionCommand positionCommand;

    private final ObjectMapper objectMapper;

    public IncreasePositionInvoker(final WalletAdminService.PositionCommand positionCommand,
                                   final ObjectMapper objectMapper) {

        assert positionCommand != null;
        assert objectMapper != null;

        this.positionCommand = positionCommand;
        this.objectMapper = objectMapper;
    }

    @Override
    public Output execute(final Input input)
        throws NoPositionUpdateForTransactionException, PositionLimitExceededException {

        try {

            return RetrofitService
                       .invoke(
                           this.positionCommand.increase(input),
                           (status, errorResponseBody) -> RestErrorResponse.decode(
                               errorResponseBody, this.objectMapper))
                       .body();

        } catch (RetrofitService.InvocationException e) {

            final var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse errorResponse) {

                final var throwable = WalletExceptionResolver.resolve(errorResponse);

                switch (throwable) {
                    case NoPositionUpdateForTransactionException e1 -> throw e1;
                    case PositionLimitExceededException e2 -> throw e2;
                    case UncheckedDomainException ude -> throw ude;
                    default -> {
                    }
                }
            }

            throw new RuntimeException(e);
        }
    }

}
