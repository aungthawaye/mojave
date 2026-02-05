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

package org.mojave.core.settlement.intercom.controller.api.command.filtergroup;

import jakarta.validation.Valid;
import org.mojave.core.settlement.contract.command.definition.CreateFilterGroupCommand;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/settlement-intercom")
public class CreateFilterGroupController {

    private final CreateFilterGroupCommand createFilterGroupCommand;

    public CreateFilterGroupController(final CreateFilterGroupCommand createFilterGroupCommand) {

        Objects.requireNonNull(createFilterGroupCommand);
        this.createFilterGroupCommand = createFilterGroupCommand;
    }

    @PostMapping("/filter-groups/create")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CreateFilterGroupCommand.Output execute(
        @Valid @RequestBody final CreateFilterGroupCommand.Input input) {

        return this.createFilterGroupCommand.execute(input);
    }

}
