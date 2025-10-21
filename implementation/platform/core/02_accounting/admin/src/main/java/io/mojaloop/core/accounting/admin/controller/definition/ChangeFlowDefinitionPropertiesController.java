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
/*-
 * ==============================================================================
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
 * ==============================================================================
 */

package io.mojaloop.core.accounting.admin.controller.definition;

import io.mojaloop.core.accounting.contract.command.definition.ChangeFlowDefinitionPropertiesCommand;
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
public class ChangeFlowDefinitionPropertiesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeFlowDefinitionPropertiesController.class);

    private final ChangeFlowDefinitionPropertiesCommand changeFlowDefinitionPropertiesCommand;

    public ChangeFlowDefinitionPropertiesController(final ChangeFlowDefinitionPropertiesCommand changeFlowDefinitionPropertiesCommand) {

        assert changeFlowDefinitionPropertiesCommand != null;

        this.changeFlowDefinitionPropertiesCommand = changeFlowDefinitionPropertiesCommand;
    }

    @PostMapping("/definitions/flows/change-properties")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ChangeFlowDefinitionPropertiesCommand.Output execute(
        @Valid @RequestBody final ChangeFlowDefinitionPropertiesCommand.Input input) {

        return this.changeFlowDefinitionPropertiesCommand.execute(input);
    }

}
