package io.mojaloop.core.accounting.intercom.controller;

import io.mojaloop.core.accounting.contract.command.ledger.PostTransactionCommand;
import io.mojaloop.core.accounting.contract.exception.ledger.DuplicatePostingInLedgerException;
import io.mojaloop.core.accounting.contract.exception.ledger.InsufficientBalanceInAccountException;
import io.mojaloop.core.accounting.contract.exception.ledger.OverdraftLimitReachedInAccountException;
import io.mojaloop.core.accounting.contract.exception.ledger.RestoreFailedInAccountException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LedgerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LedgerController.class);

    private final PostTransactionCommand postTransactionCommand;

    public LedgerController(PostTransactionCommand postTransactionCommand) {

        assert postTransactionCommand != null;

        this.postTransactionCommand = postTransactionCommand;
    }

    @PostMapping("/ledgers/post-transaction")
    public PostTransactionCommand.Output execute(@Valid @RequestBody PostTransactionCommand.Input input) throws
                                                                                                         InsufficientBalanceInAccountException,
                                                                                                         DuplicatePostingInLedgerException,
                                                                                                         RestoreFailedInAccountException,
                                                                                                         OverdraftLimitReachedInAccountException {

        LOGGER.info("Entering PostTransactionCommand.execute: input : {}", input);

        final var output = this.postTransactionCommand.execute(input);

        LOGGER.info("Exiting PostTransactionCommand.execute: {}", output);

        return output;
    }

}
