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
package org.mojave.core.accounting.contract.command.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.core.common.datatype.identifier.accounting.AccountId;

public interface ChangeAccountPropertiesCommand {

    Output execute(Input input);

    /**
     * Input for changing Account properties. Only allows changing code, name, and description.
     */
    record Input(@JsonProperty(required = true) @NotNull AccountId accountId,
                 @JsonProperty(required = false) @Size(max = StringSizeConstraints.MAX_NAME_TITLE_LENGTH) String name,
                 @JsonProperty(required = false) @Size(max = StringSizeConstraints.MAX_DESCRIPTION_LENGTH) String description) { }

    record Output(AccountId accountId) { }

}
