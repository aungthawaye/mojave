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

package org.mojave.core.participant.contract.command.fsp;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.participant.FspCurrencyId;
import org.mojave.common.datatype.identifier.participant.FspId;

public interface ActivateFspCurrencyCommand {

    Output execute(Input input);

    record Input(@JsonProperty(required = true) @NotNull FspId fspId,
                 @JsonProperty(required = true) @NotNull Currency currency) { }

    record Output(FspCurrencyId fspCurrencyId, boolean activated) { }

}
