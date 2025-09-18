/*-
 * ================================================================================
 * Mojaloop OSS
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
package io.mojaloop.core.participant.admin.controller.hub;

import io.mojaloop.core.participant.contract.data.HubData;
import io.mojaloop.core.participant.contract.query.HubQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GetAllHubsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetAllHubsController.class);

    private final HubQuery hubQuery;

    public GetAllHubsController(HubQuery hubQuery) {

        assert hubQuery != null;

        this.hubQuery = hubQuery;
    }

    @GetMapping("/hubs/get-all-hubs")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<HubData> execute() {

        return this.hubQuery.getAll();
    }
}
