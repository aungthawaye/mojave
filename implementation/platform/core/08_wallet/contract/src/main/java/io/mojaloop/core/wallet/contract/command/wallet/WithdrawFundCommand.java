package io.mojaloop.core.wallet.contract.command.wallet;

import io.mojaloop.core.common.datatype.enums.wallet.BalanceAction;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.wallet.BalanceUpdateId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletId;
import io.mojaloop.core.wallet.contract.exception.wallet.InsufficientBalanceInWalletException;
import io.mojaloop.core.wallet.contract.exception.wallet.NoBalanceUpdateForTransactionException;
import io.mojaloop.fspiop.spec.core.Currency;

import java.math.BigDecimal;
import java.time.Instant;

public interface WithdrawFundCommand {

    Output execute(Input input) throws NoBalanceUpdateForTransactionException, InsufficientBalanceInWalletException;

    record Input(WalletId walletId, BigDecimal amount, TransactionId transactionId, Instant transactionAt, String description) { }

    record Output(BalanceUpdateId balanceUpdateId,
                  WalletId walletId,
                  BalanceAction action,
                  TransactionId transactionId,
                  Currency currency,
                  BigDecimal amount,
                  BigDecimal oldBalance,
                  BigDecimal newBalance,
                  Instant transactionAt) { }

}
