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

import org.mojave.core.accounting.contract.data.FlowDefinitionData;
import org.mojave.core.accounting.contract.query.FlowDefinitionQuery;
import org.mojave.scheme.common.datatype.identifier.accounting.FlowDefinitionId;
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
public class FlowDefinitionQueryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        FlowDefinitionQueryController.class.getName());

    private final FlowDefinitionQuery flowDefinitionQuery;

    public FlowDefinitionQueryController(final FlowDefinitionQuery flowDefinitionQuery) {

        assert flowDefinitionQuery != null;

        this.flowDefinitionQuery = flowDefinitionQuery;
    }

    @GetMapping("/flow-definitions/get-by-flow-definition-id")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public FlowDefinitionData get(@RequestParam final FlowDefinitionId flowDefinitionId) {

        return this.flowDefinitionQuery.get(flowDefinitionId);
    }

    @GetMapping("/flow-definitions/get-all-flow-definitions")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<FlowDefinitionData> getAll() {

        return this.flowDefinitionQuery.getAll();
    }

    @GetMapping("/flow-definitions/get-by-name-contains")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<FlowDefinitionData> getByNameContains(@RequestParam final String name) {

        return this.flowDefinitionQuery.getByNameContains(name);
    }

}
