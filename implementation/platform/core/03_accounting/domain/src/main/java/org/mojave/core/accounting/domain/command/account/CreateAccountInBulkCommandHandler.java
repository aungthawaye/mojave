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
import org.mojave.core.accounting.contract.command.account.CreateAccountInBulkCommand;
import org.mojave.core.accounting.domain.repository.ChartEntryRepository;
import org.mojave.core.common.datatype.enums.accounting.OverdraftMode;
import org.mojave.core.common.datatype.type.accounting.AccountCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class CreateAccountInBulkCommandHandler implements CreateAccountInBulkCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        CreateAccountInBulkCommandHandler.class);

    private final CreateAccountCommand createAccountCommand;

    private final ChartEntryRepository chartEntryRepository;

    public CreateAccountInBulkCommandHandler(CreateAccountCommand createAccountCommand,
                                             ChartEntryRepository chartEntryRepository) {

        assert createAccountCommand != null;
        assert chartEntryRepository != null;

        this.createAccountCommand = createAccountCommand;
        this.chartEntryRepository = chartEntryRepository;
    }

    @Transactional
    @Write
    @Override
    public Output execute(Input input) {

        LOGGER.info("CreateAccountInBulkCommand : input: ({})", ObjectLogger.log(input));

        final var entries = this.chartEntryRepository.findAll(
            ChartEntryRepository.Filters.withCategory(input.category()));

        final var accountIds = entries.stream().map(entry -> {

            var generic =
                input.fspCode().value() + "-" + entry.getCode().value() + "-" + input.currency();

            final var code = new AccountCode(generic);

            final var createInput = new CreateAccountCommand.Input(
                entry.getId(), input.ownerId(), input.currency(), code, generic, generic,
                OverdraftMode.LIMITED, BigDecimal.ZERO);

            final var output = this.createAccountCommand.execute(createInput);

            return output.accountId();

        }).toList();

        final var output = new Output(accountIds);

        LOGGER.info("CreateAccountInBulkCommand : output: ({})", ObjectLogger.log(output));

        return output;
    }

}
