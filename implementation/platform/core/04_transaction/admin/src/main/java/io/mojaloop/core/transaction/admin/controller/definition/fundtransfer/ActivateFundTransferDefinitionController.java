package io.mojaloop.core.transaction.admin.controller.definition.fundtransfer;

import io.mojaloop.core.transaction.contract.command.definition.fundtransfer.ActivateFundTransferDefinitionCommand;
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
public class ActivateFundTransferDefinitionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivateFundTransferDefinitionController.class);

    private final ActivateFundTransferDefinitionCommand activateFundTransferDefinitionCommand;

    public ActivateFundTransferDefinitionController(ActivateFundTransferDefinitionCommand activateFundTransferDefinitionCommand) {

        assert activateFundTransferDefinitionCommand != null;

        this.activateFundTransferDefinitionCommand = activateFundTransferDefinitionCommand;
    }

    @PostMapping("/definitions/fund-transfer/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void execute(@Valid @RequestBody ActivateFundTransferDefinitionCommand.Input input) throws FundTransferDefinitionNotFoundException {

        this.activateFundTransferDefinitionCommand.execute(input);
    }

}
