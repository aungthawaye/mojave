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
import io.mojaloop.core.accounting.contract.command.definition.AddPostingDefinitionCommand;
import io.mojaloop.core.accounting.contract.command.definition.CreateFlowDefinitionCommand;
import io.mojaloop.core.accounting.contract.exception.definition.ImmatureChartEntryException;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AddPostingDefinitionCommandIT extends BaseDomainIT {

    @Autowired
    private CreateChartCommand createChartCommand;

    @Autowired
    private CreateChartEntryCommand createChartEntryCommand;

    @Autowired
    private CreateAccountCommand createAccountCommand;

    @Autowired
    private CreateFlowDefinitionCommand createFlowDefinitionCommand;

    @Autowired
    private AddPostingDefinitionCommand addPostingDefinitionCommand;

    @Test
    void should_add_posting_definition_successfully() throws Exception {
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
                                                                                                       "Liab Desc",
                                                                                                       AccountType.LIABILITY));

        // Mature entries
        this.createAccountCommand.execute(new CreateAccountCommand.Input(debitEntry.chartEntryId(),
                                                                         new AccountOwnerId(3301L),
                                                                         Currency.USD,
                                                                         new AccountCode("ACC_D"),
                                                                         "Debit Acc",
                                                                         "Test",
                                                                         OverdraftMode.FORBID,
                                                                         BigDecimal.ZERO));
        this.createAccountCommand.execute(new CreateAccountCommand.Input(creditEntry.chartEntryId(),
                                                                         new AccountOwnerId(3301L),
                                                                         Currency.USD,
                                                                         new AccountCode("ACC_C"),
                                                                         "Credit Acc",
                                                                         "Test",
                                                                         OverdraftMode.FORBID,
                                                                         BigDecimal.ZERO));

        final var initialPostings = List.of(new CreateFlowDefinitionCommand.Input.Posting(ReceiveIn.CHART_ENTRY,
                                                                                          debitEntry.chartEntryId().getId(),
                                                                                          "DEPOSIT_INTO_FSP",
                                                                                          "LIQUIDITY_AMOUNT",
                                                                                          Side.DEBIT,
                                                                                          "Debit Assets"));

        final var created = this.createFlowDefinitionCommand.execute(new CreateFlowDefinitionCommand.Input(TransactionType.FUND_IN,
                                                                                                           Currency.USD,
                                                                                                           "Flow D",
                                                                                                           "Desc",
                                                                                                           initialPostings));

        final var addPosting = new AddPostingDefinitionCommand.Input(created.flowDefinitionId(),
                                                                     new AddPostingDefinitionCommand.Input.Posting(ReceiveIn.CHART_ENTRY,
                                                                                                                   creditEntry.chartEntryId().getId(),
                                                                                                                   "DEPOSIT_INTO_FSP",
                                                                                                                   "LIQUIDITY_AMOUNT",
                                                                                                                   Side.CREDIT,
                                                                                                                   "Credit Liabilities"));

        // Act
        final var output = this.addPostingDefinitionCommand.execute(addPosting);

        // Assert
        assertNotNull(output);
        assertNotNull(output.flowDefinitionId());
        assertNotNull(output.postingDefinitionId());
    }

    @Test
    void should_fail_when_chart_entry_is_immature() throws Exception {
        // Arrange
        final var chartOut = this.createChartCommand.execute(new CreateChartCommand.Input("Main Chart"));

        final var immatureEntry = this.createChartEntryCommand.execute(new CreateChartEntryCommand.Input(chartOut.chartId(),
                                                                                                         new ChartEntryCode("REV"),
                                                                                                         "Revenue",
                                                                                                         "Revenue Desc",
                                                                                                         AccountType.REVENUE));

        // Create definition with a matured entry first
        final var maturedEntry = this.createChartEntryCommand.execute(new CreateChartEntryCommand.Input(chartOut.chartId(),
                                                                                                        new ChartEntryCode("EXP"),
                                                                                                        "Expense",
                                                                                                        "Expense Desc",
                                                                                                        AccountType.EXPENSE));
        this.createAccountCommand.execute(new CreateAccountCommand.Input(maturedEntry.chartEntryId(),
                                                                         new AccountOwnerId(3302L),
                                                                         Currency.USD,
                                                                         new AccountCode("ACC_EXP"),
                                                                         "Expense Acc",
                                                                         "Test",
                                                                         OverdraftMode.FORBID,
                                                                         BigDecimal.ZERO));

        final var created = this.createFlowDefinitionCommand.execute(new CreateFlowDefinitionCommand.Input(TransactionType.FUND_OUT,
                                                                                                           Currency.USD,
                                                                                                           "Flow E",
                                                                                                           "Desc",
                                                                                                           List.of(new CreateFlowDefinitionCommand.Input.Posting(ReceiveIn.CHART_ENTRY,
                                                                                                                                                                 maturedEntry.chartEntryId()
                                                                                                                                                                             .getId(),
                                                                                                                                                                 "WITHDRAW_FROM_FSP",
                                                                                                                                                                 "LIQUIDITY_AMOUNT",
                                                                                                                                                                 Side.DEBIT,
                                                                                                                                                                 "Debit Expense"))));

        final var addPosting = new AddPostingDefinitionCommand.Input(created.flowDefinitionId(),
                                                                     new AddPostingDefinitionCommand.Input.Posting(ReceiveIn.CHART_ENTRY,
                                                                                                                   immatureEntry.chartEntryId().getId(),
                                                                                                                   "WITHDRAW_FROM_FSP",
                                                                                                                   "LIQUIDITY_AMOUNT",
                                                                                                                   Side.CREDIT,
                                                                                                                   "Credit Revenue"));

        // Act & Assert
        assertThrows(ImmatureChartEntryException.class, () -> this.addPostingDefinitionCommand.execute(addPosting));
    }

}
