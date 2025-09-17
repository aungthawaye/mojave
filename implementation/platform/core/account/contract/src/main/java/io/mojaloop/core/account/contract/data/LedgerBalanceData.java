package io.mojaloop.core.account.contract.data;

import io.mojaloop.core.common.datatype.enums.account.OverdraftMode;
import io.mojaloop.core.common.datatype.enums.account.Side;
import io.mojaloop.core.common.datatype.identifier.account.AccountId;
import io.mojaloop.fspiop.spec.core.Currency;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public record LedgerBalanceData(AccountId accountId,
                                Currency currency,
                                int scale,
                                Side nature,
                                BigDecimal postedDebits,
                                BigDecimal postedCredits,
                                OverdraftMode overdraftMode,
                                BigDecimal overdraftLimit,
                                Instant createdAt) {

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof LedgerBalanceData that)) {
            return false;
        }
        return Objects.equals(accountId, that.accountId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(accountId);
    }

}