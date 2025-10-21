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
import io.mojaloop.core.common.datatype.identifier.accounting.FlowDefinitionId;
import jakarta.validation.constraints.NotNull;

/**
 * Command contract for deactivating a Fund-In Definition.
 */
public interface DeactivateFlowDefinitionCommand {

    Output execute(Input input);

    /**
     * Input specifying the definition to deactivate.
     */
    record Input(@JsonProperty(required = true) @NotNull FlowDefinitionId flowDefinitionId) { }

    /**
     * Output returning the affected DefinitionId.
     */
    record Output(FlowDefinitionId flowDefinitionId) { }

}
