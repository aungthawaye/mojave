package org.mojave.core.wallet.domain.command.balance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.wallet.BalanceAction;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.identifier.wallet.WalletId;
import org.mojave.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.core.wallet.contract.command.CreateWalletCommand;
import org.mojave.core.wallet.contract.command.balance.DepositBalanceCommand;
import org.mojave.core.wallet.contract.command.balance.WithdrawBalanceCommand;
import org.mojave.core.wallet.contract.exception.WalletNotFoundException;
import org.mojave.core.wallet.contract.exception.balance.InsufficientBalanceException;
import org.mojave.core.wallet.contract.exception.balance.NoBalanceUpdateForTransactionException;
import org.mojave.core.wallet.domain.BaseIT;
import org.mojave.core.wallet.domain.WalletDomainTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    @Autowired
    private DepositBalanceCommand depositBalanceCommand;

    @Test
    @DisplayName("Throw when withdrawing missing balance")
    public void balanceNotExist() {

        assertThrows(
            WalletNotFoundException.class, () -> this.withdrawBalanceCommand.execute(
                new WithdrawBalanceCommand.Input(
                    new WalletOwnerId(306L), Currency.USD, "P2P_TRANSFER", new BigDecimal("5.00"),
                    new TransactionId(30601L), TRANSACTION_AT, "Withdraw missing balance")));
    }

    @Test
    @DisplayName("Throw when engine returns no balance update")
    public void noBalanceUpdate() throws
                                  NoBalanceUpdateForTransactionException,
                                  InsufficientBalanceException {

        this.createDefaultWallet(this.createWalletCommand, 307L, Currency.USD, "Withdraw Wallet");
        this.depositBalanceCommand.execute(
            new DepositBalanceCommand.Input(
                new WalletOwnerId(307L), Currency.USD, "P2P_TRANSFER", new BigDecimal("20.00"),
                new TransactionId(30700L), TRANSACTION_AT, "Seed balance"));

        final var transactionId = new TransactionId(30701L);
        final var input = new WithdrawBalanceCommand.Input(
            new WalletOwnerId(307L), Currency.USD, "P2P_TRANSFER", new BigDecimal("6.00"),
            transactionId, TRANSACTION_AT, "Withdraw without update");

        this.withdrawBalanceCommand.execute(input);

        final var exception = assertThrows(
            NoBalanceUpdateForTransactionException.class,
            () -> this.withdrawBalanceCommand.execute(input));

        assertEquals(transactionId, exception.getTransactionId());
    }

    @Test
    @DisplayName("Throw when withdraw exceeds balance")
    public void insufficientBalance() throws NoBalanceUpdateForTransactionException {

        final var walletId = this.createDefaultWallet(
            this.createWalletCommand, 308L, Currency.USD, "Insufficient Wallet");
        this.depositBalanceCommand.execute(
            new DepositBalanceCommand.Input(
                new WalletOwnerId(308L), Currency.USD, "P2P_TRANSFER", new BigDecimal("8.25"),
                new TransactionId(30800L), TRANSACTION_AT, "Seed balance"));
        final var transactionId = new TransactionId(30801L);

        final var exception = assertThrows(
            InsufficientBalanceException.class, () -> this.withdrawBalanceCommand.execute(
                new WithdrawBalanceCommand.Input(
                    new WalletOwnerId(308L), Currency.USD, "P2P_TRANSFER", new BigDecimal("15.00"),
                    transactionId, TRANSACTION_AT, "Withdraw too much")));

        assertEquals(new WalletId(walletId.getId()), exception.getWalletId());
        assertEquals(0, exception.getAmount().compareTo(new BigDecimal("15.00")));
        assertEquals(0, exception.getOldBalance().compareTo(new BigDecimal("8.25")));
        assertEquals(transactionId, exception.getTransactionId());
    }

    @Test
    @DisplayName("Withdraw balance successfully")
    public void successful() throws
                             NoBalanceUpdateForTransactionException,
                             InsufficientBalanceException {

        final var walletId = this.createDefaultWallet(
            this.createWalletCommand, 309L, Currency.USD, "Withdraw Wallet");
        this.depositBalanceCommand.execute(
            new DepositBalanceCommand.Input(
                new WalletOwnerId(309L), Currency.USD, "P2P_TRANSFER", new BigDecimal("20.00"),
                new TransactionId(30900L), TRANSACTION_AT, "Seed balance"));
        final var transactionId = new TransactionId(30901L);

        final var output = this.withdrawBalanceCommand.execute(
            new WithdrawBalanceCommand.Input(
                new WalletOwnerId(309L), Currency.USD, "P2P_TRANSFER", new BigDecimal("4.75"),
                transactionId, TRANSACTION_AT, "Withdraw funds"));

        assertNotNull(output.balanceUpdateId());
        assertEquals(new WalletId(walletId.getId()), output.walletId());
        assertEquals(BalanceAction.WITHDRAW, output.action());
        assertEquals(0, output.oldBalance().compareTo(new BigDecimal("20.00")));
        assertEquals(0, output.newBalance().compareTo(new BigDecimal("15.25")));
    }

}
