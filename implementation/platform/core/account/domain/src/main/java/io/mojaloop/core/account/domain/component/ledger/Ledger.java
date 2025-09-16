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

            super("Insufficient balance in Account (" + accountId + ") : posted debits: " + drCr.debits + ", posted credits: " + drCr.credits);

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
