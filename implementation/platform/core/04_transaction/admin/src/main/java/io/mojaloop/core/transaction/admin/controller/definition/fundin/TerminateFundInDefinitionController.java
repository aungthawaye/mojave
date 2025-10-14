package io.mojaloop.core.transaction.admin.controller.definition.fundin;

import io.mojaloop.core.transaction.contract.command.definition.fundin.TerminateFundInDefinitionCommand;
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
public class TerminateFundInDefinitionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminateFundInDefinitionController.class);

    private final TerminateFundInDefinitionCommand terminateFundInDefinitionCommand;

    public TerminateFundInDefinitionController(TerminateFundInDefinitionCommand terminateFundInDefinitionCommand) {

        assert terminateFundInDefinitionCommand != null;

        this.terminateFundInDefinitionCommand = terminateFundInDefinitionCommand;
    }

    @PostMapping("/definitions/fund-in/terminate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void execute(@Valid @RequestBody TerminateFundInDefinitionCommand.Input input) throws FundInDefinitionNotFoundException {

        this.terminateFundInDefinitionCommand.execute(input);
    }

}
