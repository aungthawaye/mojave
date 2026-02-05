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

import org.mojave.common.datatype.identifier.participant.FspId;
import org.mojave.common.datatype.type.participant.FspCode;
import org.mojave.core.participant.contract.data.FspData;
import org.mojave.core.participant.contract.query.FspQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/participant")
public class FspQueryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        FspQueryController.class.getName());

    private final FspQuery fspQuery;

    public FspQueryController(FspQuery fspQuery) {

        Objects.requireNonNull(fspQuery);

        this.fspQuery = fspQuery;
    }

    @GetMapping("/fsps/get-by-fsp-code")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public FspData get(@RequestParam FspCode fspCode) {

        return this.fspQuery.get(fspCode);
    }

    @GetMapping("/fsps/get-by-fsp-id")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public FspData get(@RequestParam FspId fspId) {

        return this.fspQuery.get(fspId);
    }

    @GetMapping("/fsps/get-all-fsps")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<FspData> getAll() {

        return this.fspQuery.getAll();
    }

}
