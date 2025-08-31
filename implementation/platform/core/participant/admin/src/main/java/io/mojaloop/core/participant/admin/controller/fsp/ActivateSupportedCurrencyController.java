package io.mojaloop.core.participant.admin.controller.fsp;

import io.mojaloop.core.participant.contract.command.fsp.ActivateSupportedCurrencyCommand;
import io.mojaloop.core.participant.contract.exception.CannotActivateSupportedCurrencyException;
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
public class ActivateSupportedCurrencyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivateSupportedCurrencyController.class);

    private final ActivateSupportedCurrencyCommand activateSupportedCurrencyCommand;

    public ActivateSupportedCurrencyController(ActivateSupportedCurrencyCommand activateSupportedCurrencyCommand) {

        assert activateSupportedCurrencyCommand != null;

        this.activateSupportedCurrencyCommand = activateSupportedCurrencyCommand;
    }

    @PostMapping("/fsps/activate-supported-currency")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ActivateSupportedCurrencyCommand.Output execute(@Valid @RequestBody ActivateSupportedCurrencyCommand.Input input)
        throws FspIdNotFoundException, CannotActivateSupportedCurrencyException {

        return this.activateSupportedCurrencyCommand.execute(input);
    }

}
