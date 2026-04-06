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
import org.mojave.wallet.contract.command.balance.DepositBalanceCommand;
import org.mojave.wallet.contract.exception.balance.BalanceNotExistException;
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
@DisplayName("Deposit Balance Command Integration Test")
public class DepositBalanceCommandIT extends BaseIT {

    private static final Instant TRANSACTION_AT = Instant.parse("2026-01-10T10:15:30Z");

    @Autowired
    private CreateWalletCommand createWalletCommand;

    @Autowired
    private DepositBalanceCommand depositBalanceCommand;

    @Test
    @DisplayName("Throw when depositing to missing balance")
    public void balanceNotExist() {

        final var exception = assertThrows(
            BalanceNotExistException.class, () -> this.depositBalanceCommand.execute(
                new DepositBalanceCommand.Input(
                    new WalletOwnerId(301L), Currency.USD, new BigDecimal("25.00"),
                    new TransactionId(30101L), TRANSACTION_AT, "Deposit missing balance")));

        assertEquals(new WalletOwnerId(301L), exception.getWalletOwnerId());
        assertEquals(Currency.USD, exception.getCurrency());
    }

    @Test
    @DisplayName("Throw when engine returns no balance update")
    public void noBalanceUpdate() {

        this.createDefaultWallet(this.createWalletCommand, 302L, Currency.USD, "Deposit Wallet");

        final var transactionId = new TransactionId(30201L);

        this.testWalletEngine.failNextDepositBalance(
            new WalletEngine.NoBalanceUpdateException(transactionId));

        final var exception = assertThrows(
            NoBalanceUpdateForTransactionException.class, () -> this.depositBalanceCommand.execute(
                new DepositBalanceCommand.Input(
                    new WalletOwnerId(302L), Currency.USD, new BigDecimal("12.50"),
                    transactionId, TRANSACTION_AT, "Deposit without update")));

        assertEquals(transactionId, exception.getTransactionId());
    }

    @Test
    @DisplayName("Deposit balance successfully")
    public void successful() throws NoBalanceUpdateForTransactionException {

        final var walletId = this.createDefaultWallet(
            this.createWalletCommand, 303L, Currency.USD, "Deposit Wallet");
        final var transactionId = new TransactionId(30301L);
        final var history = this.balanceHistory(
            new BalanceUpdateId(30302L), walletId, BalanceAction.DEPOSIT, transactionId,
            Currency.USD, new BigDecimal("25.50"), new BigDecimal("10.00"),
            new BigDecimal("35.50"), TRANSACTION_AT, null);

        this.testWalletEngine.completeNextDepositBalance(history);

        final var output = this.depositBalanceCommand.execute(
            new DepositBalanceCommand.Input(
                new WalletOwnerId(303L), Currency.USD, new BigDecimal("25.50"),
                transactionId, TRANSACTION_AT, "Deposit funds"));

        assertEquals(history.balanceUpdateId(), output.balanceUpdateId());
        assertEquals(new BalanceId(walletId.getId()), output.balanceId());
        assertEquals(BalanceAction.DEPOSIT, output.action());
        assertEquals(new BigDecimal("35.50"), output.newBalance());
    }

}
