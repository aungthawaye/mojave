package org.mojave.mono.core.admin.api.command.accounting.chart;

import org.mojave.core.accounting.contract.command.chart.CreateCoaCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class CreateCoaController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateCoaController.class);

    private final CreateCoaCommand createCoaCommand;

    public CreateCoaController(CreateCoaCommand createCoaCommand) {

        Objects.requireNonNull(createCoaCommand);

        this.createCoaCommand = createCoaCommand;
    }

    @PostMapping("/accounting/charts/create-coa")
    public ResponseEntity<CreateCoaCommand.Output> createCoa(
        @RequestBody CreateCoaCommand.Input input) {

        LOGGER.info("CreateCoaCommand: input ({})", input);

        var output = this.createCoaCommand.execute(input);

        LOGGER.info("CreateCoaCommand: output ({})", output);

        return ResponseEntity.ok(output);
    }

}
