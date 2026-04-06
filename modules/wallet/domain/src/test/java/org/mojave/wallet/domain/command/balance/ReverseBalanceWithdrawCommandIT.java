package org.mojave.wallet.domain.command.balance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.wallet.BalanceAction;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.identifier.wallet.BalanceId;
import org.mojave.common.datatype.identifier.wallet.BalanceUpdateId;
import org.mojave.wallet.contract.command.CreateWalletCommand;
import org.mojave.wallet.contract.command.balance.ReverseBalanceWithdrawCommand;
import org.mojave.wallet.contract.exception.balance.ReversalFailedInWalletException;
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
@DisplayName("Reverse Balance Withdraw Command Integration Test")
public class ReverseBalanceWithdrawCommandIT extends BaseIT {

    private static final Instant TRANSACTION_AT = Instant.parse("2026-01-10T10:15:30Z");

    @Autowired
    private CreateWalletCommand createWalletCommand;

    @Autowired
    private ReverseBalanceWithdrawCommand reverseBalanceWithdrawCommand;

    @Test
    @DisplayName("Throw when reversing missing withdraw")
    public void reversalFailed() {

        final var reversalId = new BalanceUpdateId(30401L);

        this.testWalletEngine.failNextRefundBalance(
            new WalletEngine.BalanceReversalFailedException(reversalId));

        final var exception = assertThrows(
            ReversalFailedInWalletException.class,
            () -> this.reverseBalanceWithdrawCommand.execute(
                new ReverseBalanceWithdrawCommand.Input(reversalId, "Reverse withdraw")));

        assertEquals(reversalId, exception.getReversalId());
    }

    @Test
    @DisplayName("Reverse withdraw successfully")
    public void successful() throws ReversalFailedInWalletException {

        final var walletId = this.createDefaultWallet(
            this.createWalletCommand, 305L, Currency.USD, "Reverse Wallet");
        final var withdrawId = new BalanceUpdateId(30501L);
        final var history = this.balanceHistory(
            new BalanceUpdateId(30502L), walletId, BalanceAction.REVERSE_WITHDRAW,
            new TransactionId(30503L), Currency.USD, new BigDecimal("7.50"),
            new BigDecimal("20.00"), new BigDecimal("27.50"), TRANSACTION_AT, withdrawId);

        this.testWalletEngine.completeNextRefundBalance(history);

        final var output = this.reverseBalanceWithdrawCommand.execute(
            new ReverseBalanceWithdrawCommand.Input(withdrawId, "Reverse previous withdraw"));

        assertEquals(history.balanceUpdateId(), output.balanceUpdateId());
        assertEquals(new BalanceId(walletId.getId()), output.balanceId());
        assertEquals(BalanceAction.REVERSE_WITHDRAW, output.action());
        assertEquals(withdrawId, output.withdrawId());
    }

}
