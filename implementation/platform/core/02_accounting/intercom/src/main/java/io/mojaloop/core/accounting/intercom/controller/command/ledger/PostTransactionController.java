/*-
 * ================================================================================
 * Mojave
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */
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
