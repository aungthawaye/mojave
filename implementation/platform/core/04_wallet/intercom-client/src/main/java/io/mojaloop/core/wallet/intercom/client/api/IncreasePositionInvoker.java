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

package io.mojaloop.core.wallet.intercom.client.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.error.RestErrorResponse;
import io.mojaloop.component.misc.exception.UncheckedDomainException;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.wallet.contract.command.position.IncreasePositionCommand;
import io.mojaloop.core.wallet.contract.exception.WalletExceptionResolver;
import io.mojaloop.core.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import io.mojaloop.core.wallet.contract.exception.position.PositionLimitExceededException;
import io.mojaloop.core.wallet.intercom.client.exception.WalletIntercomClientException;
import io.mojaloop.core.wallet.intercom.client.service.WalletIntercomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class IncreasePositionInvoker implements IncreasePositionCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(IncreasePositionInvoker.class);

    private final WalletIntercomService walletIntercomService;

    private final ObjectMapper objectMapper;

    public IncreasePositionInvoker(final WalletIntercomService walletIntercomService, final ObjectMapper objectMapper) {

        assert walletIntercomService != null;
        assert objectMapper != null;

        this.walletIntercomService = walletIntercomService;
        this.objectMapper = objectMapper;
    }

    public IncreasePositionCommand.Output execute(final IncreasePositionCommand.Input input) throws NoPositionUpdateForTransactionException, PositionLimitExceededException {

        try {

            return RetrofitService.invoke(this.walletIntercomService.increasePosition(input),
                (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            LOGGER.error("Error invoking increasePosition : {}", e.getMessage());

            var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse errorResponse) {

                switch (WalletExceptionResolver.resolve(errorResponse)) {
                    case UncheckedDomainException ude -> throw ude;
                    case NoPositionUpdateForTransactionException e1 -> throw e1;
                    case PositionLimitExceededException e2 -> throw e2;
                    default -> {
                    }
                }
            }

            throw new RuntimeException(e);
        }
    }

}
