package org.mojave.accounting.contract.engine;

import lombok.Getter;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.accounting.MovementResult;
import org.mojave.common.datatype.enums.accounting.MovementStage;
import org.mojave.common.datatype.enums.accounting.OverdraftMode;
import org.mojave.common.datatype.enums.accounting.Side;
import org.mojave.common.datatype.identifier.accounting.AccountId;
import org.mojave.common.datatype.identifier.accounting.FlowDefinitionId;
import org.mojave.common.datatype.identifier.accounting.FlowDefinitionLineId;
import org.mojave.common.datatype.identifier.accounting.LedgerMovementId;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.scheme.rule.accounting.scenario.AccountingScenario;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public interface LedgerEngine {

    LedgerBalance createLedgerBalance(AccountId accountId, Currency currency, Integer scale,
                                      Side nature, BigDecimal postedDebits,
                                      BigDecimal postedCredits, OverdraftMode overdraftMode,
                                      BigDecimal overdraftLimit)
        throws AccountIdAlreadyTakenException;

    DrCr getDrCr(AccountId accountId, Side side);

    String getEngineType();

    List<Movement> postAccountingFlow(List<Request> requests, TransactionId transactionId,
                                      Instant transactionAt, AccountingScenario scenario) throws
                                                                                          InsufficientBalanceException,
                                                                                          NegativeAmountException,
                                                                                          OverdraftExceededException,
                                                                                          RestoreFailedException,
                                                                                          DuplicatePostingException;

    record LedgerBalance(AccountId accountId,
                         Currency currency,
                         Integer scale,
                         Side nature,
                         BigDecimal postedDebits,
                         BigDecimal postedCredits,
                         OverdraftMode overdraftMode,
                         BigDecimal overdraftLimit,
                         Instant createdAt) { }

    record Request(LedgerMovementId ledgerMovementId,
                   Integer step,
                   AccountId accountId,
                   Side side,
                   Currency currency,
                   BigDecimal amount,
                   AccountingScenario scenario,
                   FlowDefinitionId flowDefinitionId,
                   FlowDefinitionLineId flowDefinitionLineId) {

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
                    AccountingScenario scenario,
                    FlowDefinitionId flowDefinitionId,
                    FlowDefinitionLineId flowDefinitionLineId,
                    MovementStage movementStage,
                    MovementResult movementResult,
                    Instant createdAt) {

    }

    record DrCr(BigDecimal debits, BigDecimal credits) {

    }

    @Getter
    class AccountIdAlreadyTakenException extends Exception {

        private final AccountId accountId;

        public AccountIdAlreadyTakenException(AccountId accountId) {

            Objects.requireNonNull(accountId);

            super("Account ID (" + accountId.getId() + ") is already taken.");

            this.accountId = accountId;
        }

    }

    @Getter
    class InsufficientBalanceException extends Exception {

        private final AccountId accountId;

        private final Side side;

        private final BigDecimal amount;

        private final LedgerEngine.DrCr drCr;

        private final TransactionId transactionId;

        public InsufficientBalanceException(AccountId accountId, Side side, BigDecimal amount,
                                            LedgerEngine.DrCr drCr, TransactionId transactionId) {

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

        private final LedgerEngine.DrCr drCr;

        private final TransactionId transactionId;

        public OverdraftExceededException(AccountId accountId, Side side, BigDecimal amount,
                                          LedgerEngine.DrCr drCr, TransactionId transactionId) {

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

        private final LedgerEngine.DrCr drCr;

        private final TransactionId transactionId;

        public RestoreFailedException(AccountId accountId, Side side, BigDecimal amount,
                                      LedgerEngine.DrCr drCr, TransactionId transactionId) {

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

        public DuplicatePostingException(AccountId accountId, Side side,
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
