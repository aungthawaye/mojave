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
package org.mojave.core.settlement.admin.controller.api.command.definition;

import jakarta.validation.Valid;
import org.mojave.core.settlement.contract.command.definition.UpdateSettlementDefinitionCommand;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class UpdateSettlementDefinitionController {

    private final UpdateSettlementDefinitionCommand updateSettlementDefinitionCommand;

    public UpdateSettlementDefinitionController(
        final UpdateSettlementDefinitionCommand updateSettlementDefinitionCommand) {

        Objects.requireNonNull(updateSettlementDefinitionCommand);
        this.updateSettlementDefinitionCommand = updateSettlementDefinitionCommand;
    }

    @PostMapping("/settlement-definitions/update")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UpdateSettlementDefinitionCommand.Output execute(
        @Valid @RequestBody final UpdateSettlementDefinitionCommand.Input input) {

        return this.updateSettlementDefinitionCommand.execute(input);
    }

}
