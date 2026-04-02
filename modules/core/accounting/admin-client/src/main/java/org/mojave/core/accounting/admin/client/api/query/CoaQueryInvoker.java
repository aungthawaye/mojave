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

package org.mojave.core.accounting.admin.client.api.query;

import org.mojave.common.datatype.identifier.accounting.CoaId;
import org.mojave.component.misc.error.RestErrorResponse;
import org.mojave.component.retrofit.RetrofitService;
import org.mojave.core.accounting.admin.client.service.AccountingAdminService;
import org.mojave.core.accounting.contract.data.CoaData;
import org.mojave.core.accounting.contract.exception.chart.CoaIdNotFoundException;
import org.mojave.core.accounting.contract.query.CoaQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Objects;

@Component
public class CoaQueryInvoker implements CoaQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoaQueryInvoker.class);

    private final AccountingAdminService.CoaQuery coaQuery;

    private final ObjectMapper objectMapper;

    public CoaQueryInvoker(final AccountingAdminService.CoaQuery coaQuery,
                           final ObjectMapper objectMapper) {

        Objects.requireNonNull(coaQuery);
        Objects.requireNonNull(objectMapper);

        this.coaQuery = coaQuery;
        this.objectMapper = objectMapper;
    }

    @Override
    public CoaData get(final CoaId coaId) throws CoaIdNotFoundException {

        try {

            return RetrofitService.invoke(
                this.coaQuery.getByCoaId(coaId),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CoaData> getAll() {

        try {

            return RetrofitService.invoke(
                this.coaQuery.getAllCoas(),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CoaData> getByNameContains(final String name) {

        try {

            return RetrofitService.invoke(
                this.coaQuery.getCoasByNameContains(name),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

}
