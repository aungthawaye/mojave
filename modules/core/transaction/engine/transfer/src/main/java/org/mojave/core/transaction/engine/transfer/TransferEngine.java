package org.mojave.core.transaction.engine.transfer;

import org.mojave.scheme.rule.identifier.transaction.TransactionId;
import org.mojave.scheme.rule.identifier.wallet.PositionUpdateId;
import org.mojave.component.misc.handy.Snowflake;
import org.mojave.core.wallet.contract.engine.WalletEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Objects;

public abstract class TransferEngine<P> implements org.mojave.core.transaction.contract.engine.TransferEngine<P> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferEngine.class);

    private final WalletEngine walletEngine;

    private final TransactionId transactionId;

    private final Instant transactionAt;

    public TransferEngine(WalletEngine walletEngine) {

        Objects.requireNonNull(walletEngine);

        this.walletEngine = walletEngine;
        this.transactionId = new TransactionId(Snowflake.get().nextId());
        this.transactionAt = Instant.now();
    }

    public void reserve(P parameter, ReservationParameterResolver<P> walletResolver)
        throws WalletEngine.PositionLimitExceededException, WalletEngine.NoPositionUpdateException {

        var sourceWalletId = walletResolver.soureWalletId(parameter);
        var destinationWalletId = walletResolver.destinationWalletId(parameter);
        var transferAmount = walletResolver.transferAmount(parameter);
        var otherAmounts = walletResolver.otherAmounts(parameter);
        var description = walletResolver.description(parameter);

        this.walletEngine.reservePosition(
            new PositionUpdateId(Snowflake.get().nextId()), this.transactionId, this.transactionAt,
            sourceWalletId, transferAmount, description);
    }


}
