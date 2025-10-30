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

package io.mojaloop.core.accounting.domain.command.account;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.core.accounting.contract.command.account.ActivateAccountCommand;
import io.mojaloop.core.accounting.contract.exception.account.AccountIdNotFoundException;
import io.mojaloop.core.accounting.domain.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivateAccountCommandHandler implements ActivateAccountCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivateAccountCommandHandler.class);

    private final AccountRepository accountRepository;

    public ActivateAccountCommandHandler(AccountRepository accountRepository) {

        assert accountRepository != null;
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) {

        LOGGER.info("Executing ActivateAccountCommand with input: {}", input);

        var account = this.accountRepository.findById(input.accountId()).orElseThrow(() -> new AccountIdNotFoundException(input.accountId()));
        LOGGER.info("Found Account with id: {}", input.accountId());

        account.activate();
        LOGGER.info("Activated Account with id: {}", account.getId());

        this.accountRepository.save(account);
        LOGGER.info("Saved Account with id: {}", account.getId());

        LOGGER.info("Completed ActivateAccountCommand with input: {}", input);

        return new Output(account.getId());
    }

}
