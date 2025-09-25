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

package io.mojaloop.core.account.domain.component.ledger;

import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.account.contract.command.account.CreateAccountCommand;
import io.mojaloop.core.account.contract.command.chart.CreateChartCommand;
import io.mojaloop.core.account.contract.command.chart.CreateChartEntryCommand;
import io.mojaloop.core.account.contract.exception.chart.ChartIdNotFoundException;
import io.mojaloop.core.account.domain.TestConfiguration;
import io.mojaloop.core.common.datatype.enums.account.AccountType;
import io.mojaloop.core.common.datatype.enums.account.OverdraftMode;
import io.mojaloop.core.common.datatype.enums.account.Side;
import io.mojaloop.core.common.datatype.enums.trasaction.TransactionType;
import io.mojaloop.core.common.datatype.identifier.account.LedgerMovementId;
import io.mojaloop.core.common.datatype.identifier.account.OwnerId;
import io.mojaloop.core.common.datatype.identifier.participant.HubId;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.type.account.AccountCode;
import io.mojaloop.core.common.datatype.type.account.ChartEntryCode;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class LedgerIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(LedgerIT.class);

    @Autowired
    private CreateChartCommand createChartCommand;

    @Autowired
    private CreateChartEntryCommand createChartEntryCommand;

    @Autowired
    private CreateAccountCommand createAccountCommand;

    @Autowired
    private Ledger ledger;

    @Test
    public void test_postings()
        throws ChartIdNotFoundException, Ledger.InsufficientBalanceException, InterruptedException {

        var chart = this.createChartCommand.execute(new CreateChartCommand.Input("Main Chart"));

        var hubLiquidity = this.createChartEntryCommand.execute(new CreateChartEntryCommand.Input(
            chart.chartId(), new ChartEntryCode("1000"), "Hub Liquidity", "Hub Liquidity (Central Bank Trust AC)",
            AccountType.ASSET));

        var fspLiquidity = this.createChartEntryCommand.execute(new CreateChartEntryCommand.Input(
            chart.chartId(), new ChartEntryCode("2000"), "FSP Liability – Liquidity",
            "FSP Liability – Liquidity from Hub PoV", AccountType.LIABILITY));

        var hubLiquidityAcc = this.createAccountCommand.execute(new CreateAccountCommand.Input(
            hubLiquidity.chartEntryId(), new OwnerId(new HubId().getId()), Currency.USD, new AccountCode("10001"),
            "Hub Liquidity", "Hub Liquidity (USD)", OverdraftMode.FORBID, BigDecimal.ZERO));

        var fsp1LiquidityAcc = this.createAccountCommand.execute(new CreateAccountCommand.Input(
            fspLiquidity.chartEntryId(), new OwnerId(2L), Currency.USD, new AccountCode("20001"), "FSP1 Liquidity",
            "FSP1 Liability - Liquidity (USD)", OverdraftMode.FORBID, BigDecimal.ZERO));

        var fsp2LiquidityAcc = this.createAccountCommand.execute(new CreateAccountCommand.Input(
            fspLiquidity.chartEntryId(), new OwnerId(3L), Currency.USD, new AccountCode("20002"), "FSP2 Liquidity",
            "FSP2 Liability - Liquidity (USD)", OverdraftMode.FORBID, BigDecimal.ZERO));

        try (var executor = Executors.newFixedThreadPool(100)) {

            AtomicLong totalExecutionTime = new AtomicLong(0);
            AtomicInteger completedThreads = new AtomicInteger(0);

            var count = 100_000;
            var latch = new CountDownLatch(count);
            var startAt = System.nanoTime();

            for (int i = 0; i < count; i++) {

                //executor.submit(() -> {

                var requests = new ArrayList<Ledger.Request>();

                requests.add(new Ledger.Request(
                    new LedgerMovementId(Snowflake.get().nextId()), hubLiquidityAcc.accountId(), Side.DEBIT,
                    new BigDecimal(1L)));
                requests.add(new Ledger.Request(
                    new LedgerMovementId(Snowflake.get().nextId()), fsp1LiquidityAcc.accountId(), Side.CREDIT,
                    new BigDecimal(1L)));
                requests.add(new Ledger.Request(
                    new LedgerMovementId(Snowflake.get().nextId()), fsp1LiquidityAcc.accountId(), Side.DEBIT,
                    new BigDecimal(1L)));
                requests.add(new Ledger.Request(
                    new LedgerMovementId(Snowflake.get().nextId()), hubLiquidityAcc.accountId(), Side.CREDIT,
                    new BigDecimal(1L)));

                try {

                    var threadStartAt = System.nanoTime();

                    this.ledger.post(
                        requests, new TransactionId(Snowflake.get().nextId()), Instant.now(),
                        TransactionType.FUND_IN);

                    var threadEndAt = System.nanoTime();

                    LOGGER.info("Thread {} took {}ns", Thread.currentThread().getName(), threadEndAt - threadStartAt);

                    totalExecutionTime.addAndGet(threadEndAt - threadStartAt);
                    completedThreads.incrementAndGet();

                    latch.countDown();

                } catch (Ledger.InsufficientBalanceException | Ledger.OverdraftExceededException |
                         Ledger.RestoreFailedException e) {

                    throw new RuntimeException(e);

                }

                //});
            }

            latch.await();
            var endAt = System.nanoTime();
            var duration = (endAt - startAt);

            LOGGER.info("Took: {}ns | {}ms", duration, duration / 1_000_000);
            LOGGER.info(
                "Average execution time for posting: {}ms",
                (totalExecutionTime.get() / count) / 1_000_000);
        }

    }

}
