package io.mojaloop.core.transaction.admin.controller.definition.fundtransfer;

import io.mojaloop.core.transaction.contract.command.definition.fundtransfer.CreateFundTransferDefinitionCommand;
import io.mojaloop.core.transaction.contract.exception.PostingAlreadyExistsException;
import io.mojaloop.core.transaction.contract.exception.fundtransfer.FundTransferDefinitionNameTakenException;
import io.mojaloop.core.transaction.contract.exception.fundtransfer.FundTransferDefinitionWithCurrencyExistsException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreateFundTransferDefinitionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateFundTransferDefinitionController.class);

    private final CreateFundTransferDefinitionCommand createFundTransferDefinitionCommand;

    public CreateFundTransferDefinitionController(CreateFundTransferDefinitionCommand createFundTransferDefinitionCommand) {

        assert createFundTransferDefinitionCommand != null;

        this.createFundTransferDefinitionCommand = createFundTransferDefinitionCommand;
    }

    @PostMapping("/definitions/fund-transfer")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateFundTransferDefinitionCommand.Output execute(@Valid @RequestBody CreateFundTransferDefinitionCommand.Input input)
        throws PostingAlreadyExistsException, FundTransferDefinitionWithCurrencyExistsException, FundTransferDefinitionNameTakenException {

        return this.createFundTransferDefinitionCommand.execute(input);
    }

}
