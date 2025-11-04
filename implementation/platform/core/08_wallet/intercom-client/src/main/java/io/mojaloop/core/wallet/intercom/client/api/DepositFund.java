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
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.wallet.contract.command.wallet.DepositFundCommand;
import io.mojaloop.core.wallet.intercom.client.exception.WalletIntercomClientException;
import io.mojaloop.core.wallet.intercom.client.service.WalletIntercomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DepositFund {

    private static final Logger LOGGER = LoggerFactory.getLogger(DepositFund.class);

    private final WalletIntercomService walletIntercomService;

    private final ObjectMapper objectMapper;

    public DepositFund(final WalletIntercomService walletIntercomService, final ObjectMapper objectMapper) {

        assert walletIntercomService != null;
        assert objectMapper != null;

        this.walletIntercomService = walletIntercomService;
        this.objectMapper = objectMapper;
    }

    public DepositFundCommand.Output execute(final DepositFundCommand.Input input) throws WalletIntercomClientException {

        try {

            return RetrofitService.invoke(this.walletIntercomService.depositFund(input),
                                          (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            LOGGER.error("Error invoking depositFund : {}", e.getMessage());

            var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse(String code, String message)) {

                throw new WalletIntercomClientException(code, message);
            }

            throw new WalletIntercomClientException("INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }
}
