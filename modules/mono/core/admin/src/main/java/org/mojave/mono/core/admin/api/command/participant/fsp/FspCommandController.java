package org.mojave.mono.core.admin.api.command.participant.fsp;

import org.mojave.core.participant.contract.command.fsp.ActivateEndpointCommand;
import org.mojave.core.participant.contract.command.fsp.ActivateFspCommand;
import org.mojave.core.participant.contract.command.fsp.ActivateFspCurrencyCommand;
import org.mojave.core.participant.contract.command.fsp.AddEndpointCommand;
import org.mojave.core.participant.contract.command.fsp.AddFspCurrencyCommand;
import org.mojave.core.participant.contract.command.fsp.ChangeFspEndpointCommand;
import org.mojave.core.participant.contract.command.fsp.ChangeFspNameCommand;
import org.mojave.core.participant.contract.command.fsp.CreateFspCommand;
import org.mojave.core.participant.contract.command.fsp.CreateFspGroupCommand;
import org.mojave.core.participant.contract.command.fsp.DeactivateEndpointCommand;
import org.mojave.core.participant.contract.command.fsp.DeactivateFspCommand;
import org.mojave.core.participant.contract.command.fsp.DeactivateFspCurrencyCommand;
import org.mojave.core.participant.contract.command.fsp.JoinFspGroupCommand;
import org.mojave.core.participant.contract.command.fsp.LeaveFspGroupCommand;
import org.mojave.core.participant.contract.command.fsp.RemoveFspGroupCommand;
import org.mojave.core.participant.contract.command.fsp.TerminateFspCommand;
import org.mojave.mono.core.admin.api.BaseApiController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class FspCommandController extends BaseApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FspCommandController.class);

    private final ActivateEndpointCommand activateEndpointCommand;

    private final ActivateFspCommand activateFspCommand;

    private final ActivateFspCurrencyCommand activateFspCurrencyCommand;

    private final AddEndpointCommand addEndpointCommand;

    private final AddFspCurrencyCommand addFspCurrencyCommand;

    private final ChangeFspEndpointCommand changeFspEndpointCommand;

    private final ChangeFspNameCommand changeFspNameCommand;

    private final CreateFspCommand createFspCommand;

    private final CreateFspGroupCommand createFspGroupCommand;

    private final DeactivateEndpointCommand deactivateEndpointCommand;

    private final DeactivateFspCommand deactivateFspCommand;

    private final DeactivateFspCurrencyCommand deactivateFspCurrencyCommand;

    private final JoinFspGroupCommand joinFspGroupCommand;

    private final LeaveFspGroupCommand leaveFspGroupCommand;

    private final RemoveFspGroupCommand removeFspGroupCommand;

    private final TerminateFspCommand terminateFspCommand;

    public FspCommandController(final ActivateEndpointCommand activateEndpointCommand,
                                final ActivateFspCommand activateFspCommand,
                                final ActivateFspCurrencyCommand activateFspCurrencyCommand,
                                final AddEndpointCommand addEndpointCommand,
                                final AddFspCurrencyCommand addFspCurrencyCommand,
                                final ChangeFspEndpointCommand changeFspEndpointCommand,
                                final ChangeFspNameCommand changeFspNameCommand,
                                final CreateFspCommand createFspCommand,
                                final CreateFspGroupCommand createFspGroupCommand,
                                final DeactivateEndpointCommand deactivateEndpointCommand,
                                final DeactivateFspCommand deactivateFspCommand,
                                final DeactivateFspCurrencyCommand deactivateFspCurrencyCommand,
                                final JoinFspGroupCommand joinFspGroupCommand,
                                final LeaveFspGroupCommand leaveFspGroupCommand,
                                final RemoveFspGroupCommand removeFspGroupCommand,
                                final TerminateFspCommand terminateFspCommand) {

        Objects.requireNonNull(activateEndpointCommand);
        Objects.requireNonNull(activateFspCommand);
        Objects.requireNonNull(activateFspCurrencyCommand);
        Objects.requireNonNull(addEndpointCommand);
        Objects.requireNonNull(addFspCurrencyCommand);
        Objects.requireNonNull(changeFspEndpointCommand);
        Objects.requireNonNull(changeFspNameCommand);
        Objects.requireNonNull(createFspCommand);
        Objects.requireNonNull(createFspGroupCommand);
        Objects.requireNonNull(deactivateEndpointCommand);
        Objects.requireNonNull(deactivateFspCommand);
        Objects.requireNonNull(deactivateFspCurrencyCommand);
        Objects.requireNonNull(joinFspGroupCommand);
        Objects.requireNonNull(leaveFspGroupCommand);
        Objects.requireNonNull(removeFspGroupCommand);
        Objects.requireNonNull(terminateFspCommand);

        this.activateEndpointCommand = activateEndpointCommand;
        this.activateFspCommand = activateFspCommand;
        this.activateFspCurrencyCommand = activateFspCurrencyCommand;
        this.addEndpointCommand = addEndpointCommand;
        this.addFspCurrencyCommand = addFspCurrencyCommand;
        this.changeFspEndpointCommand = changeFspEndpointCommand;
        this.changeFspNameCommand = changeFspNameCommand;
        this.createFspCommand = createFspCommand;
        this.createFspGroupCommand = createFspGroupCommand;
        this.deactivateEndpointCommand = deactivateEndpointCommand;
        this.deactivateFspCommand = deactivateFspCommand;
        this.deactivateFspCurrencyCommand = deactivateFspCurrencyCommand;
        this.joinFspGroupCommand = joinFspGroupCommand;
        this.leaveFspGroupCommand = leaveFspGroupCommand;
        this.removeFspGroupCommand = removeFspGroupCommand;
        this.terminateFspCommand = terminateFspCommand;
    }

    @PostMapping("/participant/fsps/activate-endpoint")
    public ResponseEntity<ActivateEndpointCommand.Output> activateEndpoint(
        @RequestBody final ActivateEndpointCommand.Input input) {

        return this.respond(
            LOGGER,
            "ActivateEndpointCommand",
            input,
            () -> this.activateEndpointCommand.execute(input));
    }

    @PostMapping("/participant/fsps/activate-fsp")
    public ResponseEntity<ActivateFspCommand.Output> activateFsp(
        @RequestBody final ActivateFspCommand.Input input) {

        return this.respond(
            LOGGER,
            "ActivateFspCommand",
            input,
            () -> this.activateFspCommand.execute(input));
    }

    @PostMapping("/participant/fsps/activate-fsp-currency")
    public ResponseEntity<ActivateFspCurrencyCommand.Output> activateFspCurrency(
        @RequestBody final ActivateFspCurrencyCommand.Input input) {

        return this.respond(
            LOGGER,
            "ActivateFspCurrencyCommand",
            input,
            () -> this.activateFspCurrencyCommand.execute(input));
    }

    @PostMapping("/participant/fsps/add-endpoint")
    public ResponseEntity<AddEndpointCommand.Output> addEndpoint(
        @RequestBody final AddEndpointCommand.Input input) {

        return this.respond(
            LOGGER,
            "AddEndpointCommand",
            input,
            () -> this.addEndpointCommand.execute(input));
    }

    @PostMapping("/participant/fsps/add-fsp-currency")
    public ResponseEntity<AddFspCurrencyCommand.Output> addFspCurrency(
        @RequestBody final AddFspCurrencyCommand.Input input) {

        return this.respond(
            LOGGER,
            "AddFspCurrencyCommand",
            input,
            () -> this.addFspCurrencyCommand.execute(input));
    }

    @PostMapping("/participant/fsps/change-fsp-endpoint")
    public ResponseEntity<ChangeFspEndpointCommand.Output> changeFspEndpoint(
        @RequestBody final ChangeFspEndpointCommand.Input input) {

        return this.respond(
            LOGGER,
            "ChangeFspEndpointCommand",
            input,
            () -> this.changeFspEndpointCommand.execute(input));
    }

    @PostMapping("/participant/fsps/change-fsp-name")
    public ResponseEntity<ChangeFspNameCommand.Output> changeFspName(
        @RequestBody final ChangeFspNameCommand.Input input) {

        return this.respond(
            LOGGER,
            "ChangeFspNameCommand",
            input,
            () -> this.changeFspNameCommand.execute(input));
    }

    @PostMapping("/participant/fsps/create-fsp")
    public ResponseEntity<CreateFspCommand.Output> createFsp(
        @RequestBody final CreateFspCommand.Input input) {

        return this.respond(
            LOGGER,
            "CreateFspCommand",
            input,
            () -> this.createFspCommand.execute(input));
    }

    @PostMapping("/participant/fsp-groups/create-fsp-group")
    public ResponseEntity<CreateFspGroupCommand.Output> createFspGroup(
        @RequestBody final CreateFspGroupCommand.Input input) {

        return this.respond(
            LOGGER,
            "CreateFspGroupCommand",
            input,
            () -> this.createFspGroupCommand.execute(input));
    }

    @PostMapping("/participant/fsps/deactivate-endpoint")
    public ResponseEntity<DeactivateEndpointCommand.Output> deactivateEndpoint(
        @RequestBody final DeactivateEndpointCommand.Input input) {

        return this.respond(
            LOGGER,
            "DeactivateEndpointCommand",
            input,
            () -> this.deactivateEndpointCommand.execute(input));
    }

    @PostMapping("/participant/fsps/deactivate-fsp")
    public ResponseEntity<DeactivateFspCommand.Output> deactivateFsp(
        @RequestBody final DeactivateFspCommand.Input input) {

        return this.respond(
            LOGGER,
            "DeactivateFspCommand",
            input,
            () -> this.deactivateFspCommand.execute(input));
    }

    @PostMapping("/participant/fsps/deactivate-fsp-currency")
    public ResponseEntity<DeactivateFspCurrencyCommand.Output> deactivateFspCurrency(
        @RequestBody final DeactivateFspCurrencyCommand.Input input) {

        return this.respond(
            LOGGER,
            "DeactivateFspCurrencyCommand",
            input,
            () -> this.deactivateFspCurrencyCommand.execute(input));
    }

    @PostMapping("/participant/fsp-groups/join-fsp-group")
    public ResponseEntity<JoinFspGroupCommand.Output> joinFspGroup(
        @RequestBody final JoinFspGroupCommand.Input input) {

        return this.respond(
            LOGGER,
            "JoinFspGroupCommand",
            input,
            () -> this.joinFspGroupCommand.execute(input));
    }

    @PostMapping("/participant/fsp-groups/leave-fsp-group")
    public ResponseEntity<LeaveFspGroupCommand.Output> leaveFspGroup(
        @RequestBody final LeaveFspGroupCommand.Input input) {

        return this.respond(
            LOGGER,
            "LeaveFspGroupCommand",
            input,
            () -> this.leaveFspGroupCommand.execute(input));
    }

    @PostMapping("/participant/fsp-groups/remove-fsp-group")
    public ResponseEntity<RemoveFspGroupCommand.Output> removeFspGroup(
        @RequestBody final RemoveFspGroupCommand.Input input) {

        return this.respond(
            LOGGER,
            "RemoveFspGroupCommand",
            input,
            () -> this.removeFspGroupCommand.execute(input));
    }

    @PostMapping("/participant/fsps/terminate-fsp")
    public ResponseEntity<TerminateFspCommand.Output> terminateFsp(
        @RequestBody final TerminateFspCommand.Input input) {

        return this.respond(
            LOGGER,
            "TerminateFspCommand",
            input,
            () -> this.terminateFspCommand.execute(input));
    }

}
