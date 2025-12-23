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

package org.mojave.core.transfer.domain.command.step.financial;

import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.accounting.contract.command.ledger.PostLedgerFlowCommand;
import org.mojave.core.accounting.producer.publisher.PostLedgerFlowPublisher;
import org.mojave.core.common.datatype.enums.trasaction.FundTransferDimension;
import org.mojave.core.common.datatype.enums.trasaction.TransactionType;
import org.mojave.core.common.datatype.identifier.accounting.AccountOwnerId;
import org.mojave.core.transfer.contract.command.step.financial.PostLedgerFlowStep;
import org.mojave.fspiop.component.error.FspiopErrors;
import org.mojave.fspiop.component.exception.FspiopException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;

@Service
public class PostLedgerFlowStepHandler implements PostLedgerFlowStep {

    private final static Logger LOGGER = LoggerFactory.getLogger(PostLedgerFlowStepHandler.class);

    private final PostLedgerFlowPublisher postLedgerFlowPublisher;

    public PostLedgerFlowStepHandler(PostLedgerFlowPublisher postLedgerFlowPublisher) {

        assert postLedgerFlowPublisher != null;

        this.postLedgerFlowPublisher = postLedgerFlowPublisher;
    }

    @Override
    public void execute(PostLedgerFlowStep.Input input) throws FspiopException {

        MDC.put("REQ_ID", input.udfTransferId().getId());
        var startAt = System.nanoTime();

        LOGGER.info("PostLedgerFlowStep : input : ({})", ObjectLogger.log(input));

        try {

            var participants = new HashMap<String, AccountOwnerId>();

            participants.put(
                FundTransferDimension.Participants.PAYER_FSP.name(),
                new AccountOwnerId(input.payerFsp().fspId().getId()));
            participants.put(
                FundTransferDimension.Participants.PAYEE_FSP.name(),
                new AccountOwnerId(input.payeeFsp().fspId().getId()));

            var amounts = new HashMap<String, BigDecimal>();

            amounts.put(
                FundTransferDimension.Amounts.TRANSFER_AMOUNT.name(), input.transferAmount());
            amounts.put(FundTransferDimension.Amounts.PAYEE_FSP_FEE.name(), input.payeeFspFee());
            amounts.put(
                FundTransferDimension.Amounts.PAYEE_FSP_COMMISSION.name(),
                input.payeeFspCommission());

            this.postLedgerFlowPublisher.publish(
                new PostLedgerFlowCommand.Input(
                    TransactionType.FUND_TRANSFER, input.currency(), input.transactionId(),
                    input.transactionAt(), participants, amounts));

            var endAt = System.nanoTime();
            LOGGER.info("PostLedgerFlowStep : done , took {} ms", (endAt - startAt) / 1_000_000);

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, e.getMessage());

        } finally {
            MDC.remove("REQ_ID");
        }
    }

}
