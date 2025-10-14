package io.mojaloop.core.transaction.admin.controller.definition.fundtransfer;

import io.mojaloop.core.transaction.contract.command.definition.fundtransfer.RemoveFundTransferDefinitionPostingCommand;
import io.mojaloop.core.transaction.contract.exception.fundtransfer.FundTransferDefinitionNotFoundException;
import io.mojaloop.core.transaction.contract.exception.fundtransfer.FundTransferDefinitionPostingNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RemoveFundTransferDefinitionPostingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveFundTransferDefinitionPostingController.class);

    private final RemoveFundTransferDefinitionPostingCommand removeFundTransferDefinitionPostingCommand;

    public RemoveFundTransferDefinitionPostingController(RemoveFundTransferDefinitionPostingCommand removeFundTransferDefinitionPostingCommand) {

        assert removeFundTransferDefinitionPostingCommand != null;

        this.removeFundTransferDefinitionPostingCommand = removeFundTransferDefinitionPostingCommand;
    }

    @PostMapping("/definitions/fund-transfer/postings/remove")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void execute(@Valid @RequestBody RemoveFundTransferDefinitionPostingCommand.Input input)
        throws FundTransferDefinitionPostingNotFoundException, FundTransferDefinitionNotFoundException {

        this.removeFundTransferDefinitionPostingCommand.execute(input);
    }
}
