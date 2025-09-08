package io.mojaloop.core.participant.admin.controller.fsp;

import io.mojaloop.core.participant.contract.command.fsp.DeactivateEndpointCommand;
import io.mojaloop.core.participant.contract.exception.FspIdNotFoundException;
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
public class DeactivateEndpointController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeactivateEndpointController.class);

    private final DeactivateEndpointCommand deactivateEndpointCommand;

    public DeactivateEndpointController(DeactivateEndpointCommand deactivateEndpointCommand) {

        assert deactivateEndpointCommand != null;

        this.deactivateEndpointCommand = deactivateEndpointCommand;
    }

    @PostMapping("/fsps/deactivate-endpoint")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void execute(@Valid @RequestBody DeactivateEndpointCommand.Input input)
        throws FspIdNotFoundException {

        this.deactivateEndpointCommand.execute(input);
    }

}
