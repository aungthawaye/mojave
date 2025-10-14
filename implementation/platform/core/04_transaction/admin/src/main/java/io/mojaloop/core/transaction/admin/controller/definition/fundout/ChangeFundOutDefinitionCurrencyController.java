package io.mojaloop.core.transaction.admin.controller.definition.fundout;

import io.mojaloop.core.transaction.contract.command.definition.fundout.ChangeFundOutDefinitionCurrencyCommand;
import io.mojaloop.core.transaction.contract.exception.fundout.FundOutDefinitionNotFoundException;
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
public class ChangeFundOutDefinitionCurrencyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeFundOutDefinitionCurrencyController.class);

    private final ChangeFundOutDefinitionCurrencyCommand changeFundOutDefinitionCurrencyCommand;

    public ChangeFundOutDefinitionCurrencyController(ChangeFundOutDefinitionCurrencyCommand changeFundOutDefinitionCurrencyCommand) {

        assert changeFundOutDefinitionCurrencyCommand != null;

        this.changeFundOutDefinitionCurrencyCommand = changeFundOutDefinitionCurrencyCommand;
    }

    @PostMapping("/definitions/fund-out/change-currency")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void execute(@Valid @RequestBody ChangeFundOutDefinitionCurrencyCommand.Input input)
        throws FundOutDefinitionNotFoundException, FundOutDefinitionWithCurrencyExistsException {

        this.changeFundOutDefinitionCurrencyCommand.execute(input);
    }
}
