package io.mojaloop.core.wallet.contract.data;

import io.mojaloop.core.common.datatype.enums.wallet.BalanceAction;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.wallet.BalanceUpdateId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletId;
import io.mojaloop.fspiop.spec.core.Currency;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public record BalanceUpdateData(BalanceUpdateId balanceUpdateId,
                               WalletId walletId,
                               BalanceAction action,
                               TransactionId transactionId,
                               Currency currency,
                               BigDecimal amount,
                               BigDecimal oldBalance,
                               BigDecimal newBalance,
                               String description,
                               Instant transactionAt,
                               Instant createdAt,
                               BalanceUpdateId reversedId) {

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof BalanceUpdateData balanceUpdateData)) {
            return false;
        }
        return Objects.equals(balanceUpdateId, balanceUpdateData.balanceUpdateId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(balanceUpdateId);
    }
}
