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

package org.mojave.core.accounting.intercom.client.api.query;

import org.mojave.common.datatype.enums.accounting.ChartEntryCategory;
import org.mojave.common.datatype.identifier.accounting.CoaEntryId;
import org.mojave.common.datatype.identifier.accounting.CoaId;
import org.mojave.component.misc.error.RestErrorResponse;
import org.mojave.component.retrofit.RetrofitService;
import org.mojave.core.accounting.contract.data.CoaEntryData;
import org.mojave.core.accounting.contract.exception.chart.CoaEntryIdNotFoundException;
import org.mojave.core.accounting.contract.query.CoaEntryQuery;
import org.mojave.core.accounting.intercom.client.service.AccountingIntercomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Objects;

@Component
public class CoaEntryQueryInvoker implements CoaEntryQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoaEntryQueryInvoker.class);

    private final AccountingIntercomService.CoaQuery coaQuery;

    private final ObjectMapper objectMapper;

    public CoaEntryQueryInvoker(final AccountingIntercomService.CoaQuery coaQuery,
                                final ObjectMapper objectMapper) {

        Objects.requireNonNull(coaQuery);
        Objects.requireNonNull(objectMapper);

        this.coaQuery = coaQuery;
        this.objectMapper = objectMapper;
    }

    @Override
    public CoaEntryData get(final CoaEntryId coaEntryId)
        throws CoaEntryIdNotFoundException {

        try {

            return RetrofitService.invoke(
                this.coaQuery.getByCoaEntryId(coaEntryId),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CoaEntryData> get(final CoaId coaId) {

        try {

            return RetrofitService.invoke(
                this.coaQuery.getEntriesByCoaId(coaId),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CoaEntryData> get(final String name) {

        try {

            return RetrofitService.invoke(
                this.coaQuery.getCoaEntriesByNameContains(name),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CoaEntryData> get(final ChartEntryCategory category) {

        try {

            return RetrofitService.invoke(
                this.coaQuery.getEntriesByCategory(category),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CoaEntryData> getAll() {

        try {

            return RetrofitService.invoke(
                this.coaQuery.getAllCoaEntries(),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

}
