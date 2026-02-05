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

import org.mojave.core.participant.contract.data.HubData;
import org.mojave.core.participant.contract.query.HubQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/participant")
public class HubQueryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        HubQueryController.class.getName());

    private final HubQuery hubQuery;

    public HubQueryController(HubQuery hubQuery) {

        Objects.requireNonNull(hubQuery);

        this.hubQuery = hubQuery;
    }

    @GetMapping("/hubs/count")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public long count() {

        return 1;
    }

    @GetMapping("/hubs/get-hub")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public HubData get() {

        return this.hubQuery.get();
    }

    @GetMapping("/hubs/get-all-hubs")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<HubData> getAll() {

        return List.of(this.hubQuery.get());
    }

}
