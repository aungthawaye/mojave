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

package io.mojaloop.core.account.contract.command.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.core.common.datatype.enums.account.OverdraftMode;
import io.mojaloop.core.common.datatype.identifier.account.AccountId;
import io.mojaloop.core.common.datatype.identifier.account.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.account.OwnerId;
import io.mojaloop.core.common.datatype.type.account.AccountCode;
import io.mojaloop.fspiop.spec.core.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public interface CreateAccountCommand {

    Output execute(Input input);

    /**
     * Input for creating an Account. Note: where the Account constructor in the domain would accept
     * other model instances (e.g., ChartEntry, Owner), here we accept only their Ids or primitive
     * representations to keep the contract decoupled from domain models, as requested.
     */
    record Input(@JsonProperty(required = true) @NotNull ChartEntryId chartEntryId,
                 @JsonProperty(required = true) @NotNull OwnerId ownerId,
                 @JsonProperty(required = true) @NotNull @NotBlank Currency currency,
                 @JsonProperty(required = true) @NotNull AccountCode code,
                 @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_NAME_TITLE_LENGTH) String name,
                 @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_DESCRIPTION_LENGTH) String description,
                 @JsonProperty(required = true) @NotNull OverdraftMode overdraftMode,
                 @JsonProperty(required = true) @NotNull BigDecimal overdraftLimit) { }

    record Output(AccountId accountId) { }

}
