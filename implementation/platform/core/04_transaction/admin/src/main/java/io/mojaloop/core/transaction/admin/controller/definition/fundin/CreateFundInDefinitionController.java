package io.mojaloop.core.transaction.admin.controller.definition.fundin;

import io.mojaloop.core.transaction.contract.command.definition.fundin.CreateFundInDefinitionCommand;
import io.mojaloop.core.transaction.contract.exception.PostingAlreadyExistsException;
import io.mojaloop.core.transaction.contract.exception.fundin.FundInDefinitionNameTakenException;
import io.mojaloop.core.transaction.contract.exception.fundin.FundInDefinitionWithCurrencyExistsException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreateFundInDefinitionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateFundInDefinitionController.class);

    private final CreateFundInDefinitionCommand createFundInDefinitionCommand;

    public CreateFundInDefinitionController(CreateFundInDefinitionCommand createFundInDefinitionCommand) {

        assert createFundInDefinitionCommand != null;

        this.createFundInDefinitionCommand = createFundInDefinitionCommand;
    }

    @PostMapping("/definitions/fund-in")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateFundInDefinitionCommand.Output execute(@Valid @RequestBody CreateFundInDefinitionCommand.Input input)
        throws PostingAlreadyExistsException, FundInDefinitionWithCurrencyExistsException, FundInDefinitionNameTakenException {

        return this.createFundInDefinitionCommand.execute(input);
    }
}
