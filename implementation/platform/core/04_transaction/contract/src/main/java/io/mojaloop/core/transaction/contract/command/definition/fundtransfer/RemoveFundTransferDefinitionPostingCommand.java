/*-
 * ==============================================================================
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
 * ==============================================================================
 */

package io.mojaloop.core.transaction.contract.command.definition.fundtransfer;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mojaloop.core.common.datatype.identifier.transaction.DefinitionId;
import io.mojaloop.core.common.datatype.identifier.transaction.PostingId;
import io.mojaloop.core.transaction.contract.exception.fundtransfer.FundTransferDefinitionNotFoundException;
import io.mojaloop.core.transaction.contract.exception.fundtransfer.FundTransferDefinitionPostingNotFoundException;
import jakarta.validation.constraints.NotNull;

/**
 * Command contract for removing a posting from a Fund-Transfer Definition.
 */
public interface RemoveFundTransferDefinitionPostingCommand {

    Output execute(Input input) throws FundTransferDefinitionPostingNotFoundException, FundTransferDefinitionNotFoundException;

    /**
     * Input identifying the definition and posting to remove.
     */
    record Input(@JsonProperty(required = true) @NotNull DefinitionId definitionId,
                 @JsonProperty(required = true) @NotNull PostingId postingId) { }

    /**
     * Output returning the affected DefinitionId.
     */
    record Output(DefinitionId definitionId) { }

}
