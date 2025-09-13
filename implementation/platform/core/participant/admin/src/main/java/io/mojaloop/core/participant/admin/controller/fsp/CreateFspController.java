package io.mojaloop.core.participant.admin.controller.fsp;

import io.mojaloop.core.participant.contract.command.fsp.CreateFspCommand;
import io.mojaloop.core.participant.contract.exception.fsp.FspCurrencyAlreadySupportedException;
import io.mojaloop.core.participant.contract.exception.fsp.FspEndpointAlreadyConfiguredException;
import io.mojaloop.core.participant.contract.exception.fsp.FspCodeAlreadyExistsException;
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
public class CreateFspController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateFspController.class.getName());

    private final CreateFspCommand createFspCommand;

    public CreateFspController(CreateFspCommand createFspCommand) {

        assert createFspCommand != null;

        this.createFspCommand = createFspCommand;
    }

    @PostMapping("/fsps/create-fsp")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CreateFspCommand.Output execute(@Valid @RequestBody CreateFspCommand.Input input)
        throws FspCodeAlreadyExistsException, FspEndpointAlreadyConfiguredException, FspCurrencyAlreadySupportedException {

        return this.createFspCommand.execute(input);
    }

}
