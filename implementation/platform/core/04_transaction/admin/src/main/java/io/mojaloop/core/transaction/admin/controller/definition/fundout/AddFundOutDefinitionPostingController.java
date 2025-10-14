package io.mojaloop.core.transaction.admin.controller.definition.fundout;

import io.mojaloop.core.transaction.contract.command.definition.fundout.AddFundOutDefinitionPostingCommand;
import io.mojaloop.core.transaction.contract.exception.PostingAlreadyExistsException;
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
public class AddFundOutDefinitionPostingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddFundOutDefinitionPostingController.class);

    private final AddFundOutDefinitionPostingCommand addFundOutDefinitionPostingCommand;

    public AddFundOutDefinitionPostingController(AddFundOutDefinitionPostingCommand addFundOutDefinitionPostingCommand) {

        assert addFundOutDefinitionPostingCommand != null;

        this.addFundOutDefinitionPostingCommand = addFundOutDefinitionPostingCommand;
    }

    @PostMapping("/definitions/fund-out/postings/add")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void execute(@Valid @RequestBody AddFundOutDefinitionPostingCommand.Input input)
        throws PostingAlreadyExistsException, FundOutDefinitionNotFoundException {

        this.addFundOutDefinitionPostingCommand.execute(input);
    }

}
