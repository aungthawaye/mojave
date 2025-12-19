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
package org.mojave.core.accounting.contract.command.ledger;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.mojave.core.accounting.contract.exception.ledger.DuplicatePostingInLedgerException;
import org.mojave.core.accounting.contract.exception.ledger.InsufficientBalanceInAccountException;
import org.mojave.core.accounting.contract.exception.ledger.OverdraftLimitReachedInAccountException;
import org.mojave.core.accounting.contract.exception.ledger.RestoreFailedInAccountException;
import org.mojave.core.common.datatype.enums.accounting.MovementResult;
import org.mojave.core.common.datatype.enums.accounting.MovementStage;
import org.mojave.core.common.datatype.enums.accounting.Side;
import org.mojave.core.common.datatype.enums.trasaction.TransactionType;
import org.mojave.core.common.datatype.identifier.accounting.AccountId;
import org.mojave.core.common.datatype.identifier.accounting.AccountOwnerId;
import org.mojave.core.common.datatype.identifier.accounting.ChartEntryId;
import org.mojave.core.common.datatype.identifier.accounting.FlowDefinitionId;
import org.mojave.core.common.datatype.identifier.accounting.LedgerMovementId;
import org.mojave.core.common.datatype.identifier.transaction.TransactionId;
import org.mojave.fspiop.spec.core.Currency;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface PostLedgerFlowCommand {

    Output execute(Input input) throws
                                InsufficientBalanceInAccountException,
                                OverdraftLimitReachedInAccountException,
                                DuplicatePostingInLedgerException,
                                RestoreFailedInAccountException;

    record Input(@JsonProperty(required = true) @NotNull TransactionType transactionType,
                 @JsonProperty(required = true) @NotNull Currency currency,
                 @JsonProperty(required = true) @NotNull TransactionId transactionId,
                 @JsonProperty(required = true) @NotNull Instant transactionAt,
                 @JsonProperty(required = true) @NotNull Map<String, AccountOwnerId> participants,
                 @JsonProperty(required = true) @NotNull Map<String, BigDecimal> amounts) {

    }

    record Output(TransactionId transactionId,
                  Instant transactionAt,
                  TransactionType transactionType,
                  FlowDefinitionId flowDefinitionId,
                  List<Movement> movements) {

        public record Movement(LedgerMovementId ledgerMovementId,
                               AccountId accountId,
                               AccountOwnerId ownerId,
                               ChartEntryId chartEntryId,
                               Side side,
                               Currency currency,
                               BigDecimal amount,
                               DrCr oldDrCr,
                               DrCr newDrCr,
                               MovementStage movementStage,
                               MovementResult movementResult,
                               Instant createdAt) {

        }

        public record DrCr(BigDecimal debits, BigDecimal credits) { }

    }

}
