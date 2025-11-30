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

package io.mojaloop.core.accounting.admin.controller.api.command.definition;

import io.mojaloop.core.accounting.contract.command.definition.ChangeFlowDefinitionCurrencyCommand;
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
public class ChangeFlowDefinitionCurrencyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        ChangeFlowDefinitionCurrencyController.class);

    private final ChangeFlowDefinitionCurrencyCommand changeFlowDefinitionCurrencyCommand;

    public ChangeFlowDefinitionCurrencyController(final ChangeFlowDefinitionCurrencyCommand changeFlowDefinitionCurrencyCommand) {

        assert changeFlowDefinitionCurrencyCommand != null;

        this.changeFlowDefinitionCurrencyCommand = changeFlowDefinitionCurrencyCommand;
    }

    @PostMapping("/flow-definitions/change-currency")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ChangeFlowDefinitionCurrencyCommand.Output execute(@Valid @RequestBody final ChangeFlowDefinitionCurrencyCommand.Input input) {

        return this.changeFlowDefinitionCurrencyCommand.execute(input);
    }

}
