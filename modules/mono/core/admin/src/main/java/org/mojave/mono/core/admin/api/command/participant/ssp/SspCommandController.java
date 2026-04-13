package org.mojave.mono.core.admin.api.command.participant.ssp;

import org.mojave.core.participant.contract.command.ssp.ActivateSspCommand;
import org.mojave.core.participant.contract.command.ssp.ActivateSspCurrencyCommand;
import org.mojave.core.participant.contract.command.ssp.AddSspCurrencyCommand;
import org.mojave.core.participant.contract.command.ssp.ChangeSspEndpointCommand;
import org.mojave.core.participant.contract.command.ssp.ChangeSspNameCommand;
import org.mojave.core.participant.contract.command.ssp.CreateSspCommand;
import org.mojave.core.participant.contract.command.ssp.DeactivateSspCommand;
import org.mojave.core.participant.contract.command.ssp.DeactivateSspCurrencyCommand;
import org.mojave.core.participant.contract.command.ssp.TerminateSspCommand;
import org.mojave.mono.core.admin.api.BaseApiController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class SspCommandController extends BaseApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SspCommandController.class);

    private final ActivateSspCommand activateSspCommand;

    private final ActivateSspCurrencyCommand activateSspCurrencyCommand;

    private final AddSspCurrencyCommand addSspCurrencyCommand;

    private final ChangeSspEndpointCommand changeSspEndpointCommand;

    private final ChangeSspNameCommand changeSspNameCommand;

    private final CreateSspCommand createSspCommand;

    private final DeactivateSspCommand deactivateSspCommand;

    private final DeactivateSspCurrencyCommand deactivateSspCurrencyCommand;

    private final TerminateSspCommand terminateSspCommand;

    public SspCommandController(final ActivateSspCommand activateSspCommand,
                                final ActivateSspCurrencyCommand activateSspCurrencyCommand,
                                final AddSspCurrencyCommand addSspCurrencyCommand,
                                final ChangeSspEndpointCommand changeSspEndpointCommand,
                                final ChangeSspNameCommand changeSspNameCommand,
                                final CreateSspCommand createSspCommand,
                                final DeactivateSspCommand deactivateSspCommand,
                                final DeactivateSspCurrencyCommand deactivateSspCurrencyCommand,
                                final TerminateSspCommand terminateSspCommand) {

        Objects.requireNonNull(activateSspCommand);
        Objects.requireNonNull(activateSspCurrencyCommand);
        Objects.requireNonNull(addSspCurrencyCommand);
        Objects.requireNonNull(changeSspEndpointCommand);
        Objects.requireNonNull(changeSspNameCommand);
        Objects.requireNonNull(createSspCommand);
        Objects.requireNonNull(deactivateSspCommand);
        Objects.requireNonNull(deactivateSspCurrencyCommand);
        Objects.requireNonNull(terminateSspCommand);

        this.activateSspCommand = activateSspCommand;
        this.activateSspCurrencyCommand = activateSspCurrencyCommand;
        this.addSspCurrencyCommand = addSspCurrencyCommand;
        this.changeSspEndpointCommand = changeSspEndpointCommand;
        this.changeSspNameCommand = changeSspNameCommand;
        this.createSspCommand = createSspCommand;
        this.deactivateSspCommand = deactivateSspCommand;
        this.deactivateSspCurrencyCommand = deactivateSspCurrencyCommand;
        this.terminateSspCommand = terminateSspCommand;
    }

    @PostMapping("/participant/ssps/activate-ssp")
    public ResponseEntity<ActivateSspCommand.Output> activateSsp(
        @RequestBody final ActivateSspCommand.Input input) {

        return this.respond(
            LOGGER,
            "ActivateSspCommand",
            input,
            () -> this.activateSspCommand.execute(input));
    }

    @PostMapping("/participant/ssps/activate-ssp-currency")
    public ResponseEntity<ActivateSspCurrencyCommand.Output> activateSspCurrency(
        @RequestBody final ActivateSspCurrencyCommand.Input input) {

        return this.respond(
            LOGGER,
            "ActivateSspCurrencyCommand",
            input,
            () -> this.activateSspCurrencyCommand.execute(input));
    }

    @PostMapping("/participant/ssps/add-ssp-currency")
    public ResponseEntity<AddSspCurrencyCommand.Output> addSspCurrency(
        @RequestBody final AddSspCurrencyCommand.Input input) {

        return this.respond(
            LOGGER,
            "AddSspCurrencyCommand",
            input,
            () -> this.addSspCurrencyCommand.execute(input));
    }

    @PostMapping("/participant/ssps/change-ssp-endpoint")
    public ResponseEntity<ChangeSspEndpointCommand.Output> changeSspEndpoint(
        @RequestBody final ChangeSspEndpointCommand.Input input) {

        return this.respond(
            LOGGER,
            "ChangeSspEndpointCommand",
            input,
            () -> this.changeSspEndpointCommand.execute(input));
    }

    @PostMapping("/participant/ssps/change-ssp-name")
    public ResponseEntity<ChangeSspNameCommand.Output> changeSspName(
        @RequestBody final ChangeSspNameCommand.Input input) {

        return this.respond(
            LOGGER,
            "ChangeSspNameCommand",
            input,
            () -> this.changeSspNameCommand.execute(input));
    }

    @PostMapping("/participant/ssps/create-ssp")
    public ResponseEntity<CreateSspCommand.Output> createSsp(
        @RequestBody final CreateSspCommand.Input input) {

        return this.respond(
            LOGGER,
            "CreateSspCommand",
            input,
            () -> this.createSspCommand.execute(input));
    }

    @PostMapping("/participant/ssps/deactivate-ssp")
    public ResponseEntity<DeactivateSspCommand.Output> deactivateSsp(
        @RequestBody final DeactivateSspCommand.Input input) {

        return this.respond(
            LOGGER,
            "DeactivateSspCommand",
            input,
            () -> this.deactivateSspCommand.execute(input));
    }

    @PostMapping("/participant/ssps/deactivate-ssp-currency")
    public ResponseEntity<DeactivateSspCurrencyCommand.Output> deactivateSspCurrency(
        @RequestBody final DeactivateSspCurrencyCommand.Input input) {

        return this.respond(
            LOGGER,
            "DeactivateSspCurrencyCommand",
            input,
            () -> this.deactivateSspCurrencyCommand.execute(input));
    }

    @PostMapping("/participant/ssps/terminate-ssp")
    public ResponseEntity<TerminateSspCommand.Output> terminateSsp(
        @RequestBody final TerminateSspCommand.Input input) {

        return this.respond(
            LOGGER,
            "TerminateSspCommand",
            input,
            () -> this.terminateSspCommand.execute(input));
    }

}
