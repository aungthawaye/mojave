package io.mojaloop.core.transaction.intercom.controller;

import io.mojaloop.core.transaction.contract.command.OpenTransactionCommand;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpenTransactionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenTransactionController.class);

    private final OpenTransactionCommand openTransactionCommand;

    public OpenTransactionController(final OpenTransactionCommand openTransactionCommand) {
        assert openTransactionCommand != null;
        this.openTransactionCommand = openTransactionCommand;
    }

    @PostMapping("/transactions/open")
    public OpenTransactionCommand.Output execute(@Valid @RequestBody final OpenTransactionCommand.Input input) {

        LOGGER.info("Entering OpenTransactionCommand.execute: input : {}", input);

        final var output = this.openTransactionCommand.execute(input);

        LOGGER.info("Exiting OpenTransactionCommand.execute: {}", output);

        return output;
    }
}
