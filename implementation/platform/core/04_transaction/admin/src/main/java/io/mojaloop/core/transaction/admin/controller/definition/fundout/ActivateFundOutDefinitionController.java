package io.mojaloop.core.transaction.admin.controller.definition.fundout;

import io.mojaloop.core.transaction.contract.command.definition.fundout.ActivateFundOutDefinitionCommand;
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
public class ActivateFundOutDefinitionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivateFundOutDefinitionController.class);

    private final ActivateFundOutDefinitionCommand activateFundOutDefinitionCommand;

    public ActivateFundOutDefinitionController(ActivateFundOutDefinitionCommand activateFundOutDefinitionCommand) {

        assert activateFundOutDefinitionCommand != null;

        this.activateFundOutDefinitionCommand = activateFundOutDefinitionCommand;
    }

    @PostMapping("/definitions/fund-out/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void execute(@Valid @RequestBody ActivateFundOutDefinitionCommand.Input input) throws FundOutDefinitionNotFoundException {

        this.activateFundOutDefinitionCommand.execute(input);
    }
}
