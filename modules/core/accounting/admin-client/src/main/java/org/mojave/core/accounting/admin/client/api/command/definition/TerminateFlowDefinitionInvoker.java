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

package org.mojave.core.accounting.admin.client.api.command.definition;

import org.mojave.component.misc.error.RestErrorResponse;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.component.retrofit.RetrofitService;
import org.mojave.core.accounting.admin.client.service.AccountingAdminService;
import org.mojave.core.accounting.contract.command.definition.TerminateFlowDefinitionCommand;
import org.mojave.core.accounting.contract.exception.AccountingExceptionResolver;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class TerminateFlowDefinitionInvoker implements TerminateFlowDefinitionCommand {

    private final AccountingAdminService.DefinitionCommand definitionCommand;

    private final ObjectMapper objectMapper;

    public TerminateFlowDefinitionInvoker(final AccountingAdminService.DefinitionCommand definitionCommand,
                                          final ObjectMapper objectMapper) {

        Objects.requireNonNull(definitionCommand);
        Objects.requireNonNull(objectMapper);

        this.definitionCommand = definitionCommand;
        this.objectMapper = objectMapper;
    }

    @Override
    public Output execute(final Input input) {

        try {

            return RetrofitService.invoke(
                this.definitionCommand.terminate(input),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse errorResponse) {

                var throwable = AccountingExceptionResolver.resolve(errorResponse);

                if (throwable instanceof UncheckedDomainException ude) {
                    throw ude;
                }
            }

            throw new RuntimeException(e);
        }
    }

}
