package io.mojaloop.core.participant.admin.controller.fsp;

import io.mojaloop.core.participant.contract.command.fsp.AddEndpointCommand;
import io.mojaloop.core.participant.contract.exception.fsp.FspEndpointAlreadyConfiguredException;
import io.mojaloop.core.participant.contract.exception.fsp.FspIdNotFoundException;
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
public class AddEndpointController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddEndpointController.class);

    private final AddEndpointCommand addEndpointCommand;

    public AddEndpointController(AddEndpointCommand addEndpointCommand) {

        assert addEndpointCommand != null;

        this.addEndpointCommand = addEndpointCommand;
    }

    @PostMapping("/fsps/add-endpoint")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public AddEndpointCommand.Output execute(@Valid @RequestBody AddEndpointCommand.Input input)
        throws FspIdNotFoundException, FspEndpointAlreadyConfiguredException {

        return this.addEndpointCommand.execute(input);

    }

}
