package org.mojave.mono.core.admin.api.command.accounting.ledger;

import org.mojave.core.accounting.contract.command.ledger.PostAccountingFlowCommand;
import org.mojave.core.accounting.contract.exception.ledger.DuplicatePostingInLedgerException;
import org.mojave.core.accounting.contract.exception.ledger.InsufficientBalanceInAccountException;
import org.mojave.core.accounting.contract.exception.ledger.OverdraftLimitReachedInAccountException;
import org.mojave.core.accounting.contract.exception.ledger.PostingAccountNotFoundException;
import org.mojave.core.accounting.contract.exception.ledger.RestoreFailedInAccountException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class LedgerCommandController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LedgerCommandController.class);

    private final PostAccountingFlowCommand postAccountingFlowCommand;

    public LedgerCommandController(final PostAccountingFlowCommand postAccountingFlowCommand) {

        Objects.requireNonNull(postAccountingFlowCommand);

        this.postAccountingFlowCommand = postAccountingFlowCommand;
    }

    @PostMapping("/accounting/ledgers/post-accounting-flow")
    public ResponseEntity<PostAccountingFlowCommand.Output> postAccountingFlow(
        @RequestBody final PostAccountingFlowCommand.Input input) throws
                                                                 InsufficientBalanceInAccountException,
                                                                 OverdraftLimitReachedInAccountException,
                                                                 DuplicatePostingInLedgerException,
                                                                 PostingAccountNotFoundException,
                                                                 RestoreFailedInAccountException {

        LOGGER.info("PostAccountingFlowCommand: input ({})", input);

        final var output = this.postAccountingFlowCommand.execute(input);

        LOGGER.info("PostAccountingFlowCommand: output ({})", output);

        return ResponseEntity.ok(output);
    }

}
