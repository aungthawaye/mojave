package io.mojaloop.core.participant.admin.controller.fsp;

import io.mojaloop.core.participant.contract.command.fsp.DeactivateFspCurrencyCommand;
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
public class DeactivateFspCurrencyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeactivateFspCurrencyController.class);

    private final DeactivateFspCurrencyCommand deactivateFspCurrencyCommand;

    public DeactivateFspCurrencyController(DeactivateFspCurrencyCommand deactivateFspCurrencyCommand) {

        assert deactivateFspCurrencyCommand != null;

        this.deactivateFspCurrencyCommand = deactivateFspCurrencyCommand;
    }

    @PostMapping("/fsps/deactivate-currency")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void execute(@Valid @RequestBody DeactivateFspCurrencyCommand.Input input) throws FspIdNotFoundException {

        this.deactivateFspCurrencyCommand.execute(input);
    }

}
