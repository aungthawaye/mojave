package org.mojave.mono.core.admin.api.command.wallet.balance;

import org.mojave.core.wallet.contract.command.balance.DepositBalanceCommand;
import org.mojave.core.wallet.contract.command.balance.ReverseBalanceWithdrawCommand;
import org.mojave.core.wallet.contract.command.balance.WithdrawBalanceCommand;
import org.mojave.core.wallet.contract.exception.balance.InsufficientBalanceException;
import org.mojave.core.wallet.contract.exception.balance.NoBalanceUpdateForTransactionException;
import org.mojave.core.wallet.contract.exception.balance.ReversalFailedInWalletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class BalanceCommandController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BalanceCommandController.class);

    private final DepositBalanceCommand depositBalanceCommand;

    private final ReverseBalanceWithdrawCommand reverseBalanceWithdrawCommand;

    private final WithdrawBalanceCommand withdrawBalanceCommand;

    public BalanceCommandController(final DepositBalanceCommand depositBalanceCommand,
                                    final ReverseBalanceWithdrawCommand reverseBalanceWithdrawCommand,
                                    final WithdrawBalanceCommand withdrawBalanceCommand) {

        Objects.requireNonNull(depositBalanceCommand);
        Objects.requireNonNull(reverseBalanceWithdrawCommand);
        Objects.requireNonNull(withdrawBalanceCommand);

        this.depositBalanceCommand = depositBalanceCommand;
        this.reverseBalanceWithdrawCommand = reverseBalanceWithdrawCommand;
        this.withdrawBalanceCommand = withdrawBalanceCommand;
    }

    @PostMapping("/wallet/balance/deposit-balance")
    public ResponseEntity<DepositBalanceCommand.Output> depositBalance(
        @RequestBody final DepositBalanceCommand.Input input)
        throws NoBalanceUpdateForTransactionException {

        LOGGER.info("DepositBalanceCommand: input ({})", input);

        final var output = this.depositBalanceCommand.execute(input);

        LOGGER.info("DepositBalanceCommand: output ({})", output);

        return ResponseEntity.ok(output);
    }

    @PostMapping("/wallet/balance/reverse-balance-withdraw")
    public ResponseEntity<ReverseBalanceWithdrawCommand.Output> reverseBalanceWithdraw(
        @RequestBody final ReverseBalanceWithdrawCommand.Input input)
        throws ReversalFailedInWalletException {

        LOGGER.info("ReverseBalanceWithdrawCommand: input ({})", input);

        final var output = this.reverseBalanceWithdrawCommand.execute(input);

        LOGGER.info("ReverseBalanceWithdrawCommand: output ({})", output);

        return ResponseEntity.ok(output);
    }

    @PostMapping("/wallet/balance/withdraw-balance")
    public ResponseEntity<WithdrawBalanceCommand.Output> withdrawBalance(
        @RequestBody final WithdrawBalanceCommand.Input input)
        throws NoBalanceUpdateForTransactionException, InsufficientBalanceException {

        LOGGER.info("WithdrawBalanceCommand: input ({})", input);

        final var output = this.withdrawBalanceCommand.execute(input);

        LOGGER.info("WithdrawBalanceCommand: output ({})", output);

        return ResponseEntity.ok(output);
    }

}
