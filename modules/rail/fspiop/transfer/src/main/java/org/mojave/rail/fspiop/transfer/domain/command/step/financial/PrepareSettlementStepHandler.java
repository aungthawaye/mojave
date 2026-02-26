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

package org.mojave.rail.fspiop.transfer.domain.command.step.financial;

import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.settlement.AmountType;
import org.mojave.common.datatype.enums.settlement.LiquidityDirection;
import org.mojave.common.datatype.enums.settlement.SettlementType;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.settlement.contract.command.record.InitiateSettlementProcessCommand;
import org.mojave.core.settlement.producer.publisher.InitiateSettlementProcessPublisher;
import org.mojave.rail.fspiop.component.error.FspiopErrors;
import org.mojave.rail.fspiop.component.exception.FspiopException;
import org.mojave.rail.fspiop.transfer.contract.command.step.financial.PrepareSettlementStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PrepareSettlementStepHandler implements PrepareSettlementStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrepareSettlementStepHandler.class);

    private final InitiateSettlementProcessPublisher initiateSettlementProcessPublisher;

    public PrepareSettlementStepHandler(
        final InitiateSettlementProcessPublisher initiateSettlementProcessPublisher) {

        Objects.requireNonNull(initiateSettlementProcessPublisher);

        this.initiateSettlementProcessPublisher = initiateSettlementProcessPublisher;
    }

    @Override
    public void execute(final Input input) throws FspiopException {

        MDC.put("REQ_ID", input.udfTransferId().getId());
        final var startAt = System.nanoTime();

        LOGGER.info("PrepareSettlementStep : input : ({})", ObjectLogger.log(input));

        try {

            final var lines = new ArrayList<InitiateSettlementProcessCommand.Input.Line>();
            final var lineNo = new AtomicInteger(1);

            this.addLinesForAmount(
                lines,
                lineNo,
                input,
                AmountType.TRANSFER_AMOUNT,
                input.transferAmount());

            this.addLinesForAmount(
                lines,
                lineNo,
                input,
                AmountType.PAYEE_FSP_FEE,
                input.payeeFspFee());

            this.addLinesForAmount(
                lines,
                lineNo,
                input,
                AmountType.HUB_FEE,
                input.payeeFspCommission());

            if (lines.isEmpty()) {
                LOGGER.info(
                    "PrepareSettlementStep : no amount lines for settlement. Skipping publish.");
                return;
            }

            final var settlementInput = new InitiateSettlementProcessCommand.Input(
                SettlementType.DFN,
                input.payerFsp().fspId(),
                input.payeeFsp().fspId(),
                input.payerFsp().fspGroupId(),
                input.payeeFsp().fspGroupId(),
                Currency.valueOf(input.currency().toString()),
                input.transferId(),
                input.transactionId(),
                input.transactionAt(),
                lines);

            this.initiateSettlementProcessPublisher.publish(settlementInput);

            final var endAt = System.nanoTime();
            LOGGER.info("PrepareSettlementStep : done , took {} ms", (endAt - startAt) / 1_000_000);

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, e.getMessage());

        } finally {
            MDC.remove("REQ_ID");
        }
    }

    private void addLinesForAmount(final List<InitiateSettlementProcessCommand.Input.Line> lines,
                                   final AtomicInteger lineNo,
                                   final Input input,
                                   final AmountType amountType,
                                   final BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        lines.add(new InitiateSettlementProcessCommand.Input.Line(
            lineNo.getAndIncrement(),
            input.payerFsp().fspId(),
            LiquidityDirection.DECREASE,
            amountType,
            amount));

        lines.add(new InitiateSettlementProcessCommand.Input.Line(
            lineNo.getAndIncrement(),
            input.payeeFsp().fspId(),
            LiquidityDirection.INCREASE,
            amountType,
            amount));
    }

}
