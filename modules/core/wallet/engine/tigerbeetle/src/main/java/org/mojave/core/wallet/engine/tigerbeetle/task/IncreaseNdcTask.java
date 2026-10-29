package org.mojave.core.wallet.engine.tigerbeetle.task;

import org.mojave.core.wallet.contract.engine.WalletEngine;
import org.mojave.core.wallet.engine.tigerbeetle.store.TigerBeetleWalletStore;
import org.mojave.core.wallet.engine.tigerbeetle.store.WalletRecord;
import org.mojave.scheme.rule.identifier.transaction.TransactionId;
import org.mojave.scheme.rule.identifier.wallet.NdcUpdateId;
import org.mojave.scheme.rule.identifier.wallet.WalletId;

import java.math.BigDecimal;
import java.time.Instant;

public final class IncreaseNdcTask {

    private IncreaseNdcTask() { }

    public static WalletEngine.NdcHistory execute(final TigerBeetleWalletStore store,
                                                    final NdcUpdateId updateId,
                                                    final TransactionId transactionId,
                                                    final Instant transactionAt,
                                                    final WalletId walletId,
                                                    final BigDecimal amount,
                                                    final String description)
        throws WalletEngine.NoBalanceUpdateException, WalletEngine.BalanceLowerThanNewNdcException {

        final var command = new WalletRecord.Command(WalletRecord.Action.INCREASE_NDC,
            updateId.getId(), walletId.getId(), transactionId.getId(), transactionAt, amount,
            0, description, 0, 0);

        try {
            return WalletTaskMapper.ndc(WalletTaskExecutor.execute(store, command));
        } catch (final WalletTaskExecutor.Rejected e) {
            if (e.reason() == WalletTaskExecutor.Reason.NDC_EXCEEDS_BALANCE) {
                throw new WalletEngine.BalanceLowerThanNewNdcException(walletId, amount,
                    e.state().balance(), e.state().ndc().add(amount), transactionId);
            }
            throw new WalletEngine.NoBalanceUpdateException(transactionId);
        }
    }
}
