package org.mojave.core.wallet.engine.tigerbeetle.task;

import org.mojave.core.wallet.contract.engine.WalletEngine;
import org.mojave.core.wallet.engine.tigerbeetle.store.TigerBeetleWalletStore;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.identifier.wallet.WalletId;

public final class CreateWalletTask {

    private CreateWalletTask() { }

    public static void execute(final TigerBeetleWalletStore store, final WalletId walletId,
                               final Currency currency, final int scale, final String tag)
        throws WalletEngine.WalletIdAlreadyTakenException {

        if (!store.createWallet(walletId.getId(), currency, scale, tag)) {
            throw new WalletEngine.WalletIdAlreadyTakenException(walletId);
        }
    }
}
