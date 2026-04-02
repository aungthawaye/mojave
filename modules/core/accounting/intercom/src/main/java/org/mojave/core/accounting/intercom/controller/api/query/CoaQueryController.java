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

import org.mojave.common.datatype.identifier.accounting.CoaId;
import org.mojave.core.accounting.contract.data.CoaData;
import org.mojave.core.accounting.contract.query.CoaQuery;
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
public class CoaQueryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        CoaQueryController.class.getName());

    private final CoaQuery coaQuery;

    public CoaQueryController(final CoaQuery coaQuery) {

        Objects.requireNonNull(coaQuery);

        this.coaQuery = coaQuery;
    }

    @GetMapping("/accounting/coas/get-by-id")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CoaData get(@RequestParam final CoaId coaId) {

        return this.coaQuery.get(coaId);
    }

    @GetMapping("/accounting/coas/get-all")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<CoaData> getAll() {

        return this.coaQuery.getAll();
    }

    @GetMapping("/accounting/coas/get-by-name-contains")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<CoaData> getByNameContains(@RequestParam final String name) {

        return this.coaQuery.getByNameContains(name);
    }

}
