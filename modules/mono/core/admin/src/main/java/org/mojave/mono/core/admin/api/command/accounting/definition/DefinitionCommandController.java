package org.mojave.mono.core.admin.api.command.accounting.definition;

import org.mojave.core.accounting.contract.command.definition.ActivateFlowDefinitionCommand;
import org.mojave.core.accounting.contract.command.definition.AddFlowDefinitionLineCommand;
import org.mojave.core.accounting.contract.command.definition.ChangeFlowDefinitionCurrencyCommand;
import org.mojave.core.accounting.contract.command.definition.ChangeFlowDefinitionPropertiesCommand;
import org.mojave.core.accounting.contract.command.definition.CreateFlowDefinitionCommand;
import org.mojave.core.accounting.contract.command.definition.DeactivateFlowDefinitionCommand;
import org.mojave.core.accounting.contract.command.definition.RemoveFlowDefinitionLineCommand;
import org.mojave.core.accounting.contract.command.definition.TerminateFlowDefinitionCommand;
import org.mojave.mono.core.admin.api.BaseApiController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class DefinitionCommandController extends BaseApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefinitionCommandController.class);

    private final ActivateFlowDefinitionCommand activateFlowDefinitionCommand;

    private final AddFlowDefinitionLineCommand addFlowDefinitionLineCommand;

    private final ChangeFlowDefinitionCurrencyCommand changeFlowDefinitionCurrencyCommand;

    private final ChangeFlowDefinitionPropertiesCommand changeFlowDefinitionPropertiesCommand;

    private final CreateFlowDefinitionCommand createFlowDefinitionCommand;

    private final DeactivateFlowDefinitionCommand deactivateFlowDefinitionCommand;

    private final RemoveFlowDefinitionLineCommand removeFlowDefinitionLineCommand;

    private final TerminateFlowDefinitionCommand terminateFlowDefinitionCommand;

    public DefinitionCommandController(final ActivateFlowDefinitionCommand activateFlowDefinitionCommand,
                                       final AddFlowDefinitionLineCommand addFlowDefinitionLineCommand,
                                       final ChangeFlowDefinitionCurrencyCommand changeFlowDefinitionCurrencyCommand,
                                       final ChangeFlowDefinitionPropertiesCommand changeFlowDefinitionPropertiesCommand,
                                       final CreateFlowDefinitionCommand createFlowDefinitionCommand,
                                       final DeactivateFlowDefinitionCommand deactivateFlowDefinitionCommand,
                                       final RemoveFlowDefinitionLineCommand removeFlowDefinitionLineCommand,
                                       final TerminateFlowDefinitionCommand terminateFlowDefinitionCommand) {

        Objects.requireNonNull(activateFlowDefinitionCommand);
        Objects.requireNonNull(addFlowDefinitionLineCommand);
        Objects.requireNonNull(changeFlowDefinitionCurrencyCommand);
        Objects.requireNonNull(changeFlowDefinitionPropertiesCommand);
        Objects.requireNonNull(createFlowDefinitionCommand);
        Objects.requireNonNull(deactivateFlowDefinitionCommand);
        Objects.requireNonNull(removeFlowDefinitionLineCommand);
        Objects.requireNonNull(terminateFlowDefinitionCommand);

        this.activateFlowDefinitionCommand = activateFlowDefinitionCommand;
        this.addFlowDefinitionLineCommand = addFlowDefinitionLineCommand;
        this.changeFlowDefinitionCurrencyCommand = changeFlowDefinitionCurrencyCommand;
        this.changeFlowDefinitionPropertiesCommand = changeFlowDefinitionPropertiesCommand;
        this.createFlowDefinitionCommand = createFlowDefinitionCommand;
        this.deactivateFlowDefinitionCommand = deactivateFlowDefinitionCommand;
        this.removeFlowDefinitionLineCommand = removeFlowDefinitionLineCommand;
        this.terminateFlowDefinitionCommand = terminateFlowDefinitionCommand;
    }

    @PostMapping("/accounting/definition/activate-flow-definition")
    public ResponseEntity<ActivateFlowDefinitionCommand.Output> activateFlowDefinition(
        @RequestBody final ActivateFlowDefinitionCommand.Input input) {

        return this.respond(
            LOGGER,
            "ActivateFlowDefinitionCommand",
            input,
            () -> this.activateFlowDefinitionCommand.execute(input));
    }

    @PostMapping("/accounting/definition/add-flow-definition-line")
    public ResponseEntity<AddFlowDefinitionLineCommand.Output> addFlowDefinitionLine(
        @RequestBody final AddFlowDefinitionLineCommand.Input input) {

        return this.respond(
            LOGGER,
            "AddFlowDefinitionLineCommand",
            input,
            () -> this.addFlowDefinitionLineCommand.execute(input));
    }

    @PostMapping("/accounting/definition/change-flow-definition-currency")
    public ResponseEntity<ChangeFlowDefinitionCurrencyCommand.Output> changeFlowDefinitionCurrency(
        @RequestBody final ChangeFlowDefinitionCurrencyCommand.Input input) {

        return this.respond(
            LOGGER,
            "ChangeFlowDefinitionCurrencyCommand",
            input,
            () -> this.changeFlowDefinitionCurrencyCommand.execute(input));
    }

    @PostMapping("/accounting/definition/change-flow-definition-properties")
    public ResponseEntity<ChangeFlowDefinitionPropertiesCommand.Output> changeFlowDefinitionProperties(
        @RequestBody final ChangeFlowDefinitionPropertiesCommand.Input input) {

        return this.respond(
            LOGGER,
            "ChangeFlowDefinitionPropertiesCommand",
            input,
            () -> this.changeFlowDefinitionPropertiesCommand.execute(input));
    }

    @PostMapping("/accounting/definition/create-flow-definition")
    public ResponseEntity<CreateFlowDefinitionCommand.Output> createFlowDefinition(
        @RequestBody final CreateFlowDefinitionCommand.Input input) {

        return this.respond(
            LOGGER,
            "CreateFlowDefinitionCommand",
            input,
            () -> this.createFlowDefinitionCommand.execute(input));
    }

    @PostMapping("/accounting/definition/deactivate-flow-definition")
    public ResponseEntity<DeactivateFlowDefinitionCommand.Output> deactivateFlowDefinition(
        @RequestBody final DeactivateFlowDefinitionCommand.Input input) {

        return this.respond(
            LOGGER,
            "DeactivateFlowDefinitionCommand",
            input,
            () -> this.deactivateFlowDefinitionCommand.execute(input));
    }

    @PostMapping("/accounting/definition/remove-flow-definition-line")
    public ResponseEntity<RemoveFlowDefinitionLineCommand.Output> removeFlowDefinitionLine(
        @RequestBody final RemoveFlowDefinitionLineCommand.Input input) {

        return this.respond(
            LOGGER,
            "RemoveFlowDefinitionLineCommand",
            input,
            () -> this.removeFlowDefinitionLineCommand.execute(input));
    }

    @PostMapping("/accounting/definition/terminate-flow-definition")
    public ResponseEntity<TerminateFlowDefinitionCommand.Output> terminateFlowDefinition(
        @RequestBody final TerminateFlowDefinitionCommand.Input input) {

        return this.respond(
            LOGGER,
            "TerminateFlowDefinitionCommand",
            input,
            () -> this.terminateFlowDefinitionCommand.execute(input));
    }

}
