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

package org.mojave.core.wallet.contract.command.position;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mojave.core.common.datatype.identifier.transaction.TransactionId;
import org.mojave.core.common.datatype.identifier.wallet.PositionId;
import org.mojave.core.wallet.contract.exception.position.FailedToUpdateNdcException;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public interface UpdateNetDebitCapCommand {

    Output execute(Input input) throws FailedToUpdateNdcException;

    record Input(@JsonProperty(required = true) @NotNull PositionId positionId,
                 @JsonProperty(required = true) @NotNull BigDecimal netDebitCap,
                 @JsonProperty(required = true) @NotNull TransactionId transactionId,
                 @JsonProperty(required = true) @NotNull Instant transactionAt) { }

    record Output(BigDecimal position, BigDecimal reserved, BigDecimal netDebitCap) { }

}
