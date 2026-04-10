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

package org.mojave.accounting.contract.command.definition;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.accounting.Side;
import org.mojave.common.datatype.identifier.accounting.CoaEntryId;
import org.mojave.common.datatype.identifier.accounting.FlowDefinitionId;
import org.mojave.common.datatype.identifier.accounting.FlowDefinitionLineId;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.scheme.rule.accounting.scenario.AccountingScenario;

import java.util.List;

public interface CreateFlowDefinitionCommand {

    String SUBJECT_NAME = "sub-accounting.create-flow-definition-command";

    String TOPIC_NAME = "tp-accounting.create-flow-definition-command";

    Output execute(Input input);

    record Input(@JsonProperty(required = true) @NotNull AccountingScenario scenario,
                 @JsonProperty(required = true) @NotNull Currency currency,
                 @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_NAME_TITLE_LENGTH) String name,
                 @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_DESCRIPTION_LENGTH) String description,
                 @JsonProperty(required = true) List<FlowDefinitionLine> flowDefinitionLines) {

        public record FlowDefinitionLine(@JsonProperty(required = true) @NotNull Integer step,
                               @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_NAME_TITLE_LENGTH) String participant,
                               @JsonProperty(required = true) @NotNull CoaEntryId coaEntryId,
                               @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_NAME_TITLE_LENGTH) String amountName,
                               @JsonProperty(required = true) @NotNull Side side,
                               @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_DESCRIPTION_LENGTH) String description) { }

    }

    record Output(FlowDefinitionId flowDefinitionId, List<FlowDefinitionLineId> flowDefinitionLineIds) { }

}
