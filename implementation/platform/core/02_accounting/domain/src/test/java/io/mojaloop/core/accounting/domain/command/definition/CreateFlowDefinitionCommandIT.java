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

package io.mojaloop.core.accounting.domain.command.definition;

import io.mojaloop.core.accounting.contract.command.account.CreateAccountCommand;
import io.mojaloop.core.accounting.contract.command.chart.CreateChartCommand;
import io.mojaloop.core.accounting.contract.command.chart.CreateChartEntryCommand;
import io.mojaloop.core.accounting.contract.command.definition.CreateFlowDefinitionCommand;
import io.mojaloop.core.accounting.contract.exception.definition.FlowDefinitionNameTakenException;
import io.mojaloop.core.accounting.contract.exception.definition.FlowDefinitionWithCurrencyExistsException;
import io.mojaloop.core.accounting.domain.command.BaseDomainIT;
import io.mojaloop.core.common.datatype.enums.accounting.AccountType;
import io.mojaloop.core.common.datatype.enums.accounting.OverdraftMode;
import io.mojaloop.core.common.datatype.enums.accounting.ReceiveIn;
import io.mojaloop.core.common.datatype.enums.accounting.Side;
import io.mojaloop.core.common.datatype.enums.trasaction.TransactionType;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountOwnerId;
import io.mojaloop.core.common.datatype.type.accounting.AccountCode;
import io.mojaloop.core.common.datatype.type.accounting.ChartEntryCode;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CreateFlowDefinitionCommandIT extends BaseDomainIT {

    @Autowired
    private CreateChartCommand createChartCommand;

    @Autowired
    private CreateChartEntryCommand createChartEntryCommand;

    @Autowired
    private CreateAccountCommand createAccountCommand;

    @Autowired
    private CreateFlowDefinitionCommand createFlowDefinitionCommand;

    @Test
    void should_create_flow_definition_successfully() throws Exception {
        // Arrange
        final var chartOut = this.createChartCommand.execute(new CreateChartCommand.Input("Main Chart"));

        final var debitEntry = this.createChartEntryCommand.execute(new CreateChartEntryCommand.Input(chartOut.chartId(),
                                                                                                      new ChartEntryCode("ASSETS"),
                                                                                                      "Assets",
                                                                                                      "Assets Desc",
                                                                                                      AccountType.ASSET));

        final var creditEntry = this.createChartEntryCommand.execute(new CreateChartEntryCommand.Input(chartOut.chartId(),
                                                                                                       new ChartEntryCode("LIAB"),
                                                                                                       "Liabilities",
                                                                                                       "Liabilities Desc",
                                                                                                       AccountType.LIABILITY));

        // Mature entries by creating accounts under them
        this.createAccountCommand.execute(new CreateAccountCommand.Input(debitEntry.chartEntryId(),
                                                                         new AccountOwnerId(2001L),
                                                                         Currency.USD,
                                                                         new AccountCode("ACC_D_ASSET"),
                                                                         "Debit Asset Acc",
                                                                         "Test",
                                                                         OverdraftMode.FORBID,
                                                                         BigDecimal.ZERO));
        this.createAccountCommand.execute(new CreateAccountCommand.Input(creditEntry.chartEntryId(),
                                                                         new AccountOwnerId(2001L),
                                                                         Currency.USD,
                                                                         new AccountCode("ACC_C_LIAB"),
                                                                         "Credit Liab Acc",
                                                                         "Test",
                                                                         OverdraftMode.FORBID,
                                                                         BigDecimal.ZERO));

        final var postings = List.of(new CreateFlowDefinitionCommand.Input.Posting(ReceiveIn.CHART_ENTRY,
                                                                                   debitEntry.chartEntryId().getId(),
                                                                                   "DEPOSIT_INTO_FSP",
                                                                                   "LIQUIDITY_AMOUNT",
                                                                                   Side.DEBIT,
                                                                                   "Debit Assets"),
                                     new CreateFlowDefinitionCommand.Input.Posting(ReceiveIn.CHART_ENTRY,
                                                                                   creditEntry.chartEntryId().getId(),
                                                                                   "DEPOSIT_INTO_FSP",
                                                                                   "LIQUIDITY_AMOUNT",
                                                                                   Side.CREDIT,
                                                                                   "Credit Liabilities"));

        final var input = new CreateFlowDefinitionCommand.Input(TransactionType.FUND_IN, Currency.USD, "FundIn (USD)", "FSP FundIn USD", postings);

        // Act
        final var output = this.createFlowDefinitionCommand.execute(input);

        // Assert
        assertNotNull(output);
        assertNotNull(output.flowDefinitionId());
        assertNotNull(output.postingDefinitionIds());
        assertEquals(2, output.postingDefinitionIds().size());
    }

    @Test
    void should_fail_when_flow_definition_currency_exists() throws Exception {
        // Arrange
        final var chartOut = this.createChartCommand.execute(new CreateChartCommand.Input("Main Chart"));
        final var entry = this.createChartEntryCommand.execute(new CreateChartEntryCommand.Input(chartOut.chartId(),
                                                                                                 new ChartEntryCode("INC"),
                                                                                                 "Income",
                                                                                                 "Income Desc",
                                                                                                 AccountType.REVENUE));

        // Mature entry by creating an account under it
        this.createAccountCommand.execute(new CreateAccountCommand.Input(entry.chartEntryId(),
                                                                         new AccountOwnerId(2002L),
                                                                         Currency.USD,
                                                                         new AccountCode("ACC_REV"),
                                                                         "Revenue Acc",
                                                                         "Test",
                                                                         OverdraftMode.FORBID,
                                                                         BigDecimal.ZERO));

        final var postings = List.of(new CreateFlowDefinitionCommand.Input.Posting(ReceiveIn.CHART_ENTRY,
                                                                                   entry.chartEntryId().getId(),
                                                                                   "DEPOSIT_INTO_FSP",
                                                                                   "LIQUIDITY_AMOUNT",
                                                                                   Side.CREDIT,
                                                                                   "Credit Income"));

        final var first = new CreateFlowDefinitionCommand.Input(TransactionType.FUND_IN, Currency.USD, "First (USD)", "First Desc", postings);

        this.createFlowDefinitionCommand.execute(first);

        final var duplicateCurrency = new CreateFlowDefinitionCommand.Input(TransactionType.FUND_IN, Currency.USD, "Second (USD)", "Second Desc", postings);

        // Act & Assert
        assertThrows(FlowDefinitionWithCurrencyExistsException.class, () -> this.createFlowDefinitionCommand.execute(duplicateCurrency));
    }

    @Test
    void should_fail_when_flow_definition_name_taken() throws Exception {
        // Arrange
        final var chartOut = this.createChartCommand.execute(new CreateChartCommand.Input("Main Chart"));
        final var entry = this.createChartEntryCommand.execute(new CreateChartEntryCommand.Input(chartOut.chartId(),
                                                                                                 new ChartEntryCode("EXP"),
                                                                                                 "Expense",
                                                                                                 "Expense Desc",
                                                                                                 AccountType.EXPENSE));

        // Mature entry by creating an account under it
        this.createAccountCommand.execute(new CreateAccountCommand.Input(entry.chartEntryId(),
                                                                         new AccountOwnerId(2003L),
                                                                         Currency.USD,
                                                                         new AccountCode("ACC_EXP"),
                                                                         "Expense Acc",
                                                                         "Test",
                                                                         OverdraftMode.FORBID,
                                                                         BigDecimal.ZERO));

        final var postings = List.of(new CreateFlowDefinitionCommand.Input.Posting(ReceiveIn.CHART_ENTRY,
                                                                                   entry.chartEntryId().getId(),
                                                                                   "WITHDRAW_FROM_FSP",
                                                                                   "LIQUIDITY_AMOUNT",
                                                                                   Side.DEBIT,
                                                                                   "Debit Expense"));

        final var name = "Common Name";
        final var first = new CreateFlowDefinitionCommand.Input(TransactionType.FUND_OUT, Currency.USD, name, "First Desc", postings);

        this.createFlowDefinitionCommand.execute(first);

        final var duplicateName = new CreateFlowDefinitionCommand.Input(TransactionType.FUND_OUT, Currency.EUR, name, "Second Desc", postings);

        // Act & Assert
        assertThrows(FlowDefinitionNameTakenException.class, () -> this.createFlowDefinitionCommand.execute(duplicateName));
    }

}
