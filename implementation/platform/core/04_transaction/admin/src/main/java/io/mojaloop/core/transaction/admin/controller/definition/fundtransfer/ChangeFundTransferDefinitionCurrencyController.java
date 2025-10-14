package io.mojaloop.core.transaction.admin.controller.definition.fundtransfer;

import io.mojaloop.core.transaction.contract.command.definition.fundtransfer.ChangeFundTransferDefinitionCurrencyCommand;
import io.mojaloop.core.transaction.contract.exception.fundtransfer.FundTransferDefinitionNotFoundException;
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
public class ChangeFundTransferDefinitionCurrencyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeFundTransferDefinitionCurrencyController.class);

    private final ChangeFundTransferDefinitionCurrencyCommand changeFundTransferDefinitionCurrencyCommand;

    public ChangeFundTransferDefinitionCurrencyController(ChangeFundTransferDefinitionCurrencyCommand changeFundTransferDefinitionCurrencyCommand) {

        assert changeFundTransferDefinitionCurrencyCommand != null;

        this.changeFundTransferDefinitionCurrencyCommand = changeFundTransferDefinitionCurrencyCommand;
    }

    @PostMapping("/definitions/fund-transfer/change-currency")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void execute(@Valid @RequestBody ChangeFundTransferDefinitionCurrencyCommand.Input input)
        throws FundTransferDefinitionNotFoundException, FundTransferDefinitionWithCurrencyExistsException {

        this.changeFundTransferDefinitionCurrencyCommand.execute(input);
    }
}
