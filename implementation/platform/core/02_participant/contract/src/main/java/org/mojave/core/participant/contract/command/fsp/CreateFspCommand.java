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

package org.mojave.core.participant.contract.command.fsp;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.core.common.datatype.enums.fspiop.EndpointType;
import org.mojave.core.common.datatype.identifier.participant.FspId;
import org.mojave.core.common.datatype.type.participant.FspCode;
import org.mojave.fspiop.spec.core.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public interface CreateFspCommand {

    Output execute(Input input);

    record Input(@JsonProperty(required = true) @NotNull FspCode fspCode,
                 @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_NAME_TITLE_LENGTH) String name,
                 @JsonProperty(required = true) @NotNull Currency[] currencies,
                 @JsonProperty(required = true) @NotNull Endpoint[] endpoints) {

        public record Endpoint(@JsonProperty(required = true) @NotNull @NotBlank EndpointType type,
                               @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_HTTP_URL_LENGTH) String baseUrl) { }

    }

    record Output(FspId fspId) { }

}
