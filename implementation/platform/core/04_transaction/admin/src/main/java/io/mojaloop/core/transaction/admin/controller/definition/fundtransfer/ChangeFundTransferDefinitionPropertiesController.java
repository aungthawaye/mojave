package io.mojaloop.core.transaction.admin.controller.definition.fundtransfer;

import io.mojaloop.core.transaction.contract.command.definition.fundtransfer.ChangeFundTransferDefinitionPropertiesCommand;
import io.mojaloop.core.transaction.contract.exception.fundtransfer.FundTransferDefinitionNameTakenException;
import io.mojaloop.core.transaction.contract.exception.fundtransfer.FundTransferDefinitionNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChangeFundTransferDefinitionPropertiesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeFundTransferDefinitionPropertiesController.class);

    private final ChangeFundTransferDefinitionPropertiesCommand changeFundTransferDefinitionPropertiesCommand;

    public ChangeFundTransferDefinitionPropertiesController(ChangeFundTransferDefinitionPropertiesCommand changeFundTransferDefinitionPropertiesCommand) {

        assert changeFundTransferDefinitionPropertiesCommand != null;

        this.changeFundTransferDefinitionPropertiesCommand = changeFundTransferDefinitionPropertiesCommand;
    }

    @PostMapping("/definitions/fund-transfer/change-properties")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void execute(@Valid @RequestBody ChangeFundTransferDefinitionPropertiesCommand.Input input)
        throws FundTransferDefinitionNotFoundException, FundTransferDefinitionNameTakenException {

        this.changeFundTransferDefinitionPropertiesCommand.execute(input);
    }

}
