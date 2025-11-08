package io.mojaloop.core.wallet.admin.controller;

import io.mojaloop.core.wallet.contract.command.position.CreatePositionCommand;
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
public class CreatePositionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreatePositionController.class);

    private final CreatePositionCommand createPositionCommand;

    public CreatePositionController(final CreatePositionCommand createPositionCommand) {

        assert createPositionCommand != null;

        this.createPositionCommand = createPositionCommand;
    }

    @PostMapping("/positions")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CreatePositionCommand.Output execute(@Valid @RequestBody final CreatePositionCommand.Input input) {

        LOGGER.info("Entering CreatePositionCommand.execute: input : {}", input);

        final var output = this.createPositionCommand.execute(input);

        LOGGER.info("Exiting CreatePositionCommand.execute: {}", output);

        return output;
    }
}
