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

package org.mojave.core.accounting.contract.command.definition;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.core.common.datatype.enums.accounting.ReceiveIn;
import org.mojave.core.common.datatype.enums.accounting.Side;
import org.mojave.core.common.datatype.enums.trasaction.TransactionType;
import org.mojave.core.common.datatype.identifier.accounting.FlowDefinitionId;
import org.mojave.core.common.datatype.identifier.accounting.PostingDefinitionId;
import org.mojave.fspiop.spec.core.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public interface CreateFlowDefinitionCommand {

    Output execute(Input input);

    /**
     * Input for creating a Flow Definition.
     */
    record Input(@JsonProperty(required = true) @NotNull TransactionType transactionType,
                 @JsonProperty(required = true) @NotNull Currency currency,
                 @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_NAME_TITLE_LENGTH) String name,
                 @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_DESCRIPTION_LENGTH) String description,
                 @JsonProperty(required = true) List<Posting> postings) {

        public record Posting(@JsonProperty(required = true) @NotNull Integer step,
                              @JsonProperty(required = true) @NotNull ReceiveIn receiveIn,
                              @JsonProperty(required = true) @NotNull Long receiveInId,
                              @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_NAME_TITLE_LENGTH) String participant,
                              @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_NAME_TITLE_LENGTH) String amountName,
                              @JsonProperty(required = true) @NotNull Side side,
                              @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_DESCRIPTION_LENGTH) String description) { }

    }

    record Output(FlowDefinitionId flowDefinitionId,
                  List<PostingDefinitionId> postingDefinitionIds) { }

}
