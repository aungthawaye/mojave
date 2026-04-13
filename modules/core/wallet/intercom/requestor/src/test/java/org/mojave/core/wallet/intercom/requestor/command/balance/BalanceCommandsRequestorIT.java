package org.mojave.core.wallet.intercom.requestor.command.balance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.scheme.rule.identifier.transaction.TransactionId;
import org.mojave.scheme.rule.identifier.wallet.BalanceUpdateId;
import org.mojave.scheme.rule.identifier.wallet.WalletId;
import org.mojave.core.wallet.contract.command.balance.DepositBalanceCommand;
import org.mojave.core.wallet.contract.command.balance.ReverseBalanceWithdrawCommand;
import org.mojave.core.wallet.contract.command.balance.WithdrawBalanceCommand;
import org.mojave.core.wallet.contract.exception.WalletNotFoundException;
import org.mojave.core.wallet.contract.exception.balance.ReversalFailedInWalletException;
import org.mojave.core.wallet.intercom.requestor.WalletIntercomRequestorTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        WalletIntercomRequestorTestConfiguration.class})
@DisplayName("Balance Commands Requestor Integration Test")
public class BalanceCommandsRequestorIT {

    @Autowired
    private DepositBalanceCommand depositBalanceCommand;

    @Autowired
    private ReverseBalanceWithdrawCommand reverseBalanceWithdrawCommand;

    @Autowired
    private WithdrawBalanceCommand withdrawBalanceCommand;

    @Test
    @DisplayName("Execute balance commands through requestor")
    public void successful() {

        final var transactionAt = Instant.now();

        assertThrows(
            WalletNotFoundException.class,
            () -> this.depositBalanceCommand.execute(
                new DepositBalanceCommand.Input(
                    new WalletId(90010101L),
                    new BigDecimal("15.00"),
                    new TransactionId(90010101L),
                    transactionAt,
                    "requestor-deposit-missing-balance")));

        assertThrows(
            WalletNotFoundException.class,
            () -> this.withdrawBalanceCommand.execute(
                new WithdrawBalanceCommand.Input(
                    new WalletId(90010201L),
                    new BigDecimal("5.00"),
                    new TransactionId(90010201L),
                    transactionAt,
                    "requestor-withdraw-missing-balance")));

        assertThrows(
            ReversalFailedInWalletException.class,
            () -> this.reverseBalanceWithdrawCommand.execute(
                new ReverseBalanceWithdrawCommand.Input(
                    new BalanceUpdateId(90010301L),
                    "requestor-reverse-missing-withdraw")));
    }

}
