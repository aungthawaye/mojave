package io.mojaloop.core.participant.admin.controller.fsp;

import io.mojaloop.core.participant.contract.command.fsp.AddSupportedCurrencyCommand;
import io.mojaloop.core.participant.contract.exception.CurrencyAlreadySupportedException;
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
public class AddSupportedCurrencyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddSupportedCurrencyController.class);

    private final AddSupportedCurrencyCommand addSupportedCurrencyCommand;

    public AddSupportedCurrencyController(AddSupportedCurrencyCommand addSupportedCurrencyCommand) {

        assert addSupportedCurrencyCommand != null;

        this.addSupportedCurrencyCommand = addSupportedCurrencyCommand;
    }

    @PostMapping("/fsps/add-supported-currency")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public AddSupportedCurrencyCommand.Output execute(@Valid @RequestBody AddSupportedCurrencyCommand.Input input)
        throws FspIdNotFoundException, CurrencyAlreadySupportedException {

        return this.addSupportedCurrencyCommand.execute(input);
    }

}
