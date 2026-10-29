package org.mojave.core.wallet.engine.tigerbeetle.task;

import org.mojave.core.wallet.contract.engine.WalletEngine;
import org.mojave.core.wallet.engine.tigerbeetle.store.TigerBeetleWalletStore;
import org.mojave.core.wallet.engine.tigerbeetle.store.WalletRecord;
import org.mojave.scheme.rule.identifier.transaction.TransactionId;
import org.mojave.scheme.rule.identifier.wallet.PositionUpdateId;
import org.mojave.scheme.rule.identifier.wallet.WalletId;

import java.math.BigDecimal;
import java.time.Instant;

public final class ReservePositionTask {

    private ReservePositionTask() { }

    public static WalletEngine.PositionHistory execute(final TigerBeetleWalletStore store,
                                                    final PositionUpdateId updateId,
                                                    final TransactionId transactionId,
                                                    final Instant transactionAt,
                                                    final WalletId walletId,
                                                    final BigDecimal amount,
                                                    final String description)
        throws WalletEngine.NoPositionUpdateException, WalletEngine.PositionLimitExceededException {

        final var command = new WalletRecord.Command(WalletRecord.Action.RESERVE,
            updateId.getId(), walletId.getId(), transactionId.getId(), transactionAt, amount,
            0, description, 0, 0);

        try {
            return WalletTaskMapper.position(WalletTaskExecutor.execute(store, command));
        } catch (final WalletTaskExecutor.Rejected e) {
            if (e.reason() == WalletTaskExecutor.Reason.POSITION_LIMIT) {
                throw new WalletEngine.PositionLimitExceededException(walletId, amount,
                    e.state().position(), e.state().reserved(), e.state().ndc(), transactionId);
            }
            throw new WalletEngine.NoPositionUpdateException(transactionId);
        }
    }
}
