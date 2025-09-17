package io.mojaloop.core.participant.admin.controller.fsp;

import io.mojaloop.core.participant.contract.command.fsp.DeactivateFspCommand;
import io.mojaloop.core.participant.contract.exception.fsp.FspIdNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeactivateFspController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeactivateFspController.class);

    private final DeactivateFspCommand deactivateFspCommand;

    public DeactivateFspController(DeactivateFspCommand deactivateFspCommand) {

        assert deactivateFspCommand != null;

        this.deactivateFspCommand = deactivateFspCommand;
    }

    @PostMapping("/fsps/deactivate-fsp")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void execute(@Valid @RequestBody DeactivateFspCommand.Input input) throws FspIdNotFoundException {

        this.deactivateFspCommand.execute(input);
    }

}
