package io.mojaloop.core.participant.admin.controller.fsp;

import io.mojaloop.core.participant.contract.command.fsp.AddFspCurrencyCommand;
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
public class AddFspCurrencyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddFspCurrencyController.class);

    private final AddFspCurrencyCommand addFspCurrencyCommand;

    public AddFspCurrencyController(AddFspCurrencyCommand addFspCurrencyCommand) {

        assert addFspCurrencyCommand != null;

        this.addFspCurrencyCommand = addFspCurrencyCommand;
    }

    @PostMapping("/fsps/add-currency")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public AddFspCurrencyCommand.Output execute(@Valid @RequestBody AddFspCurrencyCommand.Input input)
        throws FspIdNotFoundException, CurrencyAlreadySupportedException {

        return this.addFspCurrencyCommand.execute(input);
    }

}
