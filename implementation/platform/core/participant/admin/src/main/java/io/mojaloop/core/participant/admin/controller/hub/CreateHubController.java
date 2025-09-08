package io.mojaloop.core.participant.admin.controller.hub;

import io.mojaloop.core.participant.contract.command.hub.CreateHubCommand;
import io.mojaloop.core.participant.contract.exception.CurrencyAlreadySupportedException;
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
public class CreateHubController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateHubController.class.getName());

    private final CreateHubCommand createHubCommand;

    public CreateHubController(CreateHubCommand createHubCommand) {

        assert createHubCommand != null;

        this.createHubCommand = createHubCommand;
    }

    @PostMapping("/hubs/create-hub")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CreateHubCommand.Output execute(@Valid @RequestBody CreateHubCommand.Input input)
        throws CurrencyAlreadySupportedException, io.mojaloop.core.participant.contract.exception.HubLimitReachedException {

        return this.createHubCommand.execute(input);
    }
}
