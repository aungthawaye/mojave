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

package org.mojave.core.accounting.domain.command.account;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.accounting.contract.command.account.TerminateAccountCommand;
import org.mojave.core.accounting.contract.exception.account.AccountIdNotFoundException;
import org.mojave.core.accounting.domain.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TerminateAccountCommandHandler implements TerminateAccountCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        TerminateAccountCommandHandler.class);

    private final AccountRepository accountRepository;

    public TerminateAccountCommandHandler(AccountRepository accountRepository) {

        assert accountRepository != null;
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) {

        LOGGER.info("TerminateAccountCommand : input: ({})", ObjectLogger.log(input));

        var account = this.accountRepository
                          .findById(input.accountId())
                          .orElseThrow(() -> new AccountIdNotFoundException(input.accountId()));

        account.terminate();

        this.accountRepository.save(account);

        var output = new Output(account.getId());

        LOGGER.info("TerminateAccountCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}
