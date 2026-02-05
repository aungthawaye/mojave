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

package org.mojave.core.accounting.domain.command.account;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.accounting.contract.command.account.CreateAccountCommand;
import org.mojave.core.accounting.contract.exception.account.AccountCodeAlreadyExistsException;
import org.mojave.core.accounting.contract.exception.chart.ChartEntryIdNotFoundException;
import org.mojave.provider.ledger.contract.Ledger;
import org.mojave.core.accounting.domain.model.Account;
import org.mojave.core.accounting.domain.repository.AccountRepository;
import org.mojave.core.accounting.domain.repository.ChartEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class CreateAccountCommandHandler implements CreateAccountCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateAccountCommandHandler.class);

    private final AccountRepository accountRepository;

    private final ChartEntryRepository chartEntryRepository;

    private final Ledger ledger;

    public CreateAccountCommandHandler(AccountRepository accountRepository,
                                       ChartEntryRepository chartEntryRepository,
                                       Ledger ledger) {

        Objects.requireNonNull(accountRepository);
        Objects.requireNonNull(chartEntryRepository);
        Objects.requireNonNull(ledger);

        this.accountRepository = accountRepository;
        this.chartEntryRepository = chartEntryRepository;
        this.ledger = ledger;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) {

        LOGGER.info("CreateAccountCommand : input: ({})", ObjectLogger.log(input));

        var chartEntry = this.chartEntryRepository
                             .findById(input.chartEntryId())
                             .orElseThrow(
                                 () -> new ChartEntryIdNotFoundException(input.chartEntryId()));

        var exist = this.accountRepository
                        .findOne(AccountRepository.Filters.withCode(input.code()))
                        .isPresent();

        if (exist) {
            LOGGER.info("Account code already exists: ({})", input.code());
            throw new AccountCodeAlreadyExistsException(input.code());
        }

        var account = new Account(
            chartEntry, input.ownerId(), input.currency(), input.code(),
            input.name(), input.description(), input.overdraftMode(), input.overdraftLimit());

        account = this.accountRepository.save(account);

        try {
            this.ledger.createLedgerBalance(new Ledger.LedgerBalance(
                account.getId(), account.getCurrency(), input.currency().getScale(),
                account.getType().getSide(), BigDecimal.ZERO, BigDecimal.ZERO,
                input.overdraftMode(), input.overdraftLimit(), account.getCreatedAt()));
            LOGGER.info("Ledger balance created for account: ({})", account.getId());

        } catch (Ledger.AccountIdAlreadyTakenException e) {

            LOGGER.error("Error:", e);
            throw new RuntimeException(e);
        }

        var output = new Output(account.getId());

        LOGGER.info("CreateAccountCommand : output: ({})", ObjectLogger.log(output));

        return output;
    }

}
