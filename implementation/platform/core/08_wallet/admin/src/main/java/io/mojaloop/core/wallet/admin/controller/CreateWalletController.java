package io.mojaloop.core.wallet.admin.controller;

import io.mojaloop.core.wallet.contract.command.wallet.CreateWalletCommand;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreateWalletController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateWalletController.class);

    private final CreateWalletCommand createWalletCommand;

    public CreateWalletController(final CreateWalletCommand createWalletCommand) {

        assert createWalletCommand != null;

        this.createWalletCommand = createWalletCommand;
    }

    @PostMapping("/wallets")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CreateWalletCommand.Output execute(@Valid @RequestBody final CreateWalletCommand.Input input) {

        LOGGER.info("Entering CreateWalletCommand.execute: input : {}", input);

        final var output = this.createWalletCommand.execute(input);

        LOGGER.info("Exiting CreateWalletCommand.execute: {}", output);

        return output;
    }
}
