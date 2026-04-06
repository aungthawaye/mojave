package org.mojave.wallet.domain.command.balance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.wallet.BalanceAction;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.identifier.wallet.BalanceId;
import org.mojave.common.datatype.identifier.wallet.BalanceUpdateId;
import org.mojave.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.wallet.contract.command.CreateWalletCommand;
import org.mojave.wallet.contract.command.balance.WithdrawBalanceCommand;
import org.mojave.wallet.contract.exception.balance.InsufficientBalanceException;
import org.mojave.wallet.contract.exception.balance.NoBalanceUpdateForTransactionException;
import org.mojave.wallet.contract.engine.WalletEngine;
import org.mojave.wallet.domain.BaseIT;
import org.mojave.wallet.domain.WalletDomainTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        WalletDomainTestConfiguration.class})
@DisplayName("Withdraw Balance Command Integration Test")
public class WithdrawBalanceCommandIT extends BaseIT {

    private static final Instant TRANSACTION_AT = Instant.parse("2026-01-10T10:15:30Z");

    @Autowired
    private CreateWalletCommand createWalletCommand;

    @Autowired
    private WithdrawBalanceCommand withdrawBalanceCommand;

    @Test
    @DisplayName("Throw when withdrawing missing balance")
    public void balanceNotExist() {

        assertThrows(
            RuntimeException.class, () -> this.withdrawBalanceCommand.execute(
                new WithdrawBalanceCommand.Input(
                    new WalletOwnerId(306L), Currency.USD, new BigDecimal("5.00"),
                    new TransactionId(30601L), TRANSACTION_AT, "Withdraw missing balance")));
    }

    @Test
    @DisplayName("Throw when engine returns no balance update")
    public void noBalanceUpdate() {

        this.createDefaultWallet(this.createWalletCommand, 307L, Currency.USD, "Withdraw Wallet");

        final var transactionId = new TransactionId(30701L);

        this.testWalletEngine.failNextWithdrawBalance(
            new WalletEngine.NoBalanceUpdateException(transactionId));

        final var exception = assertThrows(
            NoBalanceUpdateForTransactionException.class, () -> this.withdrawBalanceCommand.execute(
                new WithdrawBalanceCommand.Input(
                    new WalletOwnerId(307L), Currency.USD, new BigDecimal("6.00"),
                    transactionId, TRANSACTION_AT, "Withdraw without update")));

        assertEquals(transactionId, exception.getTransactionId());
    }

    @Test
    @DisplayName("Throw when withdraw exceeds balance")
    public void insufficientBalance() {

        final var walletId = this.createDefaultWallet(
            this.createWalletCommand, 308L, Currency.USD, "Insufficient Wallet");
        final var transactionId = new TransactionId(30801L);

        this.testWalletEngine.failNextWithdrawBalance(
            new WalletEngine.InsufficientBalanceException(
                transactionId, walletId, new BigDecimal("15.00"), new BigDecimal("8.25")));

        final var exception = assertThrows(
            InsufficientBalanceException.class, () -> this.withdrawBalanceCommand.execute(
                new WithdrawBalanceCommand.Input(
                    new WalletOwnerId(308L), Currency.USD, new BigDecimal("15.00"),
                    transactionId, TRANSACTION_AT, "Withdraw too much")));

        assertEquals(new BalanceId(walletId.getId()), exception.getBalanceId());
        assertEquals(new BigDecimal("15.00"), exception.getAmount());
        assertEquals(new BigDecimal("8.25"), exception.getOldBalance());
        assertEquals(transactionId, exception.getTransactionId());
    }

    @Test
    @DisplayName("Withdraw balance successfully")
    public void successful() throws
                             NoBalanceUpdateForTransactionException,
                             InsufficientBalanceException {

        final var walletId = this.createDefaultWallet(
            this.createWalletCommand, 309L, Currency.USD, "Withdraw Wallet");
        final var transactionId = new TransactionId(30901L);
        final var history = this.balanceHistory(
            new BalanceUpdateId(30902L), walletId, BalanceAction.WITHDRAW, transactionId,
            Currency.USD, new BigDecimal("4.75"), new BigDecimal("20.00"),
            new BigDecimal("15.25"), TRANSACTION_AT, null);

        this.testWalletEngine.completeNextWithdrawBalance(history);

        final var output = this.withdrawBalanceCommand.execute(
            new WithdrawBalanceCommand.Input(
                new WalletOwnerId(309L), Currency.USD, new BigDecimal("4.75"),
                transactionId, TRANSACTION_AT, "Withdraw funds"));

        assertEquals(history.balanceUpdateId(), output.balanceUpdateId());
        assertEquals(new BalanceId(walletId.getId()), output.balanceId());
        assertEquals(BalanceAction.WITHDRAW, output.action());
        assertEquals(new BigDecimal("15.25"), output.newBalance());
    }

}
