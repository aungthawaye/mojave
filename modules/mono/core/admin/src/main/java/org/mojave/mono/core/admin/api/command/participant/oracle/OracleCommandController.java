package org.mojave.mono.core.admin.api.command.participant.oracle;

import org.mojave.core.participant.contract.command.oracle.ActivateOracleCommand;
import org.mojave.core.participant.contract.command.oracle.ChangeOracleNameCommand;
import org.mojave.core.participant.contract.command.oracle.ChangeOracleTypeCommand;
import org.mojave.core.participant.contract.command.oracle.CreateOracleCommand;
import org.mojave.core.participant.contract.command.oracle.DeactivateOracleCommand;
import org.mojave.core.participant.contract.command.oracle.TerminateOracleCommand;
import org.mojave.mono.core.admin.api.BaseApiController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class OracleCommandController extends BaseApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OracleCommandController.class);

    private final ActivateOracleCommand activateOracleCommand;

    private final ChangeOracleNameCommand changeOracleNameCommand;

    private final ChangeOracleTypeCommand changeOracleTypeCommand;

    private final CreateOracleCommand createOracleCommand;

    private final DeactivateOracleCommand deactivateOracleCommand;

    private final TerminateOracleCommand terminateOracleCommand;

    public OracleCommandController(final ActivateOracleCommand activateOracleCommand,
                                   final ChangeOracleNameCommand changeOracleNameCommand,
                                   final ChangeOracleTypeCommand changeOracleTypeCommand,
                                   final CreateOracleCommand createOracleCommand,
                                   final DeactivateOracleCommand deactivateOracleCommand,
                                   final TerminateOracleCommand terminateOracleCommand) {

        Objects.requireNonNull(activateOracleCommand);
        Objects.requireNonNull(changeOracleNameCommand);
        Objects.requireNonNull(changeOracleTypeCommand);
        Objects.requireNonNull(createOracleCommand);
        Objects.requireNonNull(deactivateOracleCommand);
        Objects.requireNonNull(terminateOracleCommand);

        this.activateOracleCommand = activateOracleCommand;
        this.changeOracleNameCommand = changeOracleNameCommand;
        this.changeOracleTypeCommand = changeOracleTypeCommand;
        this.createOracleCommand = createOracleCommand;
        this.deactivateOracleCommand = deactivateOracleCommand;
        this.terminateOracleCommand = terminateOracleCommand;
    }

    @PostMapping("/participant/oracle/activate-oracle")
    public ResponseEntity<ActivateOracleCommand.Output> activateOracle(
        @RequestBody final ActivateOracleCommand.Input input) {

        return this.respond(
            LOGGER,
            "ActivateOracleCommand",
            input,
            () -> this.activateOracleCommand.execute(input));
    }

    @PostMapping("/participant/oracle/change-oracle-name")
    public ResponseEntity<ChangeOracleNameCommand.Output> changeOracleName(
        @RequestBody final ChangeOracleNameCommand.Input input) {

        return this.respond(
            LOGGER,
            "ChangeOracleNameCommand",
            input,
            () -> this.changeOracleNameCommand.execute(input));
    }

    @PostMapping("/participant/oracle/change-oracle-type")
    public ResponseEntity<ChangeOracleTypeCommand.Output> changeOracleType(
        @RequestBody final ChangeOracleTypeCommand.Input input) {

        return this.respond(
            LOGGER,
            "ChangeOracleTypeCommand",
            input,
            () -> this.changeOracleTypeCommand.execute(input));
    }

    @PostMapping("/participant/oracle/create-oracle")
    public ResponseEntity<CreateOracleCommand.Output> createOracle(
        @RequestBody final CreateOracleCommand.Input input) {

        return this.respond(
            LOGGER,
            "CreateOracleCommand",
            input,
            () -> this.createOracleCommand.execute(input));
    }

    @PostMapping("/participant/oracle/deactivate-oracle")
    public ResponseEntity<DeactivateOracleCommand.Output> deactivateOracle(
        @RequestBody final DeactivateOracleCommand.Input input) {

        return this.respond(
            LOGGER,
            "DeactivateOracleCommand",
            input,
            () -> this.deactivateOracleCommand.execute(input));
    }

    @PostMapping("/participant/oracle/terminate-oracle")
    public ResponseEntity<TerminateOracleCommand.Output> terminateOracle(
        @RequestBody final TerminateOracleCommand.Input input) {

        return this.respond(
            LOGGER,
            "TerminateOracleCommand",
            input,
            () -> this.terminateOracleCommand.execute(input));
    }

}
