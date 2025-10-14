package io.mojaloop.core.transaction.admin.controller.definition.fundin;

import io.mojaloop.core.transaction.contract.command.definition.fundin.ChangeFundInDefinitionPropertiesCommand;
import io.mojaloop.core.transaction.contract.exception.fundin.FundInDefinitionNameTakenException;
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
public class ChangeFundInDefinitionPropertiesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeFundInDefinitionPropertiesController.class);

    private final ChangeFundInDefinitionPropertiesCommand changeFundInDefinitionPropertiesCommand;

    public ChangeFundInDefinitionPropertiesController(ChangeFundInDefinitionPropertiesCommand changeFundInDefinitionPropertiesCommand) {

        assert changeFundInDefinitionPropertiesCommand != null;

        this.changeFundInDefinitionPropertiesCommand = changeFundInDefinitionPropertiesCommand;
    }

    @PostMapping("/definitions/fund-in/change-properties")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void execute(@Valid @RequestBody ChangeFundInDefinitionPropertiesCommand.Input input)
        throws FundInDefinitionNotFoundException, FundInDefinitionNameTakenException {

        this.changeFundInDefinitionPropertiesCommand.execute(input);
    }

}
