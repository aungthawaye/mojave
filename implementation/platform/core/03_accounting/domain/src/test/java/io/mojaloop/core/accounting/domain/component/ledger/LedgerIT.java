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

package io.mojaloop.core.accounting.domain.component.ledger;

import io.mojaloop.component.jpa.routing.RoutingDataSource;
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.accounting.contract.command.account.CreateAccountCommand;
import io.mojaloop.core.accounting.contract.command.chart.CreateChartCommand;
import io.mojaloop.core.accounting.contract.command.chart.CreateChartEntryCommand;
import io.mojaloop.core.accounting.contract.exception.chart.ChartEntryCodeAlreadyExistsException;
import io.mojaloop.core.accounting.contract.exception.chart.ChartEntryNameAlreadyExistsException;
import io.mojaloop.core.accounting.contract.exception.chart.ChartIdNotFoundException;
import io.mojaloop.core.accounting.domain.TestConfiguration;
import io.mojaloop.core.common.datatype.enums.accounting.AccountType;
import io.mojaloop.core.common.datatype.enums.accounting.OverdraftMode;
import io.mojaloop.core.common.datatype.enums.accounting.Side;
import io.mojaloop.core.common.datatype.enums.trasaction.TransactionType;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountOwnerId;
import io.mojaloop.core.common.datatype.identifier.accounting.LedgerMovementId;
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
    private Ledger ledger;

    @Autowired
    public void setDataSource(final DataSource dataSource) {

        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    public void test_postings()
        throws ChartIdNotFoundException, InterruptedException, ChartEntryCodeAlreadyExistsException, ChartEntryNameAlreadyExistsException {

        var chart = this.createChartCommand.execute(new CreateChartCommand.Input("HUB"));

        var hubLiquidity = this.createChartEntryCommand.execute(new CreateChartEntryCommand.Input(chart.chartId(),
                                                                                                  new ChartEntryCode("HUB_1000"),
                                                                                                  "Hub Liquidity",
                                                                                                  "Hub Liquidity (Central Bank Trust AC)",
                                                                                                  AccountType.ASSET));

        var fspLiabilityLiquidity = this.createChartEntryCommand.execute(new CreateChartEntryCommand.Input(chart.chartId(),
                                                                                                           new ChartEntryCode("HUB_2000"),
                                                                                                           "FSP Liability – Liquidity",
                                                                                                           "FSP Liability – Liquidity from Hub to FSP in Hub PoV",
                                                                                                           AccountType.LIABILITY));

        var fspLiabilityPosition = this.createChartEntryCommand.execute(new CreateChartEntryCommand.Input(chart.chartId(),
                                                                                                          new ChartEntryCode("HUB_3000"),
                                                                                                          "FSP Liability – Position",
                                                                                                          "FSP Liability – Position from Hub to FSP in Hub PoV",
                                                                                                          AccountType.LIABILITY));

        var hubLiquidityAcc = this.createAccountCommand.execute(new CreateAccountCommand.Input(hubLiquidity.chartEntryId(),
                                                                                               new AccountOwnerId(new HubId().getId()),
                                                                                               Currency.USD,
                                                                                               new AccountCode("HUB_1000_USD"),
                                                                                               "Hub Liquidity",
                                                                                               "Hub Liquidity (USD)",
                                                                                               OverdraftMode.FORBID,
                                                                                               BigDecimal.ZERO));

        var fsp1 = new AccountOwnerId(2L);
        var fsp1_LiabilityLiquidityAcc = this.createAccountCommand.execute(new CreateAccountCommand.Input(fspLiabilityLiquidity.chartEntryId(),
                                                                                                          fsp1,
                                                                                                          Currency.USD,
                                                                                                          new AccountCode("HUB_2000_FSP1_USD"),
                                                                                                          "FSP1 Liquidity (USD)",
                                                                                                          "FSP1 Liquidity (USD)",
                                                                                                          OverdraftMode.FORBID,
                                                                                                          BigDecimal.ZERO));

        var fsp1_LiabilityPositionAcc = this.createAccountCommand.execute(new CreateAccountCommand.Input(fspLiabilityPosition.chartEntryId(),
                                                                                                         fsp1,
                                                                                                         Currency.USD,
                                                                                                         new AccountCode("HUB_3000_FSP1_USD"),
                                                                                                         "FSP1 Position (USD)",
                                                                                                         "FSP1 Position (USD)",
                                                                                                         OverdraftMode.FORBID,
                                                                                                         BigDecimal.ZERO));

        var fsp2 = new AccountOwnerId(3L);
        var fsp2_LiabilityLiquidityAcc = this.createAccountCommand.execute(new CreateAccountCommand.Input(fspLiabilityLiquidity.chartEntryId(),
                                                                                                          fsp2,
                                                                                                          Currency.USD,
                                                                                                          new AccountCode("HUB_2000_FSP2_USD"),
                                                                                                          "FSP2 Liquidity (USD)",
                                                                                                          "FSP2 Liquidity (USD)",
                                                                                                          OverdraftMode.FORBID,
                                                                                                          BigDecimal.ZERO));

        var fsp2_LiabilityPositionAcc = this.createAccountCommand.execute(new CreateAccountCommand.Input(fspLiabilityPosition.chartEntryId(),
                                                                                                         fsp2,
                                                                                                         Currency.USD,
                                                                                                         new AccountCode("HUB_3000_FSP2_USD"),
                                                                                                         "FSP2 Position (USD)",
                                                                                                         "FSP2 Position (USD)",
                                                                                                         OverdraftMode.FORBID,
                                                                                                         BigDecimal.ZERO));

        try (var executor = Executors.newFixedThreadPool(100)) {

            var count = 0;
            var latch = new CountDownLatch(count);
            var index = new AtomicInteger(0);
            var startAt = System.nanoTime();

            for (int i = 0; i < count; i++) {

                //executor.submit(() -> {

                index.incrementAndGet();
                var requests = new ArrayList<Ledger.Request>();

                requests.add(new Ledger.Request(new LedgerMovementId(Snowflake.get().nextId()), hubLiquidityAcc.accountId(), Side.DEBIT, Currency.USD, new BigDecimal(4L)));

                // Hub -> Fsp1
                requests.add(new Ledger.Request(new LedgerMovementId(Snowflake.get().nextId()),
                                                fsp1_LiabilityLiquidityAcc.accountId(),
                                                Side.CREDIT,
                                                Currency.USD,
                                                new BigDecimal(2L)));
                requests.add(new Ledger.Request(new LedgerMovementId(Snowflake.get().nextId()),
                                                fsp1_LiabilityLiquidityAcc.accountId(),
                                                Side.DEBIT,
                                                Currency.USD,
                                                new BigDecimal(1L)));
                requests.add(new Ledger.Request(new LedgerMovementId(Snowflake.get().nextId()),
                                                fsp1_LiabilityPositionAcc.accountId(),
                                                Side.CREDIT,
                                                Currency.USD,
                                                new BigDecimal(1L)));

                // Hub -> Fsp2
                requests.add(new Ledger.Request(new LedgerMovementId(Snowflake.get().nextId()),
                                                fsp2_LiabilityLiquidityAcc.accountId(),
                                                Side.CREDIT,
                                                Currency.USD,
                                                new BigDecimal(2L)));
                requests.add(new Ledger.Request(new LedgerMovementId(Snowflake.get().nextId()),
                                                fsp2_LiabilityLiquidityAcc.accountId(),
                                                Side.DEBIT,
                                                Currency.USD,
                                                new BigDecimal(1L)));
                requests.add(new Ledger.Request(new LedgerMovementId(Snowflake.get().nextId()),
                                                fsp2_LiabilityPositionAcc.accountId(),
                                                Side.CREDIT,
                                                Currency.USD,
                                                new BigDecimal(1L)));

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

                //});
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

        RoutingDataSource.setDataSourceKey(RoutingDataSource.Keys.WRITE);

        // Disable FK checks to truncate in any order
        this.jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=0");

        // Order doesn't matter with FK checks disabled
        this.jdbcTemplate.execute("TRUNCATE TABLE acc_ledger_movement");
        this.jdbcTemplate.execute("TRUNCATE TABLE acc_ledger_balance");
        this.jdbcTemplate.execute("TRUNCATE TABLE acc_account");
        this.jdbcTemplate.execute("TRUNCATE TABLE acc_chart_entry");
        this.jdbcTemplate.execute("TRUNCATE TABLE acc_chart");

        // Re-enable FK checks
        this.jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=1");
    }

}
