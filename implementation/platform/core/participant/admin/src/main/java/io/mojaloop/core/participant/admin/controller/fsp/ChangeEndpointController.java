package io.mojaloop.core.participant.admin.controller.fsp;

import io.mojaloop.core.participant.contract.command.fsp.ChangeEndpointCommand;
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
public class ChangeEndpointController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeEndpointController.class);

    private final ChangeEndpointCommand changeEndpointCommand;

    public ChangeEndpointController(ChangeEndpointCommand changeEndpointCommand) {

        assert changeEndpointCommand != null;

        this.changeEndpointCommand = changeEndpointCommand;
    }

    @PostMapping("/fsps/change-endpoint")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ChangeEndpointCommand.Output execute(@Valid @RequestBody ChangeEndpointCommand.Input input) throws FspIdNotFoundException {

        return this.changeEndpointCommand.execute(input);
    }

}
