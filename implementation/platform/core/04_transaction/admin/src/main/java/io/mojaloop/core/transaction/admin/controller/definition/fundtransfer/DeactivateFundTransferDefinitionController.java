package io.mojaloop.core.transaction.admin.controller.definition.fundtransfer;

import io.mojaloop.core.transaction.contract.command.definition.fundtransfer.DeactivateFundTransferDefinitionCommand;
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
public class DeactivateFundTransferDefinitionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeactivateFundTransferDefinitionController.class);

    private final DeactivateFundTransferDefinitionCommand deactivateFundTransferDefinitionCommand;

    public DeactivateFundTransferDefinitionController(DeactivateFundTransferDefinitionCommand deactivateFundTransferDefinitionCommand) {

        assert deactivateFundTransferDefinitionCommand != null;

        this.deactivateFundTransferDefinitionCommand = deactivateFundTransferDefinitionCommand;
    }

    @PostMapping("/definitions/fund-transfer/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void execute(@Valid @RequestBody DeactivateFundTransferDefinitionCommand.Input input) throws FundTransferDefinitionNotFoundException {

        this.deactivateFundTransferDefinitionCommand.execute(input);
    }

}
