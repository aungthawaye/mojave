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
import io.mojaloop.core.common.datatype.enums.accounting.ReceiveIn;
import io.mojaloop.core.common.datatype.enums.accounting.Side;
import io.mojaloop.core.common.datatype.identifier.accounting.FlowDefinitionId;
import io.mojaloop.core.common.datatype.identifier.accounting.PostingDefinitionId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Command contract for adding postings to a Flow Definition.
 */
public interface AddPostingDefinitionCommand {

    Output execute(Input input);

    /**
     * Input for adding postings under a Flow Definition.
     */
    record Input(@JsonProperty(required = true) @NotNull FlowDefinitionId flowDefinitionId,
                 @JsonProperty(required = true) @NotNull Posting posting) {

        public record Posting(@JsonProperty(required = true) @NotNull ReceiveIn receiveIn,
                              @JsonProperty(required = true) @NotNull Long receiveInId,
                              @JsonProperty(required = true) @NotNull @NotBlank @Size(
                                  max = StringSizeConstraints.MAX_NAME_TITLE_LENGTH) String participant,
                              @JsonProperty(required = true) @NotNull @NotBlank @Size(
                                  max = StringSizeConstraints.MAX_NAME_TITLE_LENGTH) String amountName,
                              @JsonProperty(required = true) @NotNull Side side, @JsonProperty(required = true) @NotNull @NotBlank @Size(
            max = StringSizeConstraints.MAX_DESCRIPTION_LENGTH) String description) { }

    }

    /**
     * Output, returning the target DefinitionId.
     */
    record Output(FlowDefinitionId flowDefinitionId, PostingDefinitionId postingDefinitionId) { }

}
