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

package io.mojaloop.core.account.domain.component.ledger;

import io.mojaloop.core.common.datatype.enums.account.Side;
import io.mojaloop.core.common.datatype.identifier.account.AccountId;
import io.mojaloop.core.common.datatype.identifier.account.LedgerMovementId;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface Ledger {

    List<Movement> post(List<Request> requests, TransactionId transactionId, Instant transactionAt)
        throws InsufficientBalanceException, NegativeAmountException;

    record Request(LedgerMovementId ledgerMovementId, AccountId accountId, Side side, BigDecimal amount) {

        public Request {

            if (amount.signum() < 0) {
                throw new NegativeAmountException();
            }

        }

    }

    record Movement(LedgerMovementId ledgerMovementId,
                    AccountId accountId,
                    Side side,
                    BigDecimal amount,
                    DrCr oldDrCr,
                    DrCr newDrCr,
                    TransactionId transactionId,
                    Instant transactionAt) {

    }

    record DrCr(BigDecimal debits, BigDecimal credits) {

    }

    @Getter
    class InsufficientBalanceException extends Exception {

        private final AccountId accountId;

        private final Side side;

        private final BigDecimal amount;

        private final Ledger.DrCr drCr;

        public InsufficientBalanceException(AccountId accountId, Side side, BigDecimal amount, Ledger.DrCr drCr) {

            super("Insufficient balance in Account (" + accountId.getId() + ") : side : " + side + " | posted debits: " + drCr.debits + "| posted credits: " +
                      drCr.credits + " | requested amount: " + amount);

            this.accountId = accountId;
            this.side = side;
            this.amount = amount;
            this.drCr = drCr;
        }

    }

    @Getter
    class OverdraftExceededException extends Exception {

        private final AccountId accountId;

        private final Side side;

        private final BigDecimal amount;

        private final Ledger.DrCr drCr;

        public OverdraftExceededException(AccountId accountId, Side side, BigDecimal amount, Ledger.DrCr drCr) {

            super("Overdraft exceeded in Account (" + accountId + ") : posted debits: " + drCr.debits + ", posted credits: " + drCr.credits);

            this.accountId = accountId;
            this.side = side;
            this.amount = amount;
            this.drCr = drCr;
        }

    }

    @Getter
    class RestoreFailedException extends Exception {

        private final AccountId accountId;

        private final Side side;

        private final BigDecimal amount;

        private final Ledger.DrCr drCr;

        public RestoreFailedException(AccountId accountId, Side side, BigDecimal amount, Ledger.DrCr drCr) {

            super("Unable to restore Dr/Cr in Account (" + accountId + ") : posted debits: " + drCr.debits + ", posted credits: " + drCr.credits);

            this.accountId = accountId;
            this.side = side;
            this.amount = amount;
            this.drCr = drCr;
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
