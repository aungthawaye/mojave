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

package org.mojave.core.settlement.admin.client.api.query;

import org.mojave.common.datatype.identifier.settlement.SettlementDefinitionId;
import org.mojave.component.misc.error.RestErrorResponse;
import org.mojave.component.retrofit.RetrofitService;
import org.mojave.core.settlement.admin.client.service.SettlementAdminService;
import org.mojave.core.settlement.contract.data.SettlementDefinitionData;
import org.mojave.core.settlement.contract.query.SettlementDefinitionQuery;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Objects;

@Component
public class SettlementDefinitionQueryInvoker implements SettlementDefinitionQuery {

    private final SettlementAdminService.DefinitionQuery definitionQuery;

    private final ObjectMapper objectMapper;

    public SettlementDefinitionQueryInvoker(
        final SettlementAdminService.DefinitionQuery definitionQuery,
        final ObjectMapper objectMapper) {

        Objects.requireNonNull(definitionQuery);
        Objects.requireNonNull(objectMapper);

        this.definitionQuery = definitionQuery;
        this.objectMapper = objectMapper;
    }

    @Override
    public SettlementDefinitionData get(final SettlementDefinitionId settlementDefinitionId) {

        try {

            return RetrofitService.invoke(
                this.definitionQuery.getBySettlementDefinitionId(settlementDefinitionId),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SettlementDefinitionData> getAll() {

        try {

            return RetrofitService.invoke(
                this.definitionQuery.getAllSettlementDefinitions(),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

}
