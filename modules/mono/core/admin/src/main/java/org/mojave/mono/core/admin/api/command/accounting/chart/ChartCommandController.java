package org.mojave.mono.core.admin.api.command.accounting.chart;

import org.mojave.core.accounting.contract.command.chart.ChangeCoaEntryPropertiesCommand;
import org.mojave.core.accounting.contract.command.chart.ChangeCoaNameCommand;
import org.mojave.core.accounting.contract.command.chart.CreateCoaCommand;
import org.mojave.core.accounting.contract.command.chart.CreateCoaEntryCommand;
import org.mojave.mono.core.admin.api.BaseApiController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class ChartCommandController extends BaseApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChartCommandController.class);

    private final ChangeCoaEntryPropertiesCommand changeCoaEntryPropertiesCommand;

    private final ChangeCoaNameCommand changeCoaNameCommand;

    private final CreateCoaCommand createCoaCommand;

    private final CreateCoaEntryCommand createCoaEntryCommand;

    public ChartCommandController(final ChangeCoaEntryPropertiesCommand changeCoaEntryPropertiesCommand,
                                  final ChangeCoaNameCommand changeCoaNameCommand,
                                  final CreateCoaCommand createCoaCommand,
                                  final CreateCoaEntryCommand createCoaEntryCommand) {

        Objects.requireNonNull(changeCoaEntryPropertiesCommand);
        Objects.requireNonNull(changeCoaNameCommand);
        Objects.requireNonNull(createCoaCommand);
        Objects.requireNonNull(createCoaEntryCommand);

        this.changeCoaEntryPropertiesCommand = changeCoaEntryPropertiesCommand;
        this.changeCoaNameCommand = changeCoaNameCommand;
        this.createCoaCommand = createCoaCommand;
        this.createCoaEntryCommand = createCoaEntryCommand;
    }

    @PostMapping("/accounting/chart/change-coa-entry-properties")
    public ResponseEntity<ChangeCoaEntryPropertiesCommand.Output> changeCoaEntryProperties(
        @RequestBody final ChangeCoaEntryPropertiesCommand.Input input) {

        return this.respond(
            LOGGER,
            "ChangeCoaEntryPropertiesCommand",
            input,
            () -> this.changeCoaEntryPropertiesCommand.execute(input));
    }

    @PostMapping("/accounting/chart/change-coa-name")
    public ResponseEntity<ChangeCoaNameCommand.Output> changeCoaName(
        @RequestBody final ChangeCoaNameCommand.Input input) {

        return this.respond(
            LOGGER,
            "ChangeCoaNameCommand",
            input,
            () -> this.changeCoaNameCommand.execute(input));
    }

    @PostMapping("/accounting/chart/create-coa")
    public ResponseEntity<CreateCoaCommand.Output> createCoa(
        @RequestBody final CreateCoaCommand.Input input) {

        return this.respond(
            LOGGER,
            "CreateCoaCommand",
            input,
            () -> this.createCoaCommand.execute(input));
    }

    @PostMapping("/accounting/chart/create-coa-entry")
    public ResponseEntity<CreateCoaEntryCommand.Output> createCoaEntry(
        @RequestBody final CreateCoaEntryCommand.Input input) {

        return this.respond(
            LOGGER,
            "CreateCoaEntryCommand",
            input,
            () -> this.createCoaEntryCommand.execute(input));
    }

}
