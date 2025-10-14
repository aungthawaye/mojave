package io.mojaloop.core.transaction.admin.controller.definition.fundtransfer;

import io.mojaloop.core.transaction.contract.command.definition.fundtransfer.AddFundTransferDefinitionPostingCommand;
import io.mojaloop.core.transaction.contract.exception.PostingAlreadyExistsException;
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
public class AddFundTransferDefinitionPostingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddFundTransferDefinitionPostingController.class);

    private final AddFundTransferDefinitionPostingCommand addFundTransferDefinitionPostingCommand;

    public AddFundTransferDefinitionPostingController(AddFundTransferDefinitionPostingCommand addFundTransferDefinitionPostingCommand) {

        assert addFundTransferDefinitionPostingCommand != null;

        this.addFundTransferDefinitionPostingCommand = addFundTransferDefinitionPostingCommand;
    }

    @PostMapping("/definitions/fund-transfer/postings/add")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void execute(@Valid @RequestBody AddFundTransferDefinitionPostingCommand.Input input)
        throws PostingAlreadyExistsException, FundTransferDefinitionNotFoundException {

        this.addFundTransferDefinitionPostingCommand.execute(input);
    }
}
