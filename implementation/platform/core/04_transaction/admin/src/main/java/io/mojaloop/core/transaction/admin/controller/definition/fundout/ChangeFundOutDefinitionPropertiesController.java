package io.mojaloop.core.transaction.admin.controller.definition.fundout;

import io.mojaloop.core.transaction.contract.command.definition.fundout.ChangeFundOutDefinitionPropertiesCommand;
import io.mojaloop.core.transaction.contract.exception.fundout.FundOutDefinitionNameTakenException;
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
public class ChangeFundOutDefinitionPropertiesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeFundOutDefinitionPropertiesController.class);

    private final ChangeFundOutDefinitionPropertiesCommand changeFundOutDefinitionPropertiesCommand;

    public ChangeFundOutDefinitionPropertiesController(ChangeFundOutDefinitionPropertiesCommand changeFundOutDefinitionPropertiesCommand) {

        assert changeFundOutDefinitionPropertiesCommand != null;

        this.changeFundOutDefinitionPropertiesCommand = changeFundOutDefinitionPropertiesCommand;
    }

    @PostMapping("/definitions/fund-out/change-properties")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void execute(@Valid @RequestBody ChangeFundOutDefinitionPropertiesCommand.Input input)
        throws FundOutDefinitionNotFoundException, FundOutDefinitionNameTakenException {

        this.changeFundOutDefinitionPropertiesCommand.execute(input);
    }

}
