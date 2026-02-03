/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
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
 * ===
 */
package org.mojave.core.accounting.admin.controller.api.command.ledger;

import jakarta.validation.Valid;
import org.mojave.core.accounting.contract.command.ledger.PostLedgerFlowCommand;
import org.mojave.core.accounting.contract.exception.ledger.DuplicatePostingInLedgerException;
import org.mojave.core.accounting.contract.exception.ledger.InsufficientBalanceInAccountException;
import org.mojave.core.accounting.contract.exception.ledger.OverdraftLimitReachedInAccountException;
import org.mojave.core.accounting.contract.exception.ledger.RestoreFailedInAccountException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.Objects;

@RestController
public class PostLedgerFlowController {

    private final PostLedgerFlowCommand postLedgerFlowCommand;

    public PostLedgerFlowController(PostLedgerFlowCommand postLedgerFlowCommand) {

        Objects.requireNonNull(postLedgerFlowCommand);

        this.postLedgerFlowCommand = postLedgerFlowCommand;
    }

    @PostMapping("/ledgers/post-ledger-flow")
    public PostLedgerFlowCommand.Output execute(
        @Valid @RequestBody PostLedgerFlowCommand.Input input) throws
                                                               InsufficientBalanceInAccountException,
                                                               DuplicatePostingInLedgerException,
                                                               RestoreFailedInAccountException,
                                                               OverdraftLimitReachedInAccountException {

        return this.postLedgerFlowCommand.execute(input);
    }

}
