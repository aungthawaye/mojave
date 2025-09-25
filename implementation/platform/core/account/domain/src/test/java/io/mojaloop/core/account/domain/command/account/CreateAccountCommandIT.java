/*-
 * ================================================================================
 * Mojaloop OSS
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

package io.mojaloop.core.account.domain.command.account;

import io.mojaloop.core.account.contract.command.account.CreateAccountCommand;
import io.mojaloop.core.account.contract.command.chart.CreateChartCommand;
import io.mojaloop.core.account.contract.command.chart.CreateChartEntryCommand;
import io.mojaloop.core.account.domain.TestConfiguration;
import io.mojaloop.core.account.domain.repository.AccountRepository;
import io.mojaloop.core.account.domain.repository.ChartEntryRepository;
import io.mojaloop.core.account.domain.repository.ChartRepository;
import io.mojaloop.core.common.datatype.enums.account.AccountType;
import io.mojaloop.core.common.datatype.enums.account.OverdraftMode;
import io.mojaloop.core.common.datatype.identifier.account.OwnerId;
import io.mojaloop.core.common.datatype.type.account.AccountCode;
import io.mojaloop.core.common.datatype.type.account.ChartEntryCode;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class CreateAccountCommandIT {

    @Autowired
    private CreateChartCommand createChartCommand;

    @Autowired
    private CreateChartEntryCommand createChartEntryCommand;

    @Autowired
    private CreateAccountCommand createAccountCommand;

    @Autowired
    private ChartRepository chartRepository;

    @Autowired
    private ChartEntryRepository chartEntryRepository;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    public void cleanDatabase() {

        this.accountRepository.deleteAll();
        this.chartEntryRepository.deleteAll();
        this.chartRepository.deleteAll();
    }

    public void createAccount_fullFlow_success() throws Exception {

        var hubChart = this.createChartCommand.execute(new CreateChartCommand.Input("Hub CoA"));
        var fspChart = this.createChartCommand.execute(new CreateChartCommand.Input("FSP CoA"));
    }

    @Test
    public void createAccount_success_persistsAndReturnsId() throws Exception {

        var chartOut = this.createChartCommand.execute(new CreateChartCommand.Input("Main Chart"));
        var chartId = chartOut.chartId();

        assertTrue(this.chartRepository.findById(chartId).isPresent());

        var entryOut = this.createChartEntryCommand.execute(
            new CreateChartEntryCommand.Input(chartId, new ChartEntryCode("2000"), "Deposits", "Customer deposit accounts", AccountType.LIABILITY));
        var chartEntryId = entryOut.chartEntryId();

        assertTrue(this.chartEntryRepository.findById(chartEntryId).isPresent());

        var out = this.createAccountCommand.execute(
            new CreateAccountCommand.Input(chartEntryId, new OwnerId(12345L), Currency.USD, new AccountCode("DEPOSITS"), "Deposits Account",
                                           "Customer deposits", OverdraftMode.FORBID, BigDecimal.ZERO));

        assertNotNull(out);
        assertNotNull(out.accountId());

        var saved = this.accountRepository.findById(out.accountId());
        assertTrue(saved.isPresent());

        var acc = saved.get();

        assertEquals("Deposits Account", acc.getName());
        assertEquals("DEPOSITS", acc.getCode().value());
        assertEquals(Currency.USD, acc.getCurrency());
        assertEquals("Customer deposits", acc.getDescription());
        assertNotNull(acc.getCreatedAt());
        assertEquals(chartEntryId, acc.getChartEntryId());
        assertEquals(AccountType.LIABILITY, acc.getType());
    }

}
