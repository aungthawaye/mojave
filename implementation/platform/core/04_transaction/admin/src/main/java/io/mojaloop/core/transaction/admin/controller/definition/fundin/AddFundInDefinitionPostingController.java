package io.mojaloop.core.transaction.admin.controller.definition.fundin;

import io.mojaloop.core.transaction.contract.command.definition.fundin.AddFundInDefinitionPostingCommand;
import io.mojaloop.core.transaction.contract.exception.PostingAlreadyExistsException;
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
public class AddFundInDefinitionPostingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddFundInDefinitionPostingController.class);

    private final AddFundInDefinitionPostingCommand addFundInDefinitionPostingCommand;

    public AddFundInDefinitionPostingController(AddFundInDefinitionPostingCommand addFundInDefinitionPostingCommand) {

        assert addFundInDefinitionPostingCommand != null;

        this.addFundInDefinitionPostingCommand = addFundInDefinitionPostingCommand;
    }

    @PostMapping("/definitions/fund-in/postings/add")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void execute(@Valid @RequestBody AddFundInDefinitionPostingCommand.Input input)
        throws PostingAlreadyExistsException, FundInDefinitionNotFoundException {

        this.addFundInDefinitionPostingCommand.execute(input);
    }

}
