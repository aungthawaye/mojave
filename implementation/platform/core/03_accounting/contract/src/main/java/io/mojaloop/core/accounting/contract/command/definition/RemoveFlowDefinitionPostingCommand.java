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
import io.mojaloop.core.common.datatype.identifier.accounting.PostingDefinitionId;
import io.mojaloop.core.accounting.contract.exception.definition.FlowDefinitionNotFoundException;
import io.mojaloop.core.accounting.contract.exception.definition.PostingDefinitionNotFoundException;
import jakarta.validation.constraints.NotNull;

/**
 * Command contract for creating a Fund-In Definition.
 */
public interface RemoveFlowDefinitionPostingCommand {

    Output execute(Input input) throws PostingDefinitionNotFoundException, FlowDefinitionNotFoundException;

    /**
     * Input for creating a Fund-In Definition.
     */
    record Input(@JsonProperty(required = true) @NotNull FlowDefinitionId flowDefinitionId, @JsonProperty(required = true) @NotNull PostingDefinitionId postingDefinitionId) {

    }

    /**
     * Output of creation, returning the generated DefinitionId.
     */
    record Output(FlowDefinitionId flowDefinitionId) { }

}
