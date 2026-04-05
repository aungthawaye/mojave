package org.mojave.accounting.domain;

import org.mojave.accounting.contract.ledger.Ledger;
import org.mojave.common.datatype.enums.accounting.MovementResult;
import org.mojave.common.datatype.enums.accounting.MovementStage;
import org.mojave.common.datatype.enums.accounting.Side;
import org.mojave.common.datatype.identifier.accounting.AccountId;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.component.jpa.routing.RoutingDataSourceConfigurer;
import org.mojave.component.jpa.routing.RoutingEntityManagerConfigurer;
import org.mojave.scheme.rule.type.TransactionType;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountingDomainSettings implements AccountingDomainConfiguration.RequiredSettings {

    private static final String READ_DB_URL = "jdbc:mysql://localhost:3306/mv_mojave?createDatabaseIfNotExist=true";

    private static final String READ_DB_USER = "root";

    private static final String READ_DB_PASSWORD = "password";

    private static final long READ_DB_CONNECTION_TIMEOUT = 30000L;

    private static final long READ_DB_VALIDATION_TIMEOUT = 5000L;

    private static final long READ_DB_MAX_LIFETIME_TIMEOUT = 1800000L;

    private static final long READ_DB_IDLE_TIMEOUT = 600000L;

    private static final long READ_DB_KEEPALIVE_TIMEOUT = 300000L;

    private static final int READ_DB_MIN_POOL_SIZE = 2;

    private static final int READ_DB_MAX_POOL_SIZE = 2;

    private static final String WRITE_DB_URL = "jdbc:mysql://localhost:3306/mv_mojave?createDatabaseIfNotExist=true";

    private static final String WRITE_DB_USER = "root";

    private static final String WRITE_DB_PASSWORD = "password";

    private static final long WRITE_DB_CONNECTION_TIMEOUT = 30000L;

    private static final long WRITE_DB_VALIDATION_TIMEOUT = 5000L;

    private static final long WRITE_DB_MAX_LIFETIME_TIMEOUT = 1800000L;

    private static final long WRITE_DB_IDLE_TIMEOUT = 600000L;

    private static final long WRITE_DB_KEEPALIVE_TIMEOUT = 300000L;

    private static final int WRITE_DB_MIN_POOL_SIZE = 2;

    private static final int WRITE_DB_MAX_POOL_SIZE = 2;



    @Bean
    @Override
    public RoutingDataSourceConfigurer.ReadSettings routingDataSourceReadSettings() {

        final var connection = new RoutingDataSourceConfigurer.ReadSettings.Connection(
            READ_DB_URL, READ_DB_USER, READ_DB_PASSWORD, READ_DB_CONNECTION_TIMEOUT,
            READ_DB_VALIDATION_TIMEOUT, READ_DB_MAX_LIFETIME_TIMEOUT, READ_DB_IDLE_TIMEOUT,
            READ_DB_KEEPALIVE_TIMEOUT, false);

        final var pool = new RoutingDataSourceConfigurer.ReadSettings.Pool(
            "mojave-accounting-read", READ_DB_MIN_POOL_SIZE, READ_DB_MAX_POOL_SIZE);

        return new RoutingDataSourceConfigurer.ReadSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingDataSourceConfigurer.WriteSettings routingDataSourceWriteSettings() {

        final var connection = new RoutingDataSourceConfigurer.WriteSettings.Connection(
            WRITE_DB_URL, WRITE_DB_USER, WRITE_DB_PASSWORD, WRITE_DB_CONNECTION_TIMEOUT,
            WRITE_DB_VALIDATION_TIMEOUT, WRITE_DB_MAX_LIFETIME_TIMEOUT, WRITE_DB_IDLE_TIMEOUT,
            WRITE_DB_KEEPALIVE_TIMEOUT, false);

        final var pool = new RoutingDataSourceConfigurer.WriteSettings.Pool(
            "mojave-accounting-write", WRITE_DB_MIN_POOL_SIZE, WRITE_DB_MAX_POOL_SIZE);

        return new RoutingDataSourceConfigurer.WriteSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingEntityManagerConfigurer.Settings routingEntityManagerSettings() {

        return new RoutingEntityManagerConfigurer.Settings("accounting-domain", false, false);
    }

    public static final class TestLedger implements Ledger {

        private final Map<AccountId, LedgerBalance> balances = new HashMap<>();

        private final Map<AccountId, Ledger.DrCr> drCrByAccountId = new HashMap<>();

        private final List<Request> lastRequests = new ArrayList<>();

        private boolean failNextCreateLedgerBalance;

        private RuntimeException nextCreateLedgerBalanceRuntimeException;

        private Exception nextPostException;

        public void failNextCreateLedgerBalance() {

            this.failNextCreateLedgerBalance = true;
            this.nextCreateLedgerBalanceRuntimeException = null;
        }

        public void failNextCreateLedgerBalance(final RuntimeException runtimeException) {

            this.failNextCreateLedgerBalance = false;
            this.nextCreateLedgerBalanceRuntimeException = runtimeException;
        }

        public void failNextPost(final Exception exception) {

            this.nextPostException = exception;
        }

        public List<Request> getLastRequests() {

            return List.copyOf(this.lastRequests);
        }

        public void reset() {

            this.balances.clear();
            this.drCrByAccountId.clear();
            this.lastRequests.clear();
            this.failNextCreateLedgerBalance = false;
            this.nextCreateLedgerBalanceRuntimeException = null;
            this.nextPostException = null;
        }

        @Override
        public void createLedgerBalance(final LedgerBalance ledgerBalance)
            throws AccountIdAlreadyTakenException {

            if (this.nextCreateLedgerBalanceRuntimeException != null) {

                final var exception = this.nextCreateLedgerBalanceRuntimeException;

                this.nextCreateLedgerBalanceRuntimeException = null;

                throw exception;
            }

            if (this.failNextCreateLedgerBalance) {

                this.failNextCreateLedgerBalance = false;

                throw new AccountIdAlreadyTakenException(ledgerBalance.accountId());
            }

            if (this.balances.containsKey(ledgerBalance.accountId())) {
                throw new AccountIdAlreadyTakenException(ledgerBalance.accountId());
            }

            this.balances.put(ledgerBalance.accountId(), ledgerBalance);
            this.drCrByAccountId.put(
                ledgerBalance.accountId(),
                new Ledger.DrCr(ledgerBalance.postedDebits(), ledgerBalance.postedCredits()));
        }

        @Override
        public List<Movement> post(final List<Request> requests,
                                   final TransactionId transactionId,
                                   final Instant transactionAt,
                                   final TransactionType transactionType) throws
                                                                          InsufficientBalanceException,
                                                                          NegativeAmountException,
                                                                          OverdraftExceededException,
                                                                          RestoreFailedException,
                                                                          DuplicatePostingException {

            this.lastRequests.clear();
            this.lastRequests.addAll(requests);

            if (this.nextPostException != null) {

                final var exception = this.nextPostException;

                this.nextPostException = null;

                if (exception instanceof InsufficientBalanceException insufficientBalanceException) {
                    throw insufficientBalanceException;
                }

                if (exception instanceof OverdraftExceededException overdraftExceededException) {
                    throw overdraftExceededException;
                }

                if (exception instanceof RestoreFailedException restoreFailedException) {
                    throw restoreFailedException;
                }

                if (exception instanceof DuplicatePostingException duplicatePostingException) {
                    throw duplicatePostingException;
                }

                if (exception instanceof NegativeAmountException negativeAmountException) {
                    throw negativeAmountException;
                }

                if (exception instanceof RuntimeException runtimeException) {
                    throw runtimeException;
                }

                throw new IllegalStateException("Unsupported ledger test exception.", exception);
            }

            final var movements = new ArrayList<Movement>();

            for (final var request : requests) {

                final var oldDrCr = this.drCrByAccountId.getOrDefault(
                    request.accountId(), new Ledger.DrCr(BigDecimal.ZERO, BigDecimal.ZERO));
                final Ledger.DrCr newDrCr;

                if (request.side() == Side.DEBIT) {
                    newDrCr = new Ledger.DrCr(
                        oldDrCr.debits().add(request.amount()), oldDrCr.credits());

                } else {
                    newDrCr = new Ledger.DrCr(
                        oldDrCr.debits(), oldDrCr.credits().add(request.amount()));
                }

                this.drCrByAccountId.put(request.accountId(), newDrCr);

                movements.add(new Movement(
                    request.ledgerMovementId(),
                    request.step(),
                    request.accountId(),
                    request.side(),
                    request.currency(),
                    request.amount(),
                    oldDrCr,
                    newDrCr,
                    transactionId,
                    transactionAt,
                    transactionType,
                    request.flowDefinitionId(),
                    request.flowLineId(),
                    MovementStage.COMMIT,
                    MovementResult.SUCCESS,
                    transactionAt));
            }

            return movements;
        }

    }



}
