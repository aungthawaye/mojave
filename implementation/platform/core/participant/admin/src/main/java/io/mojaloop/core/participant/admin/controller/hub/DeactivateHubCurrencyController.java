package io.mojaloop.core.participant.admin.controller.hub;

import io.mojaloop.core.participant.contract.command.hub.DeactivateHubCurrencyCommand;
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
public class DeactivateHubCurrencyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeactivateHubCurrencyController.class);

    private final DeactivateHubCurrencyCommand deactivateHubCurrencyCommand;

    public DeactivateHubCurrencyController(DeactivateHubCurrencyCommand deactivateHubCurrencyCommand) {

        assert deactivateHubCurrencyCommand != null;

        this.deactivateHubCurrencyCommand = deactivateHubCurrencyCommand;
    }

    @PostMapping("/hubs/deactivate-currency")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public DeactivateHubCurrencyCommand.Output execute(@Valid @RequestBody DeactivateHubCurrencyCommand.Input input)
        throws HubIdNotFoundException {

        return this.deactivateHubCurrencyCommand.execute(input);
    }
}
