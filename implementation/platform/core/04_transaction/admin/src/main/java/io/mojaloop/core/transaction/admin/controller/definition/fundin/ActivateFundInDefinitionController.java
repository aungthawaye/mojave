package io.mojaloop.core.transaction.admin.controller.definition.fundin;

import io.mojaloop.core.transaction.contract.command.definition.fundin.ActivateFundInDefinitionCommand;
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
public class ActivateFundInDefinitionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivateFundInDefinitionController.class);

    private final ActivateFundInDefinitionCommand activateFundInDefinitionCommand;

    public ActivateFundInDefinitionController(ActivateFundInDefinitionCommand activateFundInDefinitionCommand) {

        assert activateFundInDefinitionCommand != null;

        this.activateFundInDefinitionCommand = activateFundInDefinitionCommand;
    }

    @PostMapping("/definitions/fund-in/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void execute(@Valid @RequestBody ActivateFundInDefinitionCommand.Input input) throws FundInDefinitionNotFoundException {

        this.activateFundInDefinitionCommand.execute(input);
    }

}
