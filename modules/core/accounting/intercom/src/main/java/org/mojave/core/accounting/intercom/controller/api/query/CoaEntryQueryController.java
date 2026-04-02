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

package org.mojave.core.accounting.intercom.controller.api.query;

import org.mojave.common.datatype.enums.accounting.ChartEntryCategory;
import org.mojave.common.datatype.identifier.accounting.CoaEntryId;
import org.mojave.common.datatype.identifier.accounting.CoaId;
import org.mojave.core.accounting.contract.data.CoaEntryData;
import org.mojave.core.accounting.contract.query.CoaEntryQuery;
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
public class CoaEntryQueryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        CoaEntryQueryController.class.getName());

    private final CoaEntryQuery coaEntryQuery;

    public CoaEntryQueryController(final CoaEntryQuery coaEntryQuery) {

        Objects.requireNonNull(coaEntryQuery);

        this.coaEntryQuery = coaEntryQuery;
    }

    @GetMapping("/accounting/coa-entries/get-by-id")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CoaEntryData get(@RequestParam final CoaEntryId coaEntryId) {

        return this.coaEntryQuery.get(coaEntryId);
    }

    @GetMapping("/accounting/coa-entries/get-by-coa-id")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<CoaEntryData> get(@RequestParam final CoaId coaId) {

        return this.coaEntryQuery.get(coaId);
    }

    @GetMapping("/accounting/coa-entries/get-all")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<CoaEntryData> getAll() {

        return this.coaEntryQuery.getAll();
    }

    @GetMapping("/accounting/coa-entries/get-by-category")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<CoaEntryData> getByCategory(@RequestParam final ChartEntryCategory category) {

        return this.coaEntryQuery.get(category);
    }

    @GetMapping("/accounting/coa-entries/get-by-name-contains")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<CoaEntryData> getByNameContains(@RequestParam final String name) {

        return this.coaEntryQuery.get(name);
    }

}
