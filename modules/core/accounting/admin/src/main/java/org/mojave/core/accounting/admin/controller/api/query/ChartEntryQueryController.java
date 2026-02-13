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

package org.mojave.core.accounting.admin.controller.api.query;

import org.mojave.common.datatype.enums.accounting.ChartEntryCategory;
import org.mojave.common.datatype.identifier.accounting.ChartEntryId;
import org.mojave.common.datatype.identifier.accounting.ChartId;
import org.mojave.core.accounting.contract.data.ChartEntryData;
import org.mojave.core.accounting.contract.query.ChartEntryQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
public class ChartEntryQueryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        ChartEntryQueryController.class.getName());

    private final ChartEntryQuery chartEntryQuery;

    public ChartEntryQueryController(final ChartEntryQuery chartEntryQuery) {

        Objects.requireNonNull(chartEntryQuery);

        this.chartEntryQuery = chartEntryQuery;
    }

    @GetMapping("/accounting/chart-entries/get-by-id")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ChartEntryData get(@RequestParam final ChartEntryId chartEntryId) {

        return this.chartEntryQuery.get(chartEntryId);
    }

    @GetMapping("/accounting/chart-entries/get-by-chart-id")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<ChartEntryData> get(@RequestParam final ChartId chartId) {

        return this.chartEntryQuery.get(chartId);
    }

    @GetMapping("/accounting/chart-entries/get-all")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<ChartEntryData> getAll() {

        return this.chartEntryQuery.getAll();
    }

    @GetMapping("/accounting/chart-entries/get-by-category")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<ChartEntryData> getByCategory(@RequestParam final ChartEntryCategory category) {

        return this.chartEntryQuery.get(category);
    }

    @GetMapping("/accounting/chart-entries/get-by-name-contains")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<ChartEntryData> getByNameContains(@RequestParam final String name) {

        return this.chartEntryQuery.get(name);
    }

}
