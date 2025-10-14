package io.mojaloop.core.transaction.admin.controller.definition.fundout;

import io.mojaloop.core.transaction.contract.command.definition.fundout.DeactivateFundOutDefinitionCommand;
import io.mojaloop.core.transaction.contract.exception.fundout.FundOutDefinitionNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeactivateFundOutDefinitionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeactivateFundOutDefinitionController.class);

    private final DeactivateFundOutDefinitionCommand deactivateFundOutDefinitionCommand;

    public DeactivateFundOutDefinitionController(DeactivateFundOutDefinitionCommand deactivateFundOutDefinitionCommand) {

        assert deactivateFundOutDefinitionCommand != null;

        this.deactivateFundOutDefinitionCommand = deactivateFundOutDefinitionCommand;
    }

    @PostMapping("/definitions/fund-out/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void execute(@Valid @RequestBody DeactivateFundOutDefinitionCommand.Input input) throws FundOutDefinitionNotFoundException {

        this.deactivateFundOutDefinitionCommand.execute(input);
    }
}
