package io.mojaloop.core.participant.admin.controller.hub;

import io.mojaloop.core.participant.contract.command.hub.ChangeHubNameCommand;
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
public class ChangeHubNameController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeHubNameController.class);

    private final ChangeHubNameCommand changeHubNameCommand;

    public ChangeHubNameController(ChangeHubNameCommand changeHubNameCommand) {

        assert changeHubNameCommand != null;

        this.changeHubNameCommand = changeHubNameCommand;
    }

    @PostMapping("/hubs/change-name")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ChangeHubNameCommand.Output execute(@Valid @RequestBody ChangeHubNameCommand.Input input) throws HubIdNotFoundException {

        return this.changeHubNameCommand.execute(input);
    }
}
