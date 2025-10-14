package io.mojaloop.core.transaction.admin.controller.definition.fundin;

import io.mojaloop.core.transaction.contract.command.definition.fundin.DeactivateFundInDefinitionCommand;
import io.mojaloop.core.transaction.contract.exception.fundin.FundInDefinitionNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeactivateFundInDefinitionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeactivateFundInDefinitionController.class);

    private final DeactivateFundInDefinitionCommand deactivateFundInDefinitionCommand;

    public DeactivateFundInDefinitionController(DeactivateFundInDefinitionCommand deactivateFundInDefinitionCommand) {

        assert deactivateFundInDefinitionCommand != null;

        this.deactivateFundInDefinitionCommand = deactivateFundInDefinitionCommand;
    }

    @PostMapping("/definitions/fund-in/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void execute(@Valid @RequestBody DeactivateFundInDefinitionCommand.Input input) throws FundInDefinitionNotFoundException {

        this.deactivateFundInDefinitionCommand.execute(input);
    }
}
