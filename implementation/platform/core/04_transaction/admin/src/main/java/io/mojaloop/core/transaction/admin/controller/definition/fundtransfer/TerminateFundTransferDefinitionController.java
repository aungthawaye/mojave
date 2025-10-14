package io.mojaloop.core.transaction.admin.controller.definition.fundtransfer;

import io.mojaloop.core.transaction.contract.command.definition.fundtransfer.TerminateFundTransferDefinitionCommand;
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
public class TerminateFundTransferDefinitionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminateFundTransferDefinitionController.class);

    private final TerminateFundTransferDefinitionCommand terminateFundTransferDefinitionCommand;

    public TerminateFundTransferDefinitionController(TerminateFundTransferDefinitionCommand terminateFundTransferDefinitionCommand) {

        assert terminateFundTransferDefinitionCommand != null;

        this.terminateFundTransferDefinitionCommand = terminateFundTransferDefinitionCommand;
    }

    @PostMapping("/definitions/fund-transfer/terminate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void execute(@Valid @RequestBody TerminateFundTransferDefinitionCommand.Input input) throws FundTransferDefinitionNotFoundException {

        this.terminateFundTransferDefinitionCommand.execute(input);
    }

}
