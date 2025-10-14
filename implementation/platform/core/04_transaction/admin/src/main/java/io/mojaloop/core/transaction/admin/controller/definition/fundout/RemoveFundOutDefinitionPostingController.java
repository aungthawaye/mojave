package io.mojaloop.core.transaction.admin.controller.definition.fundout;

import io.mojaloop.core.transaction.contract.command.definition.fundout.RemoveFundOutDefinitionPostingCommand;
import io.mojaloop.core.transaction.contract.exception.fundout.FundOutDefinitionNotFoundException;
import io.mojaloop.core.transaction.contract.exception.fundout.FundOutDefinitionPostingNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RemoveFundOutDefinitionPostingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveFundOutDefinitionPostingController.class);

    private final RemoveFundOutDefinitionPostingCommand removeFundOutDefinitionPostingCommand;

    public RemoveFundOutDefinitionPostingController(RemoveFundOutDefinitionPostingCommand removeFundOutDefinitionPostingCommand) {

        assert removeFundOutDefinitionPostingCommand != null;

        this.removeFundOutDefinitionPostingCommand = removeFundOutDefinitionPostingCommand;
    }

    @PostMapping("/definitions/fund-out/postings/remove")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void execute(@Valid @RequestBody RemoveFundOutDefinitionPostingCommand.Input input)
        throws FundOutDefinitionPostingNotFoundException, FundOutDefinitionNotFoundException {

        this.removeFundOutDefinitionPostingCommand.execute(input);
    }

}
