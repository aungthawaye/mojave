package org.mojave.core.wallet.engine.tigerbeetle.task;

import org.mojave.core.wallet.contract.engine.WalletEngine;
import org.mojave.core.wallet.engine.tigerbeetle.store.TigerBeetleWalletStore;
import org.mojave.core.wallet.engine.tigerbeetle.store.WalletRecord;
import org.mojave.scheme.rule.identifier.transaction.TransactionId;
import org.mojave.scheme.rule.identifier.wallet.NdcUpdateId;
import org.mojave.scheme.rule.identifier.wallet.WalletId;

import java.math.BigDecimal;
import java.time.Instant;

public final class DecreaseNdcTask {

    private DecreaseNdcTask() { }

    public static WalletEngine.NdcHistory execute(final TigerBeetleWalletStore store,
                                                    final NdcUpdateId updateId,
                                                    final TransactionId transactionId,
                                                    final Instant transactionAt,
                                                    final WalletId walletId,
                                                    final BigDecimal amount,
                                                    final String description)
        throws WalletEngine.NoBalanceUpdateException, WalletEngine.PositionReservedExceedsNdcException {

        final var command = new WalletRecord.Command(WalletRecord.Action.DECREASE_NDC,
            updateId.getId(), walletId.getId(), transactionId.getId(), transactionAt, amount,
            0, description, 0, 0);

        try {
            return WalletTaskMapper.ndc(WalletTaskExecutor.execute(store, command));
        } catch (final WalletTaskExecutor.Rejected e) {
            if (e.reason() == WalletTaskExecutor.Reason.NDC_BELOW_POSITION) {
                throw new WalletEngine.PositionReservedExceedsNdcException(walletId, amount,
                    e.state().position(), e.state().reserved(),
                    e.state().ndc().subtract(amount).max(BigDecimal.ZERO), transactionId);
            }
            throw new WalletEngine.NoBalanceUpdateException(transactionId);
        }
    }
}
