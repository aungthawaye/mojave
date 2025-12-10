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

package org.mojave.core.accounting.admin.client.api.query;

import tools.jackson.databind.ObjectMapper;
import org.mojave.component.misc.error.RestErrorResponse;
import org.mojave.component.retrofit.RetrofitService;
import org.mojave.core.accounting.admin.client.service.AccountingAdminService;
import org.mojave.core.accounting.contract.data.ChartData;
import org.mojave.core.accounting.contract.exception.chart.ChartIdNotFoundException;
import org.mojave.core.accounting.contract.query.ChartQuery;
import org.mojave.core.common.datatype.identifier.accounting.ChartId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChartQueryInvoker implements ChartQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChartQueryInvoker.class);

    private final AccountingAdminService.ChartQuery chartQuery;

    private final ObjectMapper objectMapper;

    public ChartQueryInvoker(final AccountingAdminService.ChartQuery chartQuery,
                             final ObjectMapper objectMapper) {

        assert chartQuery != null;
        assert objectMapper != null;

        this.chartQuery = chartQuery;
        this.objectMapper = objectMapper;
    }

    @Override
    public ChartData get(final ChartId chartId) throws ChartIdNotFoundException {

        try {

            return RetrofitService.invoke(
                this.chartQuery.getByChartId(chartId),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ChartData> getAll() {

        try {

            return RetrofitService.invoke(
                this.chartQuery.getAllCharts(),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ChartData> getByNameContains(final String name) {

        try {

            return RetrofitService.invoke(
                this.chartQuery.getChartsByNameContains(name),
                (status, errorResponseBody) -> RestErrorResponse.decode(
                    errorResponseBody,
                    this.objectMapper)).body();

        } catch (RetrofitService.InvocationException e) {

            throw new RuntimeException(e);
        }
    }

}
