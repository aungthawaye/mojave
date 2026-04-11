package org.mojave.wallet.domain.command.balance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.wallet.BalanceAction;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.identifier.wallet.WalletId;
import org.mojave.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.wallet.contract.command.CreateWalletCommand;
import org.mojave.wallet.contract.command.balance.DepositBalanceCommand;
import org.mojave.wallet.contract.exception.WalletNotFoundException;
import org.mojave.wallet.contract.exception.balance.NoBalanceUpdateForTransactionException;
import org.mojave.wallet.domain.BaseIT;
import org.mojave.wallet.domain.WalletDomainTestConfiguration;
import org.mojave.scheme.rule.wallet.WalletPurpose;
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
            WalletNotFoundException.class, () -> this.depositBalanceCommand.execute(
                new DepositBalanceCommand.Input(
                    new WalletOwnerId(301L), Currency.USD, WalletPurpose.ANY, new BigDecimal("25.00"),
                    new TransactionId(30101L), TRANSACTION_AT, "Deposit missing balance")));

        assertEquals(new WalletOwnerId(301L), exception.getWalletOwnerId());
        assertEquals(Currency.USD, exception.getCurrency());
        assertEquals(WalletPurpose.ANY, exception.getPurpose());
    }

    @Test
    @DisplayName("Throw when engine returns no balance update")
    public void noBalanceUpdate() throws NoBalanceUpdateForTransactionException {

        this.createDefaultWallet(this.createWalletCommand, 302L, Currency.USD, "Deposit Wallet");

        final var transactionId = new TransactionId(30201L);
        final var input = new DepositBalanceCommand.Input(
            new WalletOwnerId(302L), Currency.USD, WalletPurpose.ANY, new BigDecimal("12.50"), transactionId,
            TRANSACTION_AT, "Deposit without update");

        this.depositBalanceCommand.execute(input);

        final var exception = assertThrows(
            NoBalanceUpdateForTransactionException.class,
            () -> this.depositBalanceCommand.execute(input));

        assertEquals(transactionId, exception.getTransactionId());
    }

    @Test
    @DisplayName("Deposit balance successfully")
    public void successful() throws NoBalanceUpdateForTransactionException {

        final var walletId = this.createDefaultWallet(
            this.createWalletCommand, 303L, Currency.USD, "Deposit Wallet");
        final var transactionId = new TransactionId(30301L);

        final var output = this.depositBalanceCommand.execute(
            new DepositBalanceCommand.Input(
                new WalletOwnerId(303L), Currency.USD, WalletPurpose.ANY, new BigDecimal("25.50"),
                transactionId, TRANSACTION_AT, "Deposit funds"));

        assertNotNull(output.balanceUpdateId());
        assertEquals(new WalletId(walletId.getId()), output.walletId());
        assertEquals(BalanceAction.DEPOSIT, output.action());
        assertEquals(0, output.oldBalance().compareTo(new BigDecimal("0.00")));
        assertEquals(0, output.newBalance().compareTo(new BigDecimal("25.50")));
    }

}
