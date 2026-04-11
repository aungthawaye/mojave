package org.mojave.core.wallet.contract.engine;

import lombok.Getter;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.wallet.BalanceAction;
import org.mojave.common.datatype.enums.wallet.PositionAction;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.identifier.wallet.BalanceUpdateId;
import org.mojave.common.datatype.identifier.wallet.NdcUpdateId;
import org.mojave.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.common.datatype.identifier.wallet.WalletId;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public interface WalletEngine {

    PositionHistory commitPositionReservation(PositionUpdateId nextPositionUpdateId,
                                              PositionUpdateId reservationId)
        throws PositionReservationCommitFailedException;

    void createWallet(WalletId walletId, Currency currency, int scale, String tag)
        throws WalletIdAlreadyTakenException;

    NdcHistory decreaseNdc(NdcUpdateId ndcUpdateId,
                           TransactionId transactionId,
                           Instant transactionAt,
                           WalletId walletId,
                           BigDecimal amount,
                           String description)
        throws NoBalanceUpdateException, PositionReservedExceedsNdcException;

    PositionHistory decreasePosition(PositionUpdateId nextPositionUpdateId,
                                     TransactionId transactionId,
                                     Instant transactionAt,
                                     WalletId walletId,
                                     BigDecimal amount,
                                     String description) throws NoPositionUpdateException;

    BalanceHistory depositBalance(TransactionId transactionId,
                                  Instant transactionAt,
                                  WalletId walletId,
                                  BigDecimal amount,
                                  String description,
                                  BalanceUpdateId nextBalanceUpdateId)
        throws WalletEngine.NoBalanceUpdateException;

    FulfilResult fulfil(PositionUpdateId reservationId,
                        PositionUpdateId reservationCommitId,
                        PositionUpdateId positionDecrementId,
                        WalletId payeeWalletId,
                        String description) throws NoPositionFulfilmentException;

    NdcHistory increaseNdc(NdcUpdateId ndcUpdateId,
                           TransactionId transactionId,
                           Instant transactionAt,
                           WalletId walletId,
                           BigDecimal amount,
                           String description)
        throws NoBalanceUpdateException, BalanceLowerThanNewNdcException;

    PositionHistory increasePosition(PositionUpdateId nextPositionUpdateId,
                                     TransactionId transactionId,
                                     Instant transactionAt,
                                     WalletId walletId,
                                     BigDecimal amount,
                                     String description)
        throws NoPositionUpdateException, PositionLimitExceededException;

    BalanceHistory refundBalance(BalanceUpdateId reversalId, BalanceUpdateId nextBalanceUpdateId)
        throws BalanceReversalFailedException;

    PositionHistory reservePosition(PositionUpdateId nextPositionUpdateId,
                                    TransactionId transactionId,
                                    Instant transactionAt,
                                    WalletId walletId,
                                    BigDecimal amount,
                                    String description)
        throws NoPositionUpdateException, PositionLimitExceededException;

    PositionHistory rollbackPositionReservation(PositionUpdateId nextPositionUpdateId,
                                                PositionUpdateId reservationId)
        throws PositionReservationRollbackFailedException;

    BalanceHistory withdrawBalance(BalanceUpdateId nextBalanceUpdateId,
                                   TransactionId transactionId,
                                   Instant transactionAt,
                                   WalletId walletId,
                                   BigDecimal amount,
                                   String description)
        throws NoBalanceUpdateException, InsufficientBalanceException;

    @Getter
    class WalletIdAlreadyTakenException extends Exception {

        private final WalletId walletId;

        public WalletIdAlreadyTakenException(WalletId walletId) {

            Objects.requireNonNull(walletId);

            super("Wallet ID (" + walletId.getId() + ") is already taken.");

            this.walletId = walletId;
        }

    }

    record BalanceHistory(BalanceUpdateId balanceUpdateId,
                          WalletId walletId,
                          BalanceAction action,
                          TransactionId transactionId,
                          Currency currency,
                          BigDecimal amount,
                          BigDecimal oldBalance,
                          BigDecimal newBalance,
                          Instant transactionAt,
                          BalanceUpdateId reversalId) { }

    @Getter
    class NoBalanceUpdateException extends Exception {

        private final TransactionId transactionId;

        public NoBalanceUpdateException(TransactionId transactionId) {

            super("No balance update found for transactionId: " + transactionId);

            this.transactionId = transactionId;
        }

    }

    @Getter
    class InsufficientBalanceException extends Exception {

        private final TransactionId transactionId;

        private final WalletId walletId;

        private final BigDecimal amount;

        private final BigDecimal oldBalance;

        public InsufficientBalanceException(TransactionId transactionId,
                                            WalletId walletId,
                                            BigDecimal amount,
                                            BigDecimal oldBalance) {

            super("Insufficient balance in walletId: " + walletId + " amount: " + amount +
                      " transactionId: " + transactionId + " oldBalance: " +
                      oldBalance.stripTrailingZeros().toPlainString());

            this.transactionId = transactionId;
            this.walletId = walletId;
            this.amount = amount;
            this.oldBalance = oldBalance;
        }

    }

    @Getter
    class BalanceReversalFailedException extends Exception {

        private final BalanceUpdateId reversalId;

        public BalanceReversalFailedException(BalanceUpdateId reversalId) {

            super("Balance Reversal failed for balance update id: " + reversalId);

            this.reversalId = reversalId;
        }

    }

    @Getter
    class PositionReservedExceedsNdcException extends Exception {

        private final WalletId walletId;

        private final BigDecimal amount;

        private final BigDecimal position;

        private final BigDecimal reserved;

        private final BigDecimal newNdc;

        private final TransactionId transactionId;

        public PositionReservedExceedsNdcException(final WalletId walletId,
                                                   final BigDecimal amount,
                                                   final BigDecimal position,
                                                   final BigDecimal reserved,
                                                   final BigDecimal newNdc,
                                                   final TransactionId transactionId) {

            super("Position plus reserved exceeds new NDC for walletId: " + walletId +
                      ", amount: " + amount +
                      ", position: " + position +
                      ", reserved: " + reserved +
                      ", newNdc: " + newNdc +
                      ", transactionId: " + transactionId);

            this.walletId = walletId;
            this.amount = amount;
            this.position = position;
            this.reserved = reserved;
            this.newNdc = newNdc;
            this.transactionId = transactionId;
        }

    }

    @Getter
    class BalanceLowerThanNewNdcException extends Exception {

        private final WalletId walletId;

        private final BigDecimal amount;

        private final BigDecimal balance;

        private final BigDecimal newNdc;

        private final TransactionId transactionId;

        public BalanceLowerThanNewNdcException(final WalletId walletId,
                                               final BigDecimal amount,
                                               final BigDecimal balance,
                                               final BigDecimal newNdc,
                                               final TransactionId transactionId) {

            super("Balance is lower than new NDC for walletId: " + walletId +
                      ", amount: " + amount +
                      ", balance: " + balance +
                      ", newNdc: " + newNdc +
                      ", transactionId: " + transactionId);

            this.walletId = walletId;
            this.amount = amount;
            this.balance = balance;
            this.newNdc = newNdc;
            this.transactionId = transactionId;
        }

    }

    record NdcHistory(NdcUpdateId ndcUpdateId,
                      WalletId walletId,
                      PositionAction action,
                      TransactionId transactionId,
                      Currency currency,
                      BigDecimal amount,
                      BigDecimal oldNdc,
                      BigDecimal newNdc,
                      Instant transactionAt) { }

    record PositionHistory(PositionUpdateId positionUpdateId,
                           WalletId walletId,
                           PositionAction action,
                           TransactionId transactionId,
                           Currency currency,
                           BigDecimal amount,
                           BigDecimal oldPosition,
                           BigDecimal newPosition,
                           BigDecimal oldReserved,
                           BigDecimal newReserved,
                           BigDecimal netDebitCap,
                           Instant transactionAt) { }

    record FulfilResult(PositionUpdateId payerCommitmentId, PositionUpdateId payeeCommitmentId) { }

    @Getter
    class NoPositionUpdateException extends Exception {

        private final TransactionId transactionId;

        public NoPositionUpdateException(TransactionId transactionId) {

            super("No position update found for transactionId: " + transactionId);
            this.transactionId = transactionId;
        }

    }

    @Getter
    class NoPositionFulfilmentException extends Exception {

        private final PositionUpdateId reservationId;

        public NoPositionFulfilmentException(PositionUpdateId reservationId) {

            super("No position fulfilment found for reservationId: " + reservationId);
            this.reservationId = reservationId;
        }

    }

    @Getter
    class PositionLimitExceededException extends Exception {

        private final WalletId walletId;

        private final BigDecimal amount;

        private final BigDecimal oldPosition;

        private final BigDecimal oldReserved;

        private final BigDecimal netDebitCap;

        private final TransactionId transactionId;

        public PositionLimitExceededException(WalletId walletId,
                                              BigDecimal amount,
                                              BigDecimal oldPosition,
                                              BigDecimal oldReserved,
                                              BigDecimal netDebitCap,
                                              TransactionId transactionId) {

            super("Position limit exceeded for walletId: " + walletId + ", amount: " + amount +
                      ", oldPosition: " + oldPosition + ", oldReserved: " + oldReserved +
                      ", netDebitCap: " + netDebitCap.stripTrailingZeros().toPlainString() +
                      ", transactionId: " + transactionId);

            this.walletId = walletId;
            this.amount = amount;
            this.oldPosition = oldPosition;
            this.oldReserved = oldReserved;
            this.netDebitCap = netDebitCap;
            this.transactionId = transactionId;
        }

    }

    @Getter
    class PositionReservationCommitFailedException extends Exception {

        private final PositionUpdateId reservationId;

        public PositionReservationCommitFailedException(PositionUpdateId reservationId) {

            super("Position reservation ommit failed for reservationId: " + reservationId);
            this.reservationId = reservationId;
        }

    }

    @Getter
    class PositionReservationRollbackFailedException extends Exception {

        private final PositionUpdateId reservationId;

        public PositionReservationRollbackFailedException(PositionUpdateId reservationId) {

            super("Position reservation rollback failed for reservationId: " + reservationId);
            this.reservationId = reservationId;
        }

    }

}
