package org.mojave.mono.core.admin.api.command.wallet;

import org.mojave.core.wallet.contract.command.CreateWalletCommand;
import org.mojave.mono.core.admin.api.BaseApiController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class WalletCommandController extends BaseApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WalletCommandController.class);

    private final CreateWalletCommand createWalletCommand;

    public WalletCommandController(final CreateWalletCommand createWalletCommand) {

        Objects.requireNonNull(createWalletCommand);

        this.createWalletCommand = createWalletCommand;
    }

    @PostMapping("/wallet/create-wallet")
    public ResponseEntity<CreateWalletCommand.Output> createWallet(
        @RequestBody final CreateWalletCommand.Input input) {

        return this.respond(
            LOGGER,
            "CreateWalletCommand",
            input,
            () -> this.createWalletCommand.execute(input));
    }

}
