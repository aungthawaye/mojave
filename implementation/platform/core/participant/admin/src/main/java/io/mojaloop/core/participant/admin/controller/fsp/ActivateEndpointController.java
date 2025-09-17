package io.mojaloop.core.participant.admin.controller.fsp;

import io.mojaloop.core.participant.contract.command.fsp.ActivateEndpointCommand;
import io.mojaloop.core.participant.contract.exception.fsp.CannotActivateFspEndpointException;
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
public class ActivateEndpointController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivateEndpointController.class);

    private final ActivateEndpointCommand activateEndpointCommand;

    public ActivateEndpointController(ActivateEndpointCommand activateEndpointCommand) {

        assert activateEndpointCommand != null;

        this.activateEndpointCommand = activateEndpointCommand;
    }

    @PostMapping("/fsps/activate-endpoint")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void execute(@Valid @RequestBody ActivateEndpointCommand.Input input)
        throws FspIdNotFoundException, CannotActivateFspEndpointException {

        this.activateEndpointCommand.execute(input);
    }

}
