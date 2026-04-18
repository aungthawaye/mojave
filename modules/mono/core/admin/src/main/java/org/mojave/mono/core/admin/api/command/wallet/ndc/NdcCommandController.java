package org.mojave.mono.core.admin.api.command.wallet.ndc;

import org.mojave.core.wallet.contract.command.ndc.DecreaseNdcCommand;
import org.mojave.core.wallet.contract.command.ndc.IncreaseNdcCommand;
import org.mojave.core.wallet.contract.exception.WalletNotFoundException;
import org.mojave.core.wallet.contract.exception.balance.NoBalanceUpdateForTransactionException;
import org.mojave.core.wallet.contract.exception.ndc.BalanceLowerThanNewNdcException;
import org.mojave.core.wallet.contract.exception.ndc.PositionReservedExceedsNdcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class NdcCommandController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NdcCommandController.class);

    private final IncreaseNdcCommand increaseNdcCommand;

    private final DecreaseNdcCommand decreaseNdcCommand;

    public NdcCommandController(final IncreaseNdcCommand increaseNdcCommand,
                                final DecreaseNdcCommand decreaseNdcCommand) {

        Objects.requireNonNull(increaseNdcCommand);
        Objects.requireNonNull(decreaseNdcCommand);

        this.increaseNdcCommand = increaseNdcCommand;
        this.decreaseNdcCommand = decreaseNdcCommand;
    }

    @PostMapping("/wallet/ndc/increase-ndc")
    public ResponseEntity<IncreaseNdcCommand.Output> increaseNdc(
        @RequestBody final IncreaseNdcCommand.Input input) throws
                                                         NoBalanceUpdateForTransactionException,
                                                         BalanceLowerThanNewNdcException,
                                                         WalletNotFoundException {

        LOGGER.info("IncreaseNdcCommand: input ({})", input);

        final var output = this.increaseNdcCommand.execute(input);

        LOGGER.info("IncreaseNdcCommand: output ({})", output);

        return ResponseEntity.ok(output);
    }

    @PostMapping("/wallet/ndc/decrease-ndc")
    public ResponseEntity<DecreaseNdcCommand.Output> decreaseNdc(
        @RequestBody final DecreaseNdcCommand.Input input) throws
                                                         NoBalanceUpdateForTransactionException,
                                                         PositionReservedExceedsNdcException,
                                                         WalletNotFoundException {

        LOGGER.info("DecreaseNdcCommand: input ({})", input);

        final var output = this.decreaseNdcCommand.execute(input);

        LOGGER.info("DecreaseNdcCommand: output ({})", output);

        return ResponseEntity.ok(output);
    }

}
