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

package io.mojaloop.core.accounting.contract.command.ledger;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mojaloop.core.accounting.contract.exception.ledger.DuplicatePostingInLedgerException;
import io.mojaloop.core.accounting.contract.exception.ledger.InsufficientBalanceInAccountException;
import io.mojaloop.core.accounting.contract.exception.ledger.OverdraftLimitReachedInAccountException;
import io.mojaloop.core.accounting.contract.exception.ledger.PostingAccountNotFoundException;
import io.mojaloop.core.accounting.contract.exception.ledger.RestoreFailedInAccountException;
import io.mojaloop.core.common.datatype.enums.accounting.MovementResult;
import io.mojaloop.core.common.datatype.enums.accounting.MovementStage;
import io.mojaloop.core.common.datatype.enums.accounting.Side;
import io.mojaloop.core.common.datatype.enums.trasaction.TransactionType;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountId;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountOwnerId;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.accounting.LedgerMovementId;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.fspiop.spec.core.Currency;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface PostLedgerFlowCommand {

    Output execute(Input input) throws
                                PostingAccountNotFoundException,
                                InsufficientBalanceInAccountException,
                                OverdraftLimitReachedInAccountException,
                                DuplicatePostingInLedgerException,
                                RestoreFailedInAccountException;

    record Input(@JsonProperty(required = true) @NotNull Currency currency,
                 @JsonProperty(required = true) @NotNull TransactionId transactionId,
                 @JsonProperty(required = true) @NotNull TransactionType transactionType,
                 @JsonProperty(required = true) @NotNull Instant transactionAt,
                 List<Posting> postings) {

        public record Posting(@JsonProperty(required = true) @NotNull AccountOwnerId ownerId,
                              @JsonProperty(required = true) @NotNull ChartEntryId chartEntryId,
                              @JsonProperty(required = true) @NotNull Side side,
                              @JsonProperty(required = true) @NotNull BigDecimal amount) {

        }

    }

    record Output(List<Flow> flows) {

        public record Flow(AccountId accountId,
                           AccountOwnerId ownerId,
                           ChartEntryId chartEntryId,
                           LedgerMovementId ledgerMovementId,
                           Side side,
                           BigDecimal amount,
                           DrCr oldDrCr,
                           DrCr newDrCr,
                           MovementStage movementStage,
                           MovementResult movementResult) {

        }

        public record DrCr(BigDecimal debits, BigDecimal credits) { }

    }

}
