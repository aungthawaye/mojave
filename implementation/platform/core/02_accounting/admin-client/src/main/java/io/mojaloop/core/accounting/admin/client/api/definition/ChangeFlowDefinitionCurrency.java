/*-
 * =============================================================================
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
 * =============================================================================
 */
/*-
 * ==============================================================================
 * Mojaloop OSS
 * -----------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * -----------------------------------------------------------------------------
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

package io.mojaloop.core.accounting.admin.client.api.definition;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.error.RestErrorResponse;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.accounting.admin.client.exception.AccountingAdminClientException;
import io.mojaloop.core.accounting.admin.client.service.AccountingAdminService;
import io.mojaloop.core.accounting.contract.command.definition.ChangeFlowDefinitionCurrencyCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ChangeFlowDefinitionCurrency {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeFlowDefinitionCurrency.class);

    private final AccountingAdminService.DefinitionCommands definitionCommands;

    private final ObjectMapper objectMapper;

    public ChangeFlowDefinitionCurrency(AccountingAdminService.DefinitionCommands definitionCommands, ObjectMapper objectMapper) {

        assert definitionCommands != null;
        assert objectMapper != null;

        this.definitionCommands = definitionCommands;
        this.objectMapper = objectMapper;
    }

    public void execute(ChangeFlowDefinitionCurrencyCommand.Input input) throws AccountingAdminClientException {

        try {

            RetrofitService
                .invoke(this.definitionCommands.changeFlowDefinitionCurrency(input),
                        (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, this.objectMapper));

        } catch (RetrofitService.InvocationException e) {

            LOGGER.error("Error invoking changeFlowDefinitionCurrency : {}", e.getMessage());

            var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse(String code, String message)) {

                throw new AccountingAdminClientException(code, message);
            }

            throw new AccountingAdminClientException("INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }
}
