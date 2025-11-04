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
import io.mojaloop.core.accounting.contract.command.account.CreateAccountCommand;
import io.mojaloop.core.accounting.domain.model.Account;
import io.mojaloop.core.accounting.domain.repository.AccountRepository;
import io.mojaloop.core.accounting.domain.repository.ChartEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateAccountCommandHandler implements CreateAccountCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateAccountCommandHandler.class);

    private final AccountRepository accountRepository;

    private final ChartEntryRepository chartEntryRepository;

    public CreateAccountCommandHandler(AccountRepository accountRepository, ChartEntryRepository chartEntryRepository) {

        assert accountRepository != null;
        assert chartEntryRepository != null;

        this.accountRepository = accountRepository;
        this.chartEntryRepository = chartEntryRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) {

        LOGGER.info("Executing CreateAccountCommand with input: {}", input);

        var chartEntry = this.chartEntryRepository.findById(input.chartEntryId()).orElseThrow(() -> new IllegalArgumentException("ChartEntry not found: " + input.chartEntryId()));
        LOGGER.info("Found ChartEntry with id: {}", input.chartEntryId());

        var account = new Account(chartEntry, input.ownerId(), input.currency(), input.code(), input.name(), input.description(), input.overdraftMode(), input.overdraftLimit());
        LOGGER.info("Created Account: {}", account);

        account = this.accountRepository.save(account);
        LOGGER.info("Saved Account with id: {}", account.getId());

        LOGGER.info("Completed CreateAccountCommand with input: {}", input);

        return new Output(account.getId());
    }

}
