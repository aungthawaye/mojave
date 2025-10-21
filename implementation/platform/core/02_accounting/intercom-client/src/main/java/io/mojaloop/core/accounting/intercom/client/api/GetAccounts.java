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

package io.mojaloop.core.accounting.intercom.client.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.error.RestErrorResponse;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.accounting.contract.data.AccountData;
import io.mojaloop.core.accounting.intercom.client.exception.AccountingIntercomClientException;
import io.mojaloop.core.accounting.intercom.client.service.AccountingIntercomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetAccounts {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetAccounts.class);

    private final AccountingIntercomService accountingIntercomService;

    private final ObjectMapper objectMapper;

    public GetAccounts(AccountingIntercomService accountingIntercomService, ObjectMapper objectMapper) {

        assert accountingIntercomService != null;
        assert objectMapper != null;

        this.accountingIntercomService = accountingIntercomService;
        this.objectMapper = objectMapper;
    }

    public List<AccountData> execute() throws AccountingIntercomClientException {

        try {

            return RetrofitService.invoke(this.accountingIntercomService.getAccounts(),
                                          (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            LOGGER.error("Error invoking getAccounts : {}", e.getMessage());

            var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse(String code, String message)) {

                throw new AccountingIntercomClientException(code, message);
            }

            throw new AccountingIntercomClientException("INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

}
