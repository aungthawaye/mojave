package io.mojaloop.core.transaction.admin.controller.definition.fundin;

import io.mojaloop.core.transaction.contract.command.definition.fundin.RemoveFundInDefinitionPostingCommand;
import io.mojaloop.core.transaction.contract.exception.fundin.FundInDefinitionNotFoundException;
import io.mojaloop.core.transaction.contract.exception.fundin.FundInDefinitionPostingNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RemoveFundInDefinitionPostingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveFundInDefinitionPostingController.class);

    private final RemoveFundInDefinitionPostingCommand removeFundInDefinitionPostingCommand;

    public RemoveFundInDefinitionPostingController(RemoveFundInDefinitionPostingCommand removeFundInDefinitionPostingCommand) {

        assert removeFundInDefinitionPostingCommand != null;

        this.removeFundInDefinitionPostingCommand = removeFundInDefinitionPostingCommand;
    }

    @PostMapping("/definitions/fund-in/postings/remove")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void execute(@Valid @RequestBody RemoveFundInDefinitionPostingCommand.Input input)
        throws FundInDefinitionPostingNotFoundException, FundInDefinitionNotFoundException {

        this.removeFundInDefinitionPostingCommand.execute(input);
    }

}
