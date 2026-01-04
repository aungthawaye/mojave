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
package org.mojave.core.accounting.contract.command.chart;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.scheme.common.datatype.enums.accounting.AccountType;
import org.mojave.scheme.common.datatype.enums.accounting.ChartEntryCategory;
import org.mojave.scheme.common.datatype.identifier.accounting.ChartEntryId;
import org.mojave.scheme.common.datatype.identifier.accounting.ChartId;
import org.mojave.scheme.common.datatype.type.accounting.ChartEntryCode;

public interface CreateChartEntryCommand {

    Output execute(Input input);

    record Input(@JsonProperty(required = true) @NotNull ChartId chartId,
                 @JsonProperty(required = true) @NotNull ChartEntryCategory category,
                 @JsonProperty(required = true) @NotNull ChartEntryCode code,
                 @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_NAME_TITLE_LENGTH) String name,
                 @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_DESCRIPTION_LENGTH) String description,
                 @JsonProperty(required = true) @NotNull AccountType accountType) { }

    record Output(ChartEntryId chartEntryId) { }

}
