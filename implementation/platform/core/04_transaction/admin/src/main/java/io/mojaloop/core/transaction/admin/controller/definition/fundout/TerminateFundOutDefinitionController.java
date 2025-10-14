package io.mojaloop.core.transaction.admin.controller.definition.fundout;

import io.mojaloop.core.transaction.contract.command.definition.fundout.TerminateFundOutDefinitionCommand;
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
public class TerminateFundOutDefinitionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminateFundOutDefinitionController.class);

    private final TerminateFundOutDefinitionCommand terminateFundOutDefinitionCommand;

    public TerminateFundOutDefinitionController(TerminateFundOutDefinitionCommand terminateFundOutDefinitionCommand) {

        assert terminateFundOutDefinitionCommand != null;

        this.terminateFundOutDefinitionCommand = terminateFundOutDefinitionCommand;
    }

    @PostMapping("/definitions/fund-out/terminate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void execute(@Valid @RequestBody TerminateFundOutDefinitionCommand.Input input) throws FundOutDefinitionNotFoundException {

        this.terminateFundOutDefinitionCommand.execute(input);
    }
}
