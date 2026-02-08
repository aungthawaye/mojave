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

package org.mojave.core.participant.admin.controller.api.query;

import org.mojave.common.datatype.identifier.participant.SspId;
import org.mojave.common.datatype.type.participant.SspCode;
import org.mojave.core.participant.contract.data.SspData;
import org.mojave.core.participant.contract.query.SspQuery;
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
public class SspQueryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        SspQueryController.class.getName());

    private final SspQuery sspQuery;

    public SspQueryController(final SspQuery sspQuery) {

        Objects.requireNonNull(sspQuery);

        this.sspQuery = sspQuery;
    }

    @GetMapping("/participant/ssps/get-by-code")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public SspData get(@RequestParam final SspCode sspCode) {

        return this.sspQuery.get(sspCode);
    }

    @GetMapping("/participant/ssps/get-by-id")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public SspData get(@RequestParam final SspId sspId) {

        return this.sspQuery.get(sspId);
    }

    @GetMapping("/participant/ssps/get-all")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<SspData> getAll() {

        return this.sspQuery.getAll();
    }

}
