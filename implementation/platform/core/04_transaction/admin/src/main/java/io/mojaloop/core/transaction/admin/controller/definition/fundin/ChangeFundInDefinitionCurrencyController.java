package io.mojaloop.core.transaction.admin.controller.definition.fundin;

import io.mojaloop.core.transaction.contract.command.definition.fundin.ChangeFundInDefinitionCurrencyCommand;
import io.mojaloop.core.transaction.contract.exception.fundin.FundInDefinitionNotFoundException;
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
public class ChangeFundInDefinitionCurrencyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeFundInDefinitionCurrencyController.class);

    private final ChangeFundInDefinitionCurrencyCommand changeFundInDefinitionCurrencyCommand;

    public ChangeFundInDefinitionCurrencyController(ChangeFundInDefinitionCurrencyCommand changeFundInDefinitionCurrencyCommand) {

        assert changeFundInDefinitionCurrencyCommand != null;

        this.changeFundInDefinitionCurrencyCommand = changeFundInDefinitionCurrencyCommand;
    }

    @PostMapping("/definitions/fund-in/change-currency")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void execute(@Valid @RequestBody ChangeFundInDefinitionCurrencyCommand.Input input)
        throws FundInDefinitionNotFoundException, FundInDefinitionWithCurrencyExistsException {

        this.changeFundInDefinitionCurrencyCommand.execute(input);
    }

}
