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

package org.mojave.core.settlement.intercom.controller.api.query;

import org.mojave.common.datatype.identifier.settlement.SettlementDefinitionId;
import org.mojave.core.settlement.contract.data.SettlementDefinitionData;
import org.mojave.core.settlement.contract.query.SettlementDefinitionQuery;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
public class SettlementDefinitionQueryController {

    private final SettlementDefinitionQuery settlementDefinitionQuery;

    public SettlementDefinitionQueryController(
        final SettlementDefinitionQuery settlementDefinitionQuery) {

        Objects.requireNonNull(settlementDefinitionQuery);

        this.settlementDefinitionQuery = settlementDefinitionQuery;
    }

    @GetMapping("/settlement/settlement-definitions/get-by-id")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public SettlementDefinitionData get(
        @RequestParam final SettlementDefinitionId settlementDefinitionId) {

        return this.settlementDefinitionQuery.get(settlementDefinitionId);
    }

    @GetMapping("/settlement/settlement-definitions/get-all")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<SettlementDefinitionData> getAll() {

        return this.settlementDefinitionQuery.getAll();
    }

}
