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

package io.mojaloop.core.transaction.contract.command.definition.fundin;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mojaloop.core.common.datatype.enums.accounting.Side;
import io.mojaloop.core.common.datatype.enums.trasaction.definition.fundin.PostingAmountType;
import io.mojaloop.core.common.datatype.enums.trasaction.definition.fundin.PostingOwnerType;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.transaction.DefinitionId;
import io.mojaloop.core.transaction.contract.exception.PostingAlreadyExistsException;
import io.mojaloop.core.transaction.contract.exception.fundin.FundInDefinitionNotFoundException;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Command contract for creating a Fund-In Definition.
 */
public interface AddFundInDefinitionPostingCommand {

    Output execute(Input input) throws PostingAlreadyExistsException, FundInDefinitionNotFoundException;

    /**
     * Input for creating a Fund-In Definition.
     */
    record Input(@JsonProperty(required = true) @NotNull DefinitionId definitionId,
                 @JsonProperty(required = true) List<Posting> postings) {

        public record Posting(Side side, PostingOwnerType forOwner, PostingAmountType forAmount, ChartEntryId chartEntryId) { }

    }

    /**
     * Output of creation, returning the generated DefinitionId.
     */
    record Output(DefinitionId definitionId) { }

}
