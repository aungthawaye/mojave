package io.mojaloop.core.participant.admin.controller.fsp;

import io.mojaloop.core.participant.contract.command.fsp.ActivateFspCommand;
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
public class ActivateFspController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivateFspController.class);

    private final ActivateFspCommand activateFspCommand;

    public ActivateFspController(ActivateFspCommand activateFspCommand) {

        assert activateFspCommand != null;

        this.activateFspCommand = activateFspCommand;
    }

    @PostMapping("/fsps/activate-fsp")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void execute(@Valid @RequestBody ActivateFspCommand.Input input)
        throws FspIdNotFoundException {

        this.activateFspCommand.execute(input);
    }

}
