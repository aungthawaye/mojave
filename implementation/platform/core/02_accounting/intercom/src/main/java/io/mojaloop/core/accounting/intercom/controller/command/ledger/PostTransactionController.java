package io.mojaloop.core.accounting.intercom.controller.command.ledger;

import io.mojaloop.core.accounting.contract.command.ledger.PostTransactionCommand;
import io.mojaloop.core.accounting.contract.exception.ledger.DuplicatePostingInLedgerException;
import io.mojaloop.core.accounting.contract.exception.ledger.InsufficientBalanceInAccountException;
import io.mojaloop.core.accounting.contract.exception.ledger.OverdraftLimitReachedInAccountException;
import io.mojaloop.core.accounting.contract.exception.ledger.RestoreFailedInAccountException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostTransactionController {

    private final PostTransactionCommand postTransactionCommand;

    public PostTransactionController(PostTransactionCommand postTransactionCommand) {

        assert postTransactionCommand != null;

        this.postTransactionCommand = postTransactionCommand;
    }

    @PostMapping("/ledgers/post-transaction")
    public PostTransactionCommand.Output execute(@Valid @RequestBody PostTransactionCommand.Input input)
        throws InsufficientBalanceInAccountException, DuplicatePostingInLedgerException, RestoreFailedInAccountException, OverdraftLimitReachedInAccountException {

        return this.postTransactionCommand.execute(input);
    }

}
