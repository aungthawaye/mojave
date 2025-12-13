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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.core.common.datatype.enums.accounting.OverdraftMode;
import org.mojave.core.common.datatype.identifier.accounting.AccountId;
import org.mojave.core.common.datatype.identifier.accounting.AccountOwnerId;
import org.mojave.core.common.datatype.identifier.accounting.ChartEntryId;
import org.mojave.core.common.datatype.type.accounting.AccountCode;
import org.mojave.fspiop.spec.core.Currency;

import java.math.BigDecimal;

public interface CreateAccountCommand {

    Output execute(Input input);

    /**
     * Input for creating an Account. Note: where the Account constructor in the domain would accept
     * other model instances (e.g., ChartEntry, Owner), here we accept only their Ids or primitive
     * representations to keep the contract decoupled from domain models, as requested.
     */
    record Input(@JsonProperty(required = true) @NotNull ChartEntryId chartEntryId,
                 @JsonProperty(required = true) @NotNull AccountOwnerId ownerId,
                 @JsonProperty(required = true) @NotNull Currency currency,
                 @JsonProperty(required = true) @NotNull AccountCode code,
                 @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_NAME_TITLE_LENGTH) String name,
                 @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_DESCRIPTION_LENGTH) String description,
                 @JsonProperty(required = true) @NotNull OverdraftMode overdraftMode,
                 @JsonProperty(required = true) @NotNull BigDecimal overdraftLimit) { }

    record Output(AccountId accountId) { }

}
