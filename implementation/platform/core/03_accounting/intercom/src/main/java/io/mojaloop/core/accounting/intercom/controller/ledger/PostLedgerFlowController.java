package io.mojaloop.core.accounting.intercom.controller.ledger;

import io.mojaloop.core.accounting.contract.command.ledger.PostLedgerFlowCommand;
import io.mojaloop.core.accounting.contract.exception.ledger.InsufficientBalanceInAccountException;
import io.mojaloop.core.accounting.contract.exception.ledger.PostingAccountFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostLedgerFlowController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostLedgerFlowController.class);

    private final PostLedgerFlowCommand postLedgerFlowCommand;

    public PostLedgerFlowController(PostLedgerFlowCommand postLedgerFlowCommand) {

        assert postLedgerFlowCommand != null;

        this.postLedgerFlowCommand = postLedgerFlowCommand;
    }

    @PostMapping("/ledgers/post-ledger-flow")
    public PostLedgerFlowCommand.Output execute(@Valid @RequestBody PostLedgerFlowCommand.Input input) throws PostingAccountFoundException, InsufficientBalanceInAccountException {

        LOGGER.info("Entering PostLedgerFlowController.execute: input : {}", input);

        final var output = this.postLedgerFlowCommand.execute(input);

        LOGGER.info("Exiting PostLedgerFlowController.execute: {}", output);

        return output;
    }

}
