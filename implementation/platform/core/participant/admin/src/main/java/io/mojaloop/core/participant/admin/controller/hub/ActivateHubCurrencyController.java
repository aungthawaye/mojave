package io.mojaloop.core.participant.admin.controller.hub;

import io.mojaloop.core.participant.contract.command.hub.ActivateHubCurrencyCommand;
import io.mojaloop.core.participant.contract.exception.HubIdNotFoundException;
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
public class ActivateHubCurrencyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivateHubCurrencyController.class);

    private final ActivateHubCurrencyCommand activateHubCurrencyCommand;

    public ActivateHubCurrencyController(ActivateHubCurrencyCommand activateHubCurrencyCommand) {

        assert activateHubCurrencyCommand != null;

        this.activateHubCurrencyCommand = activateHubCurrencyCommand;
    }

    @PostMapping("/hubs/activate-currency")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ActivateHubCurrencyCommand.Output execute(@Valid @RequestBody ActivateHubCurrencyCommand.Input input)
        throws HubIdNotFoundException {

        return this.activateHubCurrencyCommand.execute(input);
    }
}
