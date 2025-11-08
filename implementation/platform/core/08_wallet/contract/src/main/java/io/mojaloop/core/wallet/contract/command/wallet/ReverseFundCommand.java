package io.mojaloop.core.wallet.contract.command.wallet;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.core.common.datatype.enums.wallet.BalanceAction;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.wallet.BalanceUpdateId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletId;
import io.mojaloop.core.wallet.contract.exception.wallet.ReversalFailedInWalletException;
import io.mojaloop.fspiop.spec.core.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;

public interface ReverseFundCommand {

    Output execute(Input input) throws ReversalFailedInWalletException;

    record Input(@JsonProperty(required = true) @NotNull BalanceUpdateId reversalId,
                 @JsonProperty(required = true) @NotNull BalanceUpdateId balanceUpdateId,
                 @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_DESCRIPTION_LENGTH) String description) { }

    record Output(BalanceUpdateId balanceUpdateId,
                  WalletId walletId,
                  BalanceAction action,
                  TransactionId transactionId,
                  Currency currency,
                  BigDecimal amount,
                  BigDecimal oldBalance,
                  BigDecimal newBalance,
                  Instant transactionAt,
                  BalanceUpdateId reversalId) { }

}
