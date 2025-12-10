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

package io.mojaloop.core.accounting.admin.client.api.query;

import tools.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.error.RestErrorResponse;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.accounting.admin.client.service.AccountingAdminService;
import io.mojaloop.core.accounting.contract.data.FlowDefinitionData;
import io.mojaloop.core.accounting.contract.exception.definition.FlowDefinitionNotFoundException;
import io.mojaloop.core.accounting.contract.query.FlowDefinitionQuery;
import io.mojaloop.core.common.datatype.identifier.accounting.FlowDefinitionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FlowDefinitionQueryInvoker implements FlowDefinitionQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlowDefinitionQueryInvoker.class);

    private final AccountingAdminService.DefinitionQuery definitionQuery;

    private final ObjectMapper objectMapper;

    public FlowDefinitionQueryInvoker(final AccountingAdminService.DefinitionQuery definitionQuery,
                                      final ObjectMapper objectMapper) {

        assert definitionQuery != null;
        assert objectMapper != null;

        this.definitionQuery = definitionQuery;
        this.objectMapper = objectMapper;
    }

    @Override
    public FlowDefinitionData get(final FlowDefinitionId flowDefinitionId)
        throws FlowDefinitionNotFoundException {

        try {

            return RetrofitService.invoke(
                this.definitionQuery.getByFlowDefinitionId(flowDefinitionId),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<FlowDefinitionData> getAll() {

        try {

            return RetrofitService.invoke(
                this.definitionQuery.getAllFlowDefinitions(),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<FlowDefinitionData> getByNameContains(final String name) {

        try {

            return RetrofitService.invoke(
                this.definitionQuery.getFlowDefinitionsByNameContains(name),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

}
