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

package org.mojave.core.accounting.admin.controller.api.query;

import org.mojave.core.accounting.contract.data.ChartData;
import org.mojave.core.accounting.contract.query.ChartQuery;
import org.mojave.core.common.datatype.identifier.accounting.ChartId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChartQueryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        ChartQueryController.class.getName());

    private final ChartQuery chartQuery;

    public ChartQueryController(final ChartQuery chartQuery) {

        assert chartQuery != null;

        this.chartQuery = chartQuery;
    }

    @GetMapping("/charts/get-by-chart-id")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ChartData get(@RequestParam final ChartId chartId) {

        return this.chartQuery.get(chartId);
    }

    @GetMapping("/charts/get-all-charts")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<ChartData> getAll() {

        return this.chartQuery.getAll();
    }

    @GetMapping("/charts/get-by-name-contains")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<ChartData> getByNameContains(@RequestParam final String name) {

        return this.chartQuery.getByNameContains(name);
    }

}
