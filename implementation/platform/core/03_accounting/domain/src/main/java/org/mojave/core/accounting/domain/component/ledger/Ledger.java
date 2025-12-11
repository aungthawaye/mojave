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
package org.mojave.core.accounting.domain.component.ledger;

import lombok.Getter;
import org.mojave.core.common.datatype.enums.accounting.MovementResult;
import org.mojave.core.common.datatype.enums.accounting.MovementStage;
import org.mojave.core.common.datatype.enums.accounting.Side;
import org.mojave.core.common.datatype.enums.trasaction.TransactionType;
import org.mojave.core.common.datatype.identifier.accounting.AccountId;
import org.mojave.core.common.datatype.identifier.accounting.FlowDefinitionId;
import org.mojave.core.common.datatype.identifier.accounting.LedgerMovementId;
import org.mojave.core.common.datatype.identifier.accounting.PostingDefinitionId;
import org.mojave.core.common.datatype.identifier.transaction.TransactionId;
import org.mojave.fspiop.spec.core.Currency;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface Ledger {

    List<Movement> post(List<Request> requests,
                        TransactionId transactionId,
                        Instant transactionAt,
                        TransactionType transactionType) throws
                                                         InsufficientBalanceException,
                                                         NegativeAmountException,
                                                         OverdraftExceededException,
                                                         RestoreFailedException,
                                                         DuplicatePostingException;

    record Request(LedgerMovementId ledgerMovementId,
                   Integer step,
                   AccountId accountId,
                   Side side,
                   Currency currency,
                   BigDecimal amount,
                   FlowDefinitionId flowDefinitionId,
                   PostingDefinitionId postingDefinitionId) {

        public Request {

            if (amount.signum() < 0) {
                throw new NegativeAmountException();
            }

        }

    }

    record Movement(LedgerMovementId ledgerMovementId,
                    Integer step,
                    AccountId accountId,
                    Side side,
                    Currency currency,
                    BigDecimal amount,
                    DrCr oldDrCr,
                    DrCr newDrCr,
                    TransactionId transactionId,
                    Instant transactionAt,
                    TransactionType transactionType,
                    FlowDefinitionId flowDefinitionId,
                    PostingDefinitionId postingDefinitionId,
                    MovementStage movementStage,
                    MovementResult movementResult,
                    Instant createdAt) {

    }

    record DrCr(BigDecimal debits, BigDecimal credits) {

    }

    @Getter
    class InsufficientBalanceException extends Exception {

        private final AccountId accountId;

        private final Side side;

        private final BigDecimal amount;

        private final Ledger.DrCr drCr;

        private final TransactionId transactionId;

        public InsufficientBalanceException(AccountId accountId,
                                            Side side,
                                            BigDecimal amount,
                                            Ledger.DrCr drCr,
                                            TransactionId transactionId) {

            super("Insufficient balance in Account (" + accountId.getId() + ") : side : " + side +
                      " | posted debits: " + drCr.debits + "| posted credits: " + drCr.credits +
                      " | requested amount: " + amount + " | transactionId: " +
                      transactionId.getId().toString());

            this.accountId = accountId;
            this.side = side;
            this.amount = amount;
            this.drCr = drCr;
            this.transactionId = transactionId;
        }

    }

    @Getter
    class OverdraftExceededException extends Exception {

        private final AccountId accountId;

        private final Side side;

        private final BigDecimal amount;

        private final Ledger.DrCr drCr;

        private final TransactionId transactionId;

        public OverdraftExceededException(AccountId accountId,
                                          Side side,
                                          BigDecimal amount,
                                          Ledger.DrCr drCr,
                                          TransactionId transactionId) {

            super("Overdraft exceeded in Account (" + accountId + ") : posted debits: " +
                      drCr.debits + ", posted credits: " + drCr.credits + " | requested amount: " +
                      amount + " | transactionId: " + transactionId.getId().toString());

            this.accountId = accountId;
            this.side = side;
            this.amount = amount;
            this.drCr = drCr;
            this.transactionId = transactionId;
        }

    }

    @Getter
    class RestoreFailedException extends Exception {

        private final AccountId accountId;

        private final Side side;

        private final BigDecimal amount;

        private final Ledger.DrCr drCr;

        private final TransactionId transactionId;

        public RestoreFailedException(AccountId accountId,
                                      Side side,
                                      BigDecimal amount,
                                      Ledger.DrCr drCr,
                                      TransactionId transactionId) {

            super("Unable to restore Dr/Cr : account (" + accountId + ") | side (" + side.name() +
                      ") | amount (" + amount.stripTrailingZeros().toPlainString() +
                      ") | posted debits: " + drCr.debits + " | posted credits: " + drCr.credits +
                      " | transactionId: " + transactionId.getId().toString());

            this.accountId = accountId;
            this.side = side;
            this.amount = amount;
            this.drCr = drCr;
            this.transactionId = transactionId;
        }

    }

    @Getter
    class DuplicatePostingException extends Exception {

        private final AccountId accountId;

        private final Side side;

        private final TransactionId transactionId;

        public DuplicatePostingException(AccountId accountId,
                                         Side side,
                                         TransactionId transactionId) {

            super("Found duplicate posting for Account (" + accountId + ") : side (" + side +
                      ") in same Transaction (" + transactionId.getId().toString() + ").");
            this.accountId = accountId;
            this.side = side;
            this.transactionId = transactionId;
        }

    }

    @Getter
    class NegativeAmountException extends RuntimeException {

        public NegativeAmountException() {

            super("Amount must not be negative.");
        }

    }

    class NoMovementResultException extends RuntimeException {

        public NoMovementResultException() {

            super("Unable to retrieve movement result from operation.");
        }

    }

    class SqlErrorOccurredException extends RuntimeException {

        public SqlErrorOccurredException() {

            super("SQL error occurred.");
        }

    }

}
