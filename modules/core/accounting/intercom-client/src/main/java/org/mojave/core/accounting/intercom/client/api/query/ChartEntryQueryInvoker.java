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

import org.mojave.component.misc.error.RestErrorResponse;
import org.mojave.component.retrofit.RetrofitService;
import org.mojave.core.accounting.contract.data.ChartEntryData;
import org.mojave.core.accounting.contract.exception.chart.ChartEntryIdNotFoundException;
import org.mojave.core.accounting.contract.query.ChartEntryQuery;
import org.mojave.core.accounting.intercom.client.service.AccountingIntercomService;
import org.mojave.common.datatype.enums.accounting.ChartEntryCategory;
import org.mojave.common.datatype.identifier.accounting.ChartEntryId;
import org.mojave.common.datatype.identifier.accounting.ChartId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Objects;

@Component
public class ChartEntryQueryInvoker implements ChartEntryQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChartEntryQueryInvoker.class);

    private final AccountingIntercomService.ChartQuery chartQuery;

    private final ObjectMapper objectMapper;

    public ChartEntryQueryInvoker(final AccountingIntercomService.ChartQuery chartQuery,
                                  final ObjectMapper objectMapper) {

        Objects.requireNonNull(chartQuery);
        Objects.requireNonNull(objectMapper);

        this.chartQuery = chartQuery;
        this.objectMapper = objectMapper;
    }

    @Override
    public ChartEntryData get(final ChartEntryId chartEntryId)
        throws ChartEntryIdNotFoundException {

        try {

            return RetrofitService.invoke(
                this.chartQuery.getByChartEntryId(chartEntryId),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ChartEntryData> get(final ChartId chartId) {

        try {

            return RetrofitService.invoke(
                this.chartQuery.getEntriesByChartId(chartId),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ChartEntryData> get(final String name) {

        try {

            return RetrofitService.invoke(
                this.chartQuery.getChartEntriesByNameContains(name),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ChartEntryData> get(final ChartEntryCategory category) {

        try {

            return RetrofitService.invoke(
                this.chartQuery.getEntriesByCategory(category),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ChartEntryData> getAll() {

        try {

            return RetrofitService.invoke(
                this.chartQuery.getAllChartEntries(),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

}
