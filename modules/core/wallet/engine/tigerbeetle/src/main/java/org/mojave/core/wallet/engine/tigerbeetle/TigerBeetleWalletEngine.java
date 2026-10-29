package org.mojave.core.wallet.engine.tigerbeetle;

import com.tigerbeetle.Client;
import com.tigerbeetle.UInt128;
import org.mojave.core.wallet.contract.engine.WalletEngine;
import org.mojave.core.wallet.engine.tigerbeetle.store.TigerBeetleWalletStore;
import org.mojave.core.wallet.engine.tigerbeetle.task.CommitPositionReservationTask;
import org.mojave.core.wallet.engine.tigerbeetle.task.CreateWalletTask;
import org.mojave.core.wallet.engine.tigerbeetle.task.DecreaseNdcTask;
import org.mojave.core.wallet.engine.tigerbeetle.task.DecreasePositionTask;
import org.mojave.core.wallet.engine.tigerbeetle.task.DepositBalanceTask;
import org.mojave.core.wallet.engine.tigerbeetle.task.FulfilPositionsTask;
import org.mojave.core.wallet.engine.tigerbeetle.task.IncreaseNdcTask;
import org.mojave.core.wallet.engine.tigerbeetle.task.IncreasePositionTask;
import org.mojave.core.wallet.engine.tigerbeetle.task.RefundBalanceTask;
import org.mojave.core.wallet.engine.tigerbeetle.task.ReservePositionTask;
import org.mojave.core.wallet.engine.tigerbeetle.task.RollbackPositionReservationTask;
import org.mojave.core.wallet.engine.tigerbeetle.task.WithdrawBalanceTask;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.identifier.transaction.TransactionId;
import org.mojave.scheme.rule.identifier.wallet.BalanceUpdateId;
import org.mojave.scheme.rule.identifier.wallet.NdcUpdateId;
import org.mojave.scheme.rule.identifier.wallet.PositionUpdateId;
import org.mojave.scheme.rule.identifier.wallet.WalletId;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public final class TigerBeetleWalletEngine implements WalletEngine, AutoCloseable {

    private final TigerBeetleWalletStore store;

    public TigerBeetleWalletEngine(final WalletClusterSettings settings) {

        Objects.requireNonNull(settings, "settings");
        this.store = new TigerBeetleWalletStore(new Client(UInt128.asBytes(settings.clusterId()),
            settings.replicaAddresses().toArray(String[]::new)));
    }

    @Override
    public PositionHistory commitPositionReservation(final PositionUpdateId nextPositionUpdateId,
                                                     final PositionUpdateId reservationId)
        throws PositionReservationCommitFailedException {

        return CommitPositionReservationTask.execute(
            store, nextPositionUpdateId, reservationId);
    }

    @Override
    public void createWallet(final WalletId walletId,
                             final Currency currency,
                             final int scale,
                             final String tag)
        throws WalletIdAlreadyTakenException {

        CreateWalletTask.execute(store, walletId, currency, scale, tag);
    }

    @Override
    public NdcHistory decreaseNdc(final NdcUpdateId ndcUpdateId,
                                  final TransactionId transactionId,
                                  final Instant transactionAt,
                                  final WalletId walletId,
                                  final BigDecimal amount,
                                  final String description)
        throws NoBalanceUpdateException, PositionReservedExceedsNdcException {

        return DecreaseNdcTask.execute(
            store, ndcUpdateId, transactionId, transactionAt, walletId, amount,
            description);
    }

    @Override
    public PositionHistory decreasePosition(final PositionUpdateId nextPositionUpdateId,
                                            final TransactionId transactionId,
                                            final Instant transactionAt,
                                            final WalletId walletId,
                                            final BigDecimal amount,
                                            final String description)
        throws NoPositionUpdateException {

        return DecreasePositionTask.execute(
            store, nextPositionUpdateId, transactionId, transactionAt, walletId,
            amount, description);
    }

    @Override
    public BalanceHistory depositBalance(final TransactionId transactionId,
                                         final Instant transactionAt,
                                         final WalletId walletId,
                                         final BigDecimal amount,
                                         final String description,
                                         final BalanceUpdateId nextBalanceUpdateId)
        throws WalletEngine.NoBalanceUpdateException {

        return DepositBalanceTask.execute(
            store, nextBalanceUpdateId, transactionId, transactionAt, walletId, amount, description);
    }

    @Override
    public PositionFulfilmentResult fulfilPositions(final PositionUpdateId reservationId,
                                                    final PositionUpdateId reservationCommitId,
                                                    final PositionUpdateId positionDecrementId,
                                                    final WalletId payeeWalletId,
                                                    final String description) throws NoPositionFulfilmentException {

        return FulfilPositionsTask.execute(
            store, reservationId, reservationCommitId, positionDecrementId,
            payeeWalletId, description);
    }

    @Override
    public NdcHistory increaseNdc(final NdcUpdateId ndcUpdateId,
                                  final TransactionId transactionId,
                                  final Instant transactionAt,
                                  final WalletId walletId,
                                  final BigDecimal amount,
                                  final String description)
        throws NoBalanceUpdateException, BalanceLowerThanNewNdcException {

        return IncreaseNdcTask.execute(
            store, ndcUpdateId, transactionId, transactionAt, walletId, amount,
            description);
    }

    @Override
    public PositionHistory increasePosition(final PositionUpdateId nextPositionUpdateId,
                                            final TransactionId transactionId,
                                            final Instant transactionAt,
                                            final WalletId walletId,
                                            final BigDecimal amount,
                                            final String description)
        throws NoPositionUpdateException, PositionLimitExceededException {

        return IncreasePositionTask.execute(
            store, nextPositionUpdateId, transactionId, transactionAt, walletId,
            amount, description);
    }

    @Override
    public BalanceHistory reverseBalanceWithdraw(final BalanceUpdateId reversalId,
                                                 final BalanceUpdateId nextBalanceUpdateId)
        throws BalanceReversalFailedException {

        return RefundBalanceTask.execute(store, nextBalanceUpdateId, reversalId);
    }

    @Override
    public PositionHistory reservePosition(final PositionUpdateId nextPositionUpdateId,
                                           final TransactionId transactionId,
                                           final Instant transactionAt,
                                           final WalletId walletId,
                                           final BigDecimal amount,
                                           final String description)
        throws NoPositionUpdateException, PositionLimitExceededException {

        return ReservePositionTask.execute(
            store, nextPositionUpdateId, transactionId, transactionAt, walletId,
            amount, description);
    }

    @Override
    public PositionHistory rollbackPositionReservation(final PositionUpdateId nextPositionUpdateId,
                                                       final PositionUpdateId reservationId)
        throws PositionReservationRollbackFailedException {

        return RollbackPositionReservationTask.execute(
            store, nextPositionUpdateId, reservationId);
    }

    @Override
    public BalanceHistory withdrawBalance(final BalanceUpdateId nextBalanceUpdateId,
                                          final TransactionId transactionId,
                                          final Instant transactionAt,
                                          final WalletId walletId,
                                          final BigDecimal amount,
                                          final String description)
        throws NoBalanceUpdateException, InsufficientBalanceException {

        return WithdrawBalanceTask.execute(
            store, nextBalanceUpdateId, transactionId, transactionAt, walletId,
            amount, description);
    }

    @Override
    public void close() {

        store.close();
    }

    public record WalletClusterSettings(BigInteger clusterId, List<String> replicaAddresses) {

        public WalletClusterSettings {

            Objects.requireNonNull(clusterId, "clusterId");
            replicaAddresses = List.copyOf(replicaAddresses);
            if (clusterId.signum() < 0 || clusterId.bitLength() > 128 || replicaAddresses.isEmpty() ||
                replicaAddresses.stream().anyMatch(String::isBlank)) {
                throw new IllegalArgumentException("Valid unsigned 128-bit cluster ID and replica addresses required");
            }
        }
    }
}
