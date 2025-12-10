/*-
 * ================================================================================
 * Mojave
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
/*-
 * ==============================================================================
 * Mojave
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
 * ==============================================================================
 */

package org.mojave.core.accounting.admin.controller.api.command.definition;

import org.mojave.core.accounting.contract.command.definition.CreateFlowDefinitionCommand;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreateFlowDefinitionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        CreateFlowDefinitionController.class);

    private final CreateFlowDefinitionCommand createFlowDefinitionCommand;

    public CreateFlowDefinitionController(final CreateFlowDefinitionCommand createFlowDefinitionCommand) {

        assert createFlowDefinitionCommand != null;

        this.createFlowDefinitionCommand = createFlowDefinitionCommand;
    }

    @PostMapping("/flow-definitions/create")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CreateFlowDefinitionCommand.Output execute(
        @Valid @RequestBody final CreateFlowDefinitionCommand.Input input) {

        return this.createFlowDefinitionCommand.execute(input);
    }

}
