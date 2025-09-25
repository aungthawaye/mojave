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

import io.mojaloop.core.account.contract.command.account.ActivateAccountCommand;
import io.mojaloop.core.account.contract.command.account.CreateAccountCommand;
import io.mojaloop.core.account.contract.command.chart.CreateChartCommand;
import io.mojaloop.core.account.contract.command.chart.CreateChartEntryCommand;
import io.mojaloop.core.account.contract.exception.account.AccountIdNotFoundException;
import io.mojaloop.core.account.domain.TestConfiguration;
import io.mojaloop.core.account.domain.repository.AccountRepository;
import io.mojaloop.core.account.domain.repository.ChartEntryRepository;
import io.mojaloop.core.account.domain.repository.ChartRepository;
import io.mojaloop.core.common.datatype.enums.ActivationStatus;
import io.mojaloop.core.common.datatype.enums.account.AccountType;
import io.mojaloop.core.common.datatype.enums.account.OverdraftMode;
import io.mojaloop.core.common.datatype.identifier.account.AccountId;
import io.mojaloop.core.common.datatype.identifier.account.OwnerId;
import io.mojaloop.core.common.datatype.type.account.AccountCode;
import io.mojaloop.core.common.datatype.type.account.ChartEntryCode;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class ActivateAccountCommandIT {

    @Autowired
    private CreateChartCommand createChartCommand;

    @Autowired
    private CreateChartEntryCommand createChartEntryCommand;

    @Autowired
    private CreateAccountCommand createAccountCommand;

    @Autowired
    private ActivateAccountCommand activateAccountCommand;

    @Autowired
    private ChartRepository chartRepository;

    @Autowired
    private ChartEntryRepository chartEntryRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void activateAccount_success_setsStatusActive() throws Exception {
        // Arrange
        var chartOut = this.createChartCommand.execute(new CreateChartCommand.Input("Main Chart"));
        var entryOut = this.createChartEntryCommand.execute(
            new CreateChartEntryCommand.Input(chartOut.chartId(), new ChartEntryCode("3000"), "Revenue", "Revenue accounts", AccountType.REVENUE));

        var createOut = this.createAccountCommand.execute(
            new CreateAccountCommand.Input(entryOut.chartEntryId(), new OwnerId(9876L), Currency.USD, new AccountCode("REV"), "Revenue", "Revenue acc",
                                           OverdraftMode.FORBID, BigDecimal.ZERO));

        // Act
        var out = this.activateAccountCommand.execute(new ActivateAccountCommand.Input(createOut.accountId()));

        // Assert
        assertNotNull(out);
        var saved = this.accountRepository.findById(out.accountId());
        assertTrue(saved.isPresent());
        assertEquals(ActivationStatus.ACTIVE, saved.get().getActivationStatus());
    }

    @Test
    public void activateAccount_withNonExistingId_throwsAccountIdNotFoundException() {

        assertThrows(AccountIdNotFoundException.class, () -> this.activateAccountCommand.execute(new ActivateAccountCommand.Input(new AccountId(111111111L))));
    }

}
