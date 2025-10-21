package io.mojaloop.core.transaction.intercom.controller;

import io.mojaloop.core.transaction.contract.command.CommitTransactionCommand;
import io.mojaloop.core.transaction.contract.exception.TransactionIdNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommitTransactionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommitTransactionController.class);

    private final CommitTransactionCommand commitTransactionCommand;

    public CommitTransactionController(final CommitTransactionCommand commitTransactionCommand) {
        assert commitTransactionCommand != null;
        this.commitTransactionCommand = commitTransactionCommand;
    }

    @PostMapping("/transactions/commit")
    public CommitTransactionCommand.Output execute(@Valid @RequestBody final CommitTransactionCommand.Input input)
        throws TransactionIdNotFoundException {

        LOGGER.info("Entering CommitTransactionCommand.execute: input : {}", input);

        final var output = this.commitTransactionCommand.execute(input);

        LOGGER.info("Exiting CommitTransactionCommand.execute: {}", output);

        return output;
    }
}
