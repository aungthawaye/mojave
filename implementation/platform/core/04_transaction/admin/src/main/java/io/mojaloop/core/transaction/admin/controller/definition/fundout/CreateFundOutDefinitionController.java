package io.mojaloop.core.transaction.admin.controller.definition.fundout;

import io.mojaloop.core.transaction.contract.command.definition.fundout.CreateFundOutDefinitionCommand;
import io.mojaloop.core.transaction.contract.exception.PostingAlreadyExistsException;
import io.mojaloop.core.transaction.contract.exception.fundout.FundOutDefinitionNameTakenException;
import io.mojaloop.core.transaction.contract.exception.fundout.FundOutDefinitionWithCurrencyExistsException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreateFundOutDefinitionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateFundOutDefinitionController.class);

    private final CreateFundOutDefinitionCommand createFundOutDefinitionCommand;

    public CreateFundOutDefinitionController(CreateFundOutDefinitionCommand createFundOutDefinitionCommand) {

        assert createFundOutDefinitionCommand != null;

        this.createFundOutDefinitionCommand = createFundOutDefinitionCommand;
    }

    @PostMapping("/definitions/fund-out")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateFundOutDefinitionCommand.Output execute(@Valid @RequestBody CreateFundOutDefinitionCommand.Input input)
        throws PostingAlreadyExistsException, FundOutDefinitionWithCurrencyExistsException, FundOutDefinitionNameTakenException {

        return this.createFundOutDefinitionCommand.execute(input);
    }
}
