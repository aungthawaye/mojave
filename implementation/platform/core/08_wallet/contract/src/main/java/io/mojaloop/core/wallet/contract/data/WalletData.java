package io.mojaloop.core.wallet.contract.data;

import io.mojaloop.core.common.datatype.identifier.wallet.WalletId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.fspiop.spec.core.Currency;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public record WalletData(WalletId walletId, WalletOwnerId walletOwnerId, Currency currency, String name, BigDecimal balance, Instant createdAt) {

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof WalletData walletData)) {
            return false;
        }
        return Objects.equals(walletId, walletData.walletId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(walletId);
    }

}
