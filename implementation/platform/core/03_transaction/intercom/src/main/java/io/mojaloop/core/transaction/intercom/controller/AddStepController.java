package io.mojaloop.core.transaction.intercom.controller;

import io.mojaloop.core.transaction.contract.command.AddStepCommand;
import io.mojaloop.core.transaction.contract.exception.TransactionIdNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AddStepController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddStepController.class);

    private final AddStepCommand addStepCommand;

    public AddStepController(final AddStepCommand addStepCommand) {
        assert addStepCommand != null;
        this.addStepCommand = addStepCommand;
    }

    @PostMapping("/transactions/add-step")
    public AddStepCommand.Output execute(@Valid @RequestBody final AddStepCommand.Input input)
        throws TransactionIdNotFoundException {

        LOGGER.info("Entering AddStepCommand.execute: input : {}", input);

        final var output = this.addStepCommand.execute(input);

        LOGGER.info("Exiting AddStepCommand.execute: {}", output);

        return output;
    }
}
