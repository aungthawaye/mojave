package org.mojave.core.wallet.domain.command.balance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.wallet.BalanceAction;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.identifier.wallet.WalletId;
import org.mojave.common.datatype.identifier.wallet.BalanceUpdateId;
import org.mojave.core.wallet.contract.command.CreateWalletCommand;
import org.mojave.core.wallet.contract.command.balance.DepositBalanceCommand;
import org.mojave.core.wallet.contract.command.balance.ReverseBalanceWithdrawCommand;
import org.mojave.core.wallet.contract.command.balance.WithdrawBalanceCommand;
import org.mojave.core.wallet.contract.exception.balance.InsufficientBalanceException;
import org.mojave.core.wallet.contract.exception.balance.NoBalanceUpdateForTransactionException;
import org.mojave.core.wallet.contract.exception.balance.ReversalFailedInWalletException;
import org.mojave.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.core.wallet.domain.BaseIT;
import org.mojave.core.wallet.domain.WalletDomainTestConfiguration;
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
@DisplayName("Reverse Balance Withdraw Command Integration Test")
public class ReverseBalanceWithdrawCommandIT extends BaseIT {

    private static final Instant TRANSACTION_AT = Instant.parse("2026-01-10T10:15:30Z");

    @Autowired
    private CreateWalletCommand createWalletCommand;

    @Autowired
    private ReverseBalanceWithdrawCommand reverseBalanceWithdrawCommand;

    @Autowired
    private DepositBalanceCommand depositBalanceCommand;

    @Autowired
    private WithdrawBalanceCommand withdrawBalanceCommand;

    @Test
    @DisplayName("Throw when reversing missing withdraw")
    public void reversalFailed() {

        final var reversalId = new BalanceUpdateId(30401L);

        final var exception = assertThrows(
            ReversalFailedInWalletException.class,
            () -> this.reverseBalanceWithdrawCommand.execute(
                new ReverseBalanceWithdrawCommand.Input(reversalId, "Reverse withdraw")));

        assertEquals(reversalId, exception.getReversalId());
    }

    @Test
    @DisplayName("Reverse withdraw successfully")
    public void successful() throws
                             ReversalFailedInWalletException,
                             NoBalanceUpdateForTransactionException,
                             InsufficientBalanceException {

        final var walletId = this.createDefaultWallet(
            this.createWalletCommand, 305L, Currency.USD, "Reverse Wallet");

        this.depositBalanceCommand.execute(
            new DepositBalanceCommand.Input(
                new WalletOwnerId(305L), Currency.USD, WalletPurpose.ANY, new BigDecimal("20.00"),
                new TransactionId(30501L), TRANSACTION_AT, "Seed balance"));

        final var withdrawOutput = this.withdrawBalanceCommand.execute(
            new WithdrawBalanceCommand.Input(
                new WalletOwnerId(305L), Currency.USD, WalletPurpose.ANY, new BigDecimal("7.50"),
                new TransactionId(30502L), TRANSACTION_AT, "Withdraw before reversal"));

        final var output = this.reverseBalanceWithdrawCommand.execute(
            new ReverseBalanceWithdrawCommand.Input(
                withdrawOutput.balanceUpdateId(), "Reverse previous withdraw"));

        assertNotNull(output.balanceUpdateId());
        assertEquals(new WalletId(walletId.getId()), output.walletId());
        assertEquals(BalanceAction.REVERSE_WITHDRAW, output.action());
        assertEquals(withdrawOutput.balanceUpdateId(), output.withdrawId());
        assertEquals(0, output.newBalance().compareTo(new BigDecimal("20.00")));
    }

}
