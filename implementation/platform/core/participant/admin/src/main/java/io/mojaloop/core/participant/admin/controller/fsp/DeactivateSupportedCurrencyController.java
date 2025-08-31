package io.mojaloop.core.participant.admin.controller.fsp;

import io.mojaloop.core.participant.contract.command.fsp.DeactivateSupportedCurrencyCommand;
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
public class DeactivateSupportedCurrencyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeactivateSupportedCurrencyController.class);

    private final DeactivateSupportedCurrencyCommand deactivateSupportedCurrencyCommand;

    public DeactivateSupportedCurrencyController(DeactivateSupportedCurrencyCommand deactivateSupportedCurrencyCommand) {

        assert deactivateSupportedCurrencyCommand != null;

        this.deactivateSupportedCurrencyCommand = deactivateSupportedCurrencyCommand;
    }

    @PostMapping("/fsps/deactivate-supported-currency")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public DeactivateSupportedCurrencyCommand.Output execute(@Valid @RequestBody DeactivateSupportedCurrencyCommand.Input input) throws FspIdNotFoundException {

        return this.deactivateSupportedCurrencyCommand.execute(input);
    }

}
