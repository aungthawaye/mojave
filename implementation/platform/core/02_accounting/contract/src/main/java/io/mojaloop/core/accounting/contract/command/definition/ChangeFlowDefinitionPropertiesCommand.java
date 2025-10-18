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

package io.mojaloop.core.accounting.contract.command.definition;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.core.common.datatype.identifier.accounting.FlowDefinitionId;
import io.mojaloop.core.accounting.contract.exception.definition.FlowDefinitionNameTakenException;
import io.mojaloop.core.accounting.contract.exception.definition.FlowDefinitionNotFoundException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Command contract for changing properties of a Fund-In Definition.
 */
public interface ChangeFlowDefinitionPropertiesCommand {

    Output execute(Input input);

    /**
     * Input accepting the identifier and optional properties to change.
     */
    record Input(@JsonProperty(required = true) @NotNull FlowDefinitionId flowDefinitionId,
                 @JsonProperty(required = false) @Size(max = StringSizeConstraints.MAX_NAME_TITLE_LENGTH) String name,
                 @JsonProperty(required = false) @Size(max = StringSizeConstraints.MAX_DESCRIPTION_LENGTH) String description) { }

    /**
     * Output returning the affected DefinitionId.
     */
    record Output(FlowDefinitionId flowDefinitionId) { }
}
