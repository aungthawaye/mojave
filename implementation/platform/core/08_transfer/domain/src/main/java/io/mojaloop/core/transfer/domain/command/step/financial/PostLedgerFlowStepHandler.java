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

package io.mojaloop.core.transfer.domain.command.step.financial;

import io.mojaloop.component.misc.logger.ObjectLogger;
import io.mojaloop.core.accounting.contract.command.ledger.PostLedgerFlowCommand;
import io.mojaloop.core.accounting.producer.publisher.PostLedgerFlowPublisher;
import io.mojaloop.core.common.datatype.enums.trasaction.FundTransferDimension;
import io.mojaloop.core.common.datatype.enums.trasaction.StepPhase;
import io.mojaloop.core.common.datatype.enums.trasaction.TransactionType;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountOwnerId;
import io.mojaloop.core.transaction.contract.command.AddStepCommand;
import io.mojaloop.core.transaction.producer.publisher.AddStepPublisher;
import io.mojaloop.core.transfer.contract.command.step.financial.PostLedgerFlowStep;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;

@Service
public class PostLedgerFlowStepHandler implements PostLedgerFlowStep {

    private final static Logger LOGGER = LoggerFactory.getLogger(PostLedgerFlowStepHandler.class);

    private final AddStepPublisher addStepPublisher;

    private final PostLedgerFlowPublisher postLedgerFlowPublisher;

    public PostLedgerFlowStepHandler(AddStepPublisher addStepPublisher,
                                     PostLedgerFlowPublisher postLedgerFlowPublisher) {

        assert addStepPublisher != null;
        assert postLedgerFlowPublisher != null;

        this.addStepPublisher = addStepPublisher;
        this.postLedgerFlowPublisher = postLedgerFlowPublisher;
    }

    @Override
    public void execute(PostLedgerFlowStep.Input input) throws FspiopException {

        var startAt = System.nanoTime();

        var CONTEXT = input.context();
        var STEP_NAME = "PostLedgerFlowStep";

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

            this.addStepPublisher.publish(new AddStepCommand.Input(
                input.transactionId(), STEP_NAME, CONTEXT, ObjectLogger.log(input).toString(),
                StepPhase.BEFORE));

            this.postLedgerFlowPublisher.publish(
                new PostLedgerFlowCommand.Input(
                    TransactionType.FUND_TRANSFER, input.currency(), input.transactionId(),
                    input.transactionAt(), participants, amounts));

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    input.transactionId(), STEP_NAME, CONTEXT, "-", StepPhase.AFTER));

            var endAt = System.nanoTime();
            LOGGER.info("PostLedgerFlowStep : done , took {} ms", (endAt - startAt) / 1_000_000);

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    input.transactionId(), STEP_NAME, CONTEXT, e.getMessage(),
                    StepPhase.ERROR));

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, e.getMessage());
        }
    }

}
