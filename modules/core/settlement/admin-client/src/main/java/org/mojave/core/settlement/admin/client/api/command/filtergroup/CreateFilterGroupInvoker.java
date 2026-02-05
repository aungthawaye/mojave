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

package org.mojave.core.settlement.admin.client.api.command.filtergroup;

import org.mojave.component.misc.error.RestErrorResponse;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.component.retrofit.RetrofitService;
import org.mojave.core.settlement.admin.client.service.SettlementAdminService;
import org.mojave.core.settlement.contract.command.definition.CreateFilterGroupCommand;
import org.mojave.core.settlement.contract.exception.SettlementExceptionResolver;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class CreateFilterGroupInvoker implements CreateFilterGroupCommand {

    private final SettlementAdminService.FilterGroupCommand filterGroupCommand;

    private final ObjectMapper objectMapper;

    public CreateFilterGroupInvoker(final SettlementAdminService.FilterGroupCommand filterGroupCommand,
                                    final ObjectMapper objectMapper) {

        Objects.requireNonNull(filterGroupCommand);
        Objects.requireNonNull(objectMapper);

        this.filterGroupCommand = filterGroupCommand;
        this.objectMapper = objectMapper;
    }

    @Override
    public Output execute(final Input input) {

        try {

            return RetrofitService.invoke(
                this.filterGroupCommand.create(input),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse errorResponse) {

                var throwable = SettlementExceptionResolver.resolve(errorResponse);

                if (throwable instanceof UncheckedDomainException ude) {
                    throw ude;
                }
            }

            throw new RuntimeException(e);
        }
    }

}
