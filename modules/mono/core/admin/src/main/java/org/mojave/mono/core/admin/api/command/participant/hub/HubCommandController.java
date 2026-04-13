package org.mojave.mono.core.admin.api.command.participant.hub;

import org.mojave.core.participant.contract.command.hub.ActivateHubCurrencyCommand;
import org.mojave.core.participant.contract.command.hub.AddHubCurrencyCommand;
import org.mojave.core.participant.contract.command.hub.ChangeHubNameCommand;
import org.mojave.core.participant.contract.command.hub.CreateHubCommand;
import org.mojave.core.participant.contract.command.hub.DeactivateHubCurrencyCommand;
import org.mojave.mono.core.admin.api.BaseApiController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class HubCommandController extends BaseApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HubCommandController.class);

    private final ActivateHubCurrencyCommand activateHubCurrencyCommand;

    private final AddHubCurrencyCommand addHubCurrencyCommand;

    private final ChangeHubNameCommand changeHubNameCommand;

    private final CreateHubCommand createHubCommand;

    private final DeactivateHubCurrencyCommand deactivateHubCurrencyCommand;

    public HubCommandController(final ActivateHubCurrencyCommand activateHubCurrencyCommand,
                                final AddHubCurrencyCommand addHubCurrencyCommand,
                                final ChangeHubNameCommand changeHubNameCommand,
                                final CreateHubCommand createHubCommand,
                                final DeactivateHubCurrencyCommand deactivateHubCurrencyCommand) {

        Objects.requireNonNull(activateHubCurrencyCommand);
        Objects.requireNonNull(addHubCurrencyCommand);
        Objects.requireNonNull(changeHubNameCommand);
        Objects.requireNonNull(createHubCommand);
        Objects.requireNonNull(deactivateHubCurrencyCommand);

        this.activateHubCurrencyCommand = activateHubCurrencyCommand;
        this.addHubCurrencyCommand = addHubCurrencyCommand;
        this.changeHubNameCommand = changeHubNameCommand;
        this.createHubCommand = createHubCommand;
        this.deactivateHubCurrencyCommand = deactivateHubCurrencyCommand;
    }

    @PostMapping("/participant/hubs/activate-hub-currency")
    public ResponseEntity<ActivateHubCurrencyCommand.Output> activateHubCurrency(
        @RequestBody final ActivateHubCurrencyCommand.Input input) {

        return this.respond(
            LOGGER,
            "ActivateHubCurrencyCommand",
            input,
            () -> this.activateHubCurrencyCommand.execute(input));
    }

    @PostMapping("/participant/hubs/add-hub-currency")
    public ResponseEntity<AddHubCurrencyCommand.Output> addHubCurrency(
        @RequestBody final AddHubCurrencyCommand.Input input) {

        return this.respond(
            LOGGER,
            "AddHubCurrencyCommand",
            input,
            () -> this.addHubCurrencyCommand.execute(input));
    }

    @PostMapping("/participant/hubs/change-hub-name")
    public ResponseEntity<ChangeHubNameCommand.Output> changeHubName(
        @RequestBody final ChangeHubNameCommand.Input input) {

        return this.respond(
            LOGGER,
            "ChangeHubNameCommand",
            input,
            () -> this.changeHubNameCommand.execute(input));
    }

    @PostMapping("/participant/hubs/create-hub")
    public ResponseEntity<CreateHubCommand.Output> createHub(
        @RequestBody final CreateHubCommand.Input input) {

        return this.respond(
            LOGGER,
            "CreateHubCommand",
            input,
            () -> this.createHubCommand.execute(input));
    }

    @PostMapping("/participant/hubs/deactivate-hub-currency")
    public ResponseEntity<DeactivateHubCurrencyCommand.Output> deactivateHubCurrency(
        @RequestBody final DeactivateHubCurrencyCommand.Input input) {

        return this.respond(
            LOGGER,
            "DeactivateHubCurrencyCommand",
            input,
            () -> this.deactivateHubCurrencyCommand.execute(input));
    }

}
