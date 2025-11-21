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

package io.mojaloop.core.accounting.domain.component.ledger;

import io.mojaloop.component.jpa.routing.RoutingDataSource;
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.accounting.contract.command.account.CreateAccountCommand;
import io.mojaloop.core.accounting.contract.command.chart.CreateChartCommand;
import io.mojaloop.core.accounting.contract.command.chart.CreateChartEntryCommand;
import io.mojaloop.core.accounting.contract.command.definition.CreateFlowDefinitionCommand;
import io.mojaloop.core.accounting.contract.command.ledger.PostTransactionCommand;
import io.mojaloop.core.accounting.contract.exception.chart.ChartEntryCodeAlreadyExistsException;
import io.mojaloop.core.accounting.contract.exception.chart.ChartEntryNameAlreadyExistsException;
import io.mojaloop.core.accounting.contract.exception.chart.ChartIdNotFoundException;
import io.mojaloop.core.accounting.contract.exception.ledger.DuplicatePostingInLedgerException;
import io.mojaloop.core.accounting.contract.exception.ledger.InsufficientBalanceInAccountException;
import io.mojaloop.core.accounting.contract.exception.ledger.OverdraftLimitReachedInAccountException;
import io.mojaloop.core.accounting.contract.exception.ledger.RestoreFailedInAccountException;
import io.mojaloop.core.accounting.domain.TestConfiguration;
import io.mojaloop.core.common.datatype.enums.accounting.AccountType;
import io.mojaloop.core.common.datatype.enums.accounting.OverdraftMode;
import io.mojaloop.core.common.datatype.enums.accounting.ReceiveIn;
import io.mojaloop.core.common.datatype.enums.accounting.Side;
import io.mojaloop.core.common.datatype.enums.trasaction.FundInDimension;
import io.mojaloop.core.common.datatype.enums.trasaction.TransactionType;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountOwnerId;
import io.mojaloop.core.common.datatype.identifier.accounting.FlowDefinitionId;
import io.mojaloop.core.common.datatype.identifier.accounting.LedgerMovementId;
import io.mojaloop.core.common.datatype.identifier.accounting.PostingDefinitionId;
import io.mojaloop.core.common.datatype.identifier.participant.HubId;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.type.accounting.AccountCode;
import io.mojaloop.core.common.datatype.type.accounting.ChartEntryCode;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class LedgerIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(LedgerIT.class);

    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CreateChartCommand createChartCommand;

    @Autowired
    private CreateChartEntryCommand createChartEntryCommand;

    @Autowired
    private CreateAccountCommand createAccountCommand;

    @Autowired
    private CreateFlowDefinitionCommand createFlowDefinitionCommand;

    @Autowired
    private PostTransactionCommand postTransactionCommand;

    @Autowired
    private Ledger ledger;

    private CreateChartCommand.Output hubChart;

    private CreateChartEntryCommand.Output hubLiquidity;

    private CreateChartEntryCommand.Output hubFspLiquidity;

    private CreateChartEntryCommand.Output hubFspPosition;

    private CreateAccountCommand.Output hubLiquidityAcc;

    private AccountOwnerId fsp1 = new AccountOwnerId(10L);

    private CreateAccountCommand.Output hubFsp1LiquidityAcc;

    private CreateAccountCommand.Output hubFsp1PositionAcc;

    private AccountOwnerId fsp2 = new AccountOwnerId(20L);

    private CreateAccountCommand.Output hubFsp2LiquidityAcc;

    private CreateAccountCommand.Output hubFsp2PositionAcc;

    @Autowired
    public void setDataSource(final DataSource dataSource) {

        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    public void test_fundIn() throws InterruptedException {

        try (var executor = Executors.newFixedThreadPool(100)) {

            var count = 100;
            var latch = new CountDownLatch(count);
            var index = new AtomicInteger(0);
            var startAt = System.nanoTime();

            for (int i = 0; i < count; i++) {

                executor.submit(() -> {

                    try {

                        index.incrementAndGet();
                        this.postTransactionCommand.execute(
                            new PostTransactionCommand.Input(TransactionType.FUND_IN, Currency.USD, new TransactionId(Snowflake.get().nextId()), Instant.now(),
                                Map.of(FundInDimension.Participants.DEPOSIT_INTO_FSP.name(), new AccountOwnerId(fsp1.getId())),
                                Map.of(FundInDimension.Amounts.LIQUIDITY_AMOUNT.name(), new BigDecimal(1))));

                        this.postTransactionCommand.execute(
                            new PostTransactionCommand.Input(TransactionType.FUND_IN, Currency.USD, new TransactionId(Snowflake.get().nextId()), Instant.now(),
                                Map.of(FundInDimension.Participants.DEPOSIT_INTO_FSP.name(), new AccountOwnerId(fsp2.getId())),
                                Map.of(FundInDimension.Amounts.LIQUIDITY_AMOUNT.name(), new BigDecimal(1))));

                    } catch (OverdraftLimitReachedInAccountException | DuplicatePostingInLedgerException | InsufficientBalanceInAccountException |
                             RestoreFailedInAccountException e) {

                        throw new RuntimeException(e);

                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            var endAt = System.nanoTime();
            var duration = (endAt - startAt);

            LOGGER.info("Took: {}ns | {}ms", duration, duration / 1_000_000);
            LOGGER.info("Total run : {}", index.get());
        }
    }

    @Test
    public void test_postings() throws ChartIdNotFoundException, InterruptedException, ChartEntryCodeAlreadyExistsException, ChartEntryNameAlreadyExistsException {

        try (var executor = Executors.newFixedThreadPool(100)) {

            var count = 100;
            var latch = new CountDownLatch(count);
            var index = new AtomicInteger(0);
            var startAt = System.nanoTime();

            for (int i = 0; i < count; i++) {

                executor.submit(() -> {

                    index.incrementAndGet();
                    var requests = new ArrayList<Ledger.Request>();

                    requests.add(new Ledger.Request(new LedgerMovementId(Snowflake.get().nextId()), hubLiquidityAcc.accountId(), Side.DEBIT, Currency.USD, new BigDecimal(4L),
                        new FlowDefinitionId(1L), new PostingDefinitionId(1L)));

                    // Hub -> Fsp1
                    requests.add(new Ledger.Request(new LedgerMovementId(Snowflake.get().nextId()), hubFsp1LiquidityAcc.accountId(), Side.CREDIT, Currency.USD, new BigDecimal(2L),
                        new FlowDefinitionId(1L), new PostingDefinitionId(1L)));

                    requests.add(new Ledger.Request(new LedgerMovementId(Snowflake.get().nextId()), hubFsp1LiquidityAcc.accountId(), Side.DEBIT, Currency.USD, new BigDecimal(1L),
                        new FlowDefinitionId(1L), new PostingDefinitionId(1L)));

                    requests.add(new Ledger.Request(new LedgerMovementId(Snowflake.get().nextId()), hubFsp1PositionAcc.accountId(), Side.CREDIT, Currency.USD, new BigDecimal(1L),
                        new FlowDefinitionId(1L), new PostingDefinitionId(1L)));

                    // Hub -> Fsp2
                    requests.add(new Ledger.Request(new LedgerMovementId(Snowflake.get().nextId()), hubFsp2LiquidityAcc.accountId(), Side.CREDIT, Currency.USD, new BigDecimal(2L),
                        new FlowDefinitionId(1L), new PostingDefinitionId(1L)));

                    requests.add(new Ledger.Request(new LedgerMovementId(Snowflake.get().nextId()), hubFsp2LiquidityAcc.accountId(), Side.DEBIT, Currency.USD, new BigDecimal(1L),
                        new FlowDefinitionId(1L), new PostingDefinitionId(1L)));

                    requests.add(new Ledger.Request(new LedgerMovementId(Snowflake.get().nextId()), hubFsp2PositionAcc.accountId(), Side.CREDIT, Currency.USD, new BigDecimal(1L),
                        new FlowDefinitionId(1L), new PostingDefinitionId(1L)));

                    try {

                        LOGGER.info("Postings : {}", requests);

                        var threadStartAt = System.nanoTime();
                        this.ledger.post(requests, new TransactionId(Snowflake.get().nextId()), Instant.now(), TransactionType.FUND_IN);
                        var threadEndAt = System.nanoTime();

                        LOGGER.info("Thread {} took {}ns", Thread.currentThread().getName(), threadEndAt - threadStartAt);

                    } catch (Ledger.InsufficientBalanceException | Ledger.DuplicatePostingException | Ledger.OverdraftExceededException | Ledger.RestoreFailedException e) {

                        LOGGER.error("Error posting ledger movements", e);
                        throw new RuntimeException(e);

                    } finally {
                        latch.countDown();
                    }

                });
            }

            latch.await();
            var endAt = System.nanoTime();
            var duration = (endAt - startAt);

            LOGGER.info("Took: {}ns | {}ms", duration, duration / 1_000_000);
            LOGGER.info("Total run : {}", index.get());
        }

    }

    @BeforeEach
    void cleanupDb() {

        RoutingDataSource.Context.set(RoutingDataSource.Keys.WRITE);

        // Disable FK checks to truncate in any order
        this.jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=0");

        // Order doesn't matter with FK checks disabled
        this.jdbcTemplate.execute("TRUNCATE TABLE acc_ledger_movement");
        this.jdbcTemplate.execute("TRUNCATE TABLE acc_ledger_balance");
        this.jdbcTemplate.execute("TRUNCATE TABLE acc_account");
        this.jdbcTemplate.execute("TRUNCATE TABLE acc_chart_entry");
        this.jdbcTemplate.execute("TRUNCATE TABLE acc_chart");
        this.jdbcTemplate.execute("TRUNCATE TABLE acc_posting_definition");
        this.jdbcTemplate.execute("TRUNCATE TABLE acc_flow_definition");

        // Re-enable FK checks
        this.jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=1");

        this.setupCoA();
        this.setupAccounts();
        this.setupFundInDefinition();
    }

    private void setupAccounts() {

        hubLiquidityAcc = this.createAccountCommand.execute(
            new CreateAccountCommand.Input(hubLiquidity.chartEntryId(), new AccountOwnerId(new HubId().getId()), Currency.USD, new AccountCode("HUB_1000_USD"), "Hub Liquidity",
                "Hub Liquidity (USD)", OverdraftMode.FORBID, BigDecimal.ZERO));

        hubFsp1LiquidityAcc = this.createAccountCommand.execute(
            new CreateAccountCommand.Input(hubFspLiquidity.chartEntryId(), fsp1, Currency.USD, new AccountCode("HUB_2000_FSP1_USD"), "FSP1 Liquidity (USD)", "FSP1 Liquidity (USD)",
                OverdraftMode.FORBID, BigDecimal.ZERO));

        hubFsp1PositionAcc = this.createAccountCommand.execute(
            new CreateAccountCommand.Input(hubFspPosition.chartEntryId(), fsp1, Currency.USD, new AccountCode("HUB_3000_FSP1_USD"), "FSP1 Position (USD)", "FSP1 Position (USD)",
                OverdraftMode.FORBID, BigDecimal.ZERO));

        hubFsp2LiquidityAcc = this.createAccountCommand.execute(
            new CreateAccountCommand.Input(hubFspLiquidity.chartEntryId(), fsp2, Currency.USD, new AccountCode("HUB_2000_FSP2_USD"), "FSP2 Liquidity (USD)", "FSP2 Liquidity (USD)",
                OverdraftMode.FORBID, BigDecimal.ZERO));

        hubFsp2PositionAcc = this.createAccountCommand.execute(
            new CreateAccountCommand.Input(hubFspPosition.chartEntryId(), fsp2, Currency.USD, new AccountCode("HUB_3000_FSP2_USD"), "FSP2 Position (USD)", "FSP2 Position (USD)",
                OverdraftMode.FORBID, BigDecimal.ZERO));
    }

    private void setupCoA() {

        hubChart = this.createChartCommand.execute(new CreateChartCommand.Input("HUB"));

        hubLiquidity = this.createChartEntryCommand.execute(
            new CreateChartEntryCommand.Input(hubChart.chartId(), new ChartEntryCode("HUB_1000"), "Hub Liquidity", "Hub Liquidity (Central Bank Trust AC)", AccountType.ASSET));

        hubFspLiquidity = this.createChartEntryCommand.execute(new CreateChartEntryCommand.Input(hubChart.chartId(), new ChartEntryCode("HUB_2000"), "FSP Liability – Liquidity",
            "FSP Liability – Liquidity from Hub to FSP in Hub PoV", AccountType.LIABILITY));

        hubFspPosition = this.createChartEntryCommand.execute(
            new CreateChartEntryCommand.Input(hubChart.chartId(), new ChartEntryCode("HUB_3000"), "FSP Liability – Position", "FSP Liability – Position from Hub to FSP in Hub PoV",
                AccountType.LIABILITY));
    }

    private void setupFundInDefinition() {

        this.createFlowDefinitionCommand.execute(new CreateFlowDefinitionCommand.Input(TransactionType.FUND_IN, Currency.USD, "FundIn (USD)", "FSP Fund In (USD).", List.of(
            new CreateFlowDefinitionCommand.Input.Posting(ReceiveIn.ACCOUNT, hubLiquidityAcc.accountId().getId(), null, FundInDimension.Amounts.LIQUIDITY_AMOUNT.name(), Side.DEBIT,
                ""),
            new CreateFlowDefinitionCommand.Input.Posting(ReceiveIn.CHART_ENTRY, hubFspLiquidity.chartEntryId().getId(), FundInDimension.Participants.DEPOSIT_INTO_FSP.name(),
                FundInDimension.Amounts.LIQUIDITY_AMOUNT.name(), Side.CREDIT, ""))));
    }

}
