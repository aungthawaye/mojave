package io.mojaloop.core.participant.admin.controller.hub;

import io.mojaloop.core.participant.contract.command.hub.AddHubCurrencyCommand;
import io.mojaloop.core.participant.contract.exception.fsp.FspCurrencyAlreadySupportedException;
import io.mojaloop.core.participant.contract.exception.hub.HubIdNotFoundException;
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
public class AddHubCurrencyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddHubCurrencyController.class);

    private final AddHubCurrencyCommand addHubCurrencyCommand;

    public AddHubCurrencyController(AddHubCurrencyCommand addHubCurrencyCommand) {

        assert addHubCurrencyCommand != null;

        this.addHubCurrencyCommand = addHubCurrencyCommand;
    }

    @PostMapping("/hubs/add-currency")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public AddHubCurrencyCommand.Output execute(@Valid @RequestBody AddHubCurrencyCommand.Input input)
        throws HubIdNotFoundException, FspCurrencyAlreadySupportedException {

        return this.addHubCurrencyCommand.execute(input);
    }
}
