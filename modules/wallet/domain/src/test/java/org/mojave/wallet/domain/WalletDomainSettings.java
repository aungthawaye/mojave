package org.mojave.wallet.domain;

import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.identifier.wallet.BalanceUpdateId;
import org.mojave.common.datatype.identifier.wallet.NdcUpdateId;
import org.mojave.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.common.datatype.identifier.wallet.WalletId;
import org.mojave.component.jpa.routing.RoutingDataSourceConfigurer;
import org.mojave.component.jpa.routing.RoutingEntityManagerConfigurer;
import org.mojave.wallet.contract.engine.WalletEngine;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class WalletDomainSettings implements WalletDomainConfiguration.RequiredSettings {

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
            "mojave-wallet-read", READ_DB_MIN_POOL_SIZE, READ_DB_MAX_POOL_SIZE);

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
            "mojave-wallet-write", WRITE_DB_MIN_POOL_SIZE, WRITE_DB_MAX_POOL_SIZE);

        return new RoutingDataSourceConfigurer.WriteSettings(connection, pool);
    }

    @Bean
    @Override
    public RoutingEntityManagerConfigurer.Settings routingEntityManagerSettings() {

        return new RoutingEntityManagerConfigurer.Settings("wallet-domain", false, false);
    }

    public static final class TestWalletEngine implements WalletEngine {

        private final Map<WalletId, CreatedWallet> wallets = new HashMap<>();

        private PositionHistory nextCommitPositionReservationHistory;

        private PositionReservationCommitFailedException nextCommitPositionReservationException;

        private PositionHistory nextDecreasePositionHistory;

        private NoPositionUpdateException nextDecreasePositionException;

        private BalanceHistory nextDepositBalanceHistory;

        private NoBalanceUpdateException nextDepositBalanceException;

        private boolean failNextCreateWallet;

        private FulfilResult nextFulfilResult;

        private NoPositionFulfilmentException nextFulfilException;

        private PositionHistory nextIncreasePositionHistory;

        private Exception nextIncreasePositionException;

        private BalanceHistory nextRefundBalanceHistory;

        private BalanceReversalFailedException nextRefundBalanceException;

        private PositionHistory nextReservePositionHistory;

        private Exception nextReservePositionException;

        private PositionHistory nextRollbackPositionReservationHistory;

        private PositionReservationRollbackFailedException nextRollbackPositionReservationException;

        private BalanceHistory nextWithdrawBalanceHistory;

        private Exception nextWithdrawBalanceException;

        public void completeNextCommitPositionReservation(
            final PositionHistory positionHistory) {

            this.nextCommitPositionReservationHistory = positionHistory;
            this.nextCommitPositionReservationException = null;
        }

        public void completeNextDecreasePosition(final PositionHistory positionHistory) {

            this.nextDecreasePositionHistory = positionHistory;
            this.nextDecreasePositionException = null;
        }

        public void completeNextDepositBalance(final BalanceHistory balanceHistory) {

            this.nextDepositBalanceHistory = balanceHistory;
            this.nextDepositBalanceException = null;
        }

        public void completeNextFulfil(final FulfilResult fulfilResult) {

            this.nextFulfilResult = fulfilResult;
            this.nextFulfilException = null;
        }

        public void completeNextIncreasePosition(final PositionHistory positionHistory) {

            this.nextIncreasePositionHistory = positionHistory;
            this.nextIncreasePositionException = null;
        }

        public void completeNextRefundBalance(final BalanceHistory balanceHistory) {

            this.nextRefundBalanceHistory = balanceHistory;
            this.nextRefundBalanceException = null;
        }

        public void completeNextReservePosition(final PositionHistory positionHistory) {

            this.nextReservePositionHistory = positionHistory;
            this.nextReservePositionException = null;
        }

        public void completeNextRollbackPositionReservation(
            final PositionHistory positionHistory) {

            this.nextRollbackPositionReservationHistory = positionHistory;
            this.nextRollbackPositionReservationException = null;
        }

        public void completeNextWithdrawBalance(final BalanceHistory balanceHistory) {

            this.nextWithdrawBalanceHistory = balanceHistory;
            this.nextWithdrawBalanceException = null;
        }

        public int createdWalletCount() {

            return this.wallets.size();
        }

        @Override
        public PositionHistory commitPositionReservation(final PositionUpdateId nextPositionUpdateId,
                                                         final PositionUpdateId reservationId)
            throws PositionReservationCommitFailedException {

            if (this.nextCommitPositionReservationException != null) {

                final var exception = this.nextCommitPositionReservationException;

                this.nextCommitPositionReservationException = null;

                throw exception;
            }

            if (this.nextCommitPositionReservationHistory == null) {
                throw new IllegalStateException(
                    "commitPositionReservation test response is not configured.");
            }

            final var history = this.nextCommitPositionReservationHistory;

            this.nextCommitPositionReservationHistory = null;

            return history;
        }

        @Override
        public void createWallet(final WalletId walletId,
                                 final Currency currency,
                                 final int scale,
                                 final String scenario) throws WalletIdAlreadyTakenException {

            if (this.failNextCreateWallet) {
                this.failNextCreateWallet = false;

                throw new WalletIdAlreadyTakenException(walletId);
            }

            if (this.wallets.containsKey(walletId)) {
                throw new WalletIdAlreadyTakenException(walletId);
            }

            this.wallets.put(walletId, new CreatedWallet(walletId, currency, scale, scenario));
        }

        @Override
        public NdcHistory decreaseNdc(final NdcUpdateId ndcUpdateId,
                                      final TransactionId transactionId,
                                      final Instant transactionAt,
                                      final WalletId walletId,
                                      final BigDecimal amount,
                                      final String description)
            throws NoBalanceUpdateException, PositionReservedExceedsNdcException {

            throw new UnsupportedOperationException("decreaseNdc is not used in these tests.");
        }

        @Override
        public PositionHistory decreasePosition(final PositionUpdateId nextPositionUpdateId,
                                                final TransactionId transactionId,
                                                final Instant transactionAt,
                                                final WalletId walletId,
                                                final BigDecimal amount,
                                                final String description)
            throws NoPositionUpdateException {

            if (this.nextDecreasePositionException != null) {

                final var exception = this.nextDecreasePositionException;

                this.nextDecreasePositionException = null;

                throw exception;
            }

            if (this.nextDecreasePositionHistory == null) {
                throw new IllegalStateException("decreasePosition test response is not configured.");
            }

            final var history = this.nextDecreasePositionHistory;

            this.nextDecreasePositionHistory = null;

            return history;
        }

        @Override
        public BalanceHistory depositBalance(final TransactionId transactionId,
                                             final Instant transactionAt,
                                             final WalletId walletId,
                                             final BigDecimal amount,
                                             final String description,
                                             final BalanceUpdateId nextBalanceUpdateId)
            throws NoBalanceUpdateException {

            if (this.nextDepositBalanceException != null) {

                final var exception = this.nextDepositBalanceException;

                this.nextDepositBalanceException = null;

                throw exception;
            }

            if (this.nextDepositBalanceHistory == null) {
                throw new IllegalStateException("depositBalance test response is not configured.");
            }

            final var history = this.nextDepositBalanceHistory;

            this.nextDepositBalanceHistory = null;

            return history;
        }

        public void failNextCreateWallet() {

            this.failNextCreateWallet = true;
        }

        public void failNextCommitPositionReservation(
            final PositionReservationCommitFailedException exception) {

            this.nextCommitPositionReservationException = exception;
            this.nextCommitPositionReservationHistory = null;
        }

        public void failNextDecreasePosition(final NoPositionUpdateException exception) {

            this.nextDecreasePositionException = exception;
            this.nextDecreasePositionHistory = null;
        }

        public void failNextDepositBalance(final NoBalanceUpdateException exception) {

            this.nextDepositBalanceException = exception;
            this.nextDepositBalanceHistory = null;
        }

        public void failNextFulfil(final NoPositionFulfilmentException exception) {

            this.nextFulfilException = exception;
            this.nextFulfilResult = null;
        }

        public void failNextIncreasePosition(final Exception exception) {

            this.nextIncreasePositionException = exception;
            this.nextIncreasePositionHistory = null;
        }

        public void failNextRefundBalance(final BalanceReversalFailedException exception) {

            this.nextRefundBalanceException = exception;
            this.nextRefundBalanceHistory = null;
        }

        public void failNextReservePosition(final Exception exception) {

            this.nextReservePositionException = exception;
            this.nextReservePositionHistory = null;
        }

        public void failNextRollbackPositionReservation(
            final PositionReservationRollbackFailedException exception) {

            this.nextRollbackPositionReservationException = exception;
            this.nextRollbackPositionReservationHistory = null;
        }

        public void failNextWithdrawBalance(final Exception exception) {

            this.nextWithdrawBalanceException = exception;
            this.nextWithdrawBalanceHistory = null;
        }

        @Override
        public FulfilResult fulfil(final PositionUpdateId reservationId,
                                   final PositionUpdateId reservationCommitId,
                                   final PositionUpdateId positionDecrementId,
                                   final WalletId payeeWalletId,
                                   final String description) throws NoPositionFulfilmentException {

            if (this.nextFulfilException != null) {

                final var exception = this.nextFulfilException;

                this.nextFulfilException = null;

                throw exception;
            }

            if (this.nextFulfilResult == null) {
                throw new IllegalStateException("fulfil test response is not configured.");
            }

            final var fulfilResult = this.nextFulfilResult;

            this.nextFulfilResult = null;

            return fulfilResult;
        }

        @Override
        public NdcHistory increaseNdc(final NdcUpdateId ndcUpdateId,
                                      final TransactionId transactionId,
                                      final Instant transactionAt,
                                      final WalletId walletId,
                                      final BigDecimal amount,
                                      final String description)
            throws NoBalanceUpdateException, BalanceLowerThanNewNdcException {

            throw new UnsupportedOperationException("increaseNdc is not used in these tests.");
        }

        @Override
        public PositionHistory increasePosition(final PositionUpdateId nextPositionUpdateId,
                                                final TransactionId transactionId,
                                                final Instant transactionAt,
                                                final WalletId walletId,
                                                final BigDecimal amount,
                                                final String description)
            throws NoPositionUpdateException, PositionLimitExceededException {

            if (this.nextIncreasePositionException != null) {

                final var exception = this.nextIncreasePositionException;

                this.nextIncreasePositionException = null;

                if (exception instanceof NoPositionUpdateException noPositionUpdateException) {
                    throw noPositionUpdateException;
                }

                if (exception instanceof PositionLimitExceededException positionLimitExceededException) {
                    throw positionLimitExceededException;
                }

                throw new IllegalStateException(
                    "Unsupported increasePosition test exception.", exception);
            }

            if (this.nextIncreasePositionHistory == null) {
                throw new IllegalStateException("increasePosition test response is not configured.");
            }

            final var history = this.nextIncreasePositionHistory;

            this.nextIncreasePositionHistory = null;

            return history;
        }

        @Override
        public BalanceHistory refundBalance(final BalanceUpdateId reversalId,
                                            final BalanceUpdateId nextBalanceUpdateId)
            throws BalanceReversalFailedException {

            if (this.nextRefundBalanceException != null) {

                final var exception = this.nextRefundBalanceException;

                this.nextRefundBalanceException = null;

                throw exception;
            }

            if (this.nextRefundBalanceHistory == null) {
                throw new IllegalStateException("refundBalance test response is not configured.");
            }

            final var history = this.nextRefundBalanceHistory;

            this.nextRefundBalanceHistory = null;

            return history;
        }

        public void reset() {

            this.wallets.clear();
            this.failNextCreateWallet = false;
            this.nextCommitPositionReservationHistory = null;
            this.nextCommitPositionReservationException = null;
            this.nextDecreasePositionHistory = null;
            this.nextDecreasePositionException = null;
            this.nextDepositBalanceHistory = null;
            this.nextDepositBalanceException = null;
            this.nextFulfilResult = null;
            this.nextFulfilException = null;
            this.nextIncreasePositionHistory = null;
            this.nextIncreasePositionException = null;
            this.nextRefundBalanceHistory = null;
            this.nextRefundBalanceException = null;
            this.nextReservePositionHistory = null;
            this.nextReservePositionException = null;
            this.nextRollbackPositionReservationHistory = null;
            this.nextRollbackPositionReservationException = null;
            this.nextWithdrawBalanceHistory = null;
            this.nextWithdrawBalanceException = null;
        }

        @Override
        public PositionHistory reservePosition(final PositionUpdateId nextPositionUpdateId,
                                               final TransactionId transactionId,
                                               final Instant transactionAt,
                                               final WalletId walletId,
                                               final BigDecimal amount,
                                               final String description)
            throws NoPositionUpdateException, PositionLimitExceededException {

            if (this.nextReservePositionException != null) {

                final var exception = this.nextReservePositionException;

                this.nextReservePositionException = null;

                if (exception instanceof NoPositionUpdateException noPositionUpdateException) {
                    throw noPositionUpdateException;
                }

                if (exception instanceof PositionLimitExceededException positionLimitExceededException) {
                    throw positionLimitExceededException;
                }

                throw new IllegalStateException(
                    "Unsupported reservePosition test exception.", exception);
            }

            if (this.nextReservePositionHistory == null) {
                throw new IllegalStateException("reservePosition test response is not configured.");
            }

            final var history = this.nextReservePositionHistory;

            this.nextReservePositionHistory = null;

            return history;
        }

        @Override
        public PositionHistory rollbackPositionReservation(final PositionUpdateId nextPositionUpdateId,
                                                           final PositionUpdateId reservationId)
            throws PositionReservationRollbackFailedException {

            if (this.nextRollbackPositionReservationException != null) {

                final var exception = this.nextRollbackPositionReservationException;

                this.nextRollbackPositionReservationException = null;

                throw exception;
            }

            if (this.nextRollbackPositionReservationHistory == null) {
                throw new IllegalStateException(
                    "rollbackPositionReservation test response is not configured.");
            }

            final var history = this.nextRollbackPositionReservationHistory;

            this.nextRollbackPositionReservationHistory = null;

            return history;
        }

        @Override
        public BalanceHistory withdrawBalance(final BalanceUpdateId nextBalanceUpdateId,
                                              final TransactionId transactionId,
                                              final Instant transactionAt,
                                              final WalletId walletId,
                                              final BigDecimal amount,
                                              final String description)
            throws NoBalanceUpdateException, InsufficientBalanceException {

            if (this.nextWithdrawBalanceException != null) {

                final var exception = this.nextWithdrawBalanceException;

                this.nextWithdrawBalanceException = null;

                if (exception instanceof NoBalanceUpdateException noBalanceUpdateException) {
                    throw noBalanceUpdateException;
                }

                if (exception instanceof InsufficientBalanceException insufficientBalanceException) {
                    throw insufficientBalanceException;
                }

                throw new IllegalStateException(
                    "Unsupported withdrawBalance test exception.", exception);
            }

            if (this.nextWithdrawBalanceHistory == null) {
                throw new IllegalStateException("withdrawBalance test response is not configured.");
            }

            final var history = this.nextWithdrawBalanceHistory;

            this.nextWithdrawBalanceHistory = null;

            return history;
        }

        private record CreatedWallet(WalletId walletId, Currency currency, int scale,
                                     String scenario) { }

    }

}
