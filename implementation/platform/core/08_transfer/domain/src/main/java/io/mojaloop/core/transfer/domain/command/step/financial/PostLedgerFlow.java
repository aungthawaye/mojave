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
import io.mojaloop.core.common.datatype.enums.trasaction.TransactionType;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountOwnerId;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.fspiop.spec.core.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;

@Service
public class PostLedgerFlow {

    private final static Logger LOGGER = LoggerFactory.getLogger(PostLedgerFlow.class);

    private final PostLedgerFlowPublisher postLedgerFlowPublisher;

    public PostLedgerFlow(PostLedgerFlowPublisher postLedgerFlowPublisher) {

        assert postLedgerFlowPublisher != null;

        this.postLedgerFlowPublisher = postLedgerFlowPublisher;
    }

    public void execute(Input input) {

        LOGGER.info("PostLedgerFlow : input : ({})", ObjectLogger.log(input));

        var participants = new HashMap<String, AccountOwnerId>();

        participants.put(
            FundTransferDimension.Participants.PAYER_FSP.name(),
            new AccountOwnerId(input.payerFsp().fspId().getId()));
        participants.put(
            FundTransferDimension.Participants.PAYEE_FSP.name(),
            new AccountOwnerId(input.payeeFsp().fspId().getId()));

        var amounts = new HashMap<String, BigDecimal>();

        amounts.put(FundTransferDimension.Amounts.TRANSFER_AMOUNT.name(), input.transferAmount());
        amounts.put(FundTransferDimension.Amounts.PAYEE_FSP_FEE.name(), input.payeeFspFee());
        amounts.put(
            FundTransferDimension.Amounts.PAYEE_FSP_COMMISSION.name(), input.payeeFspCommission());

        this.postLedgerFlowPublisher.publish(
            new PostLedgerFlowCommand.Input(
                TransactionType.FUND_TRANSFER, input.currency(), input.transactionId,
                input.transactionAt, participants, amounts));

        LOGGER.info("PostLedgerFlow : done");
    }

    public record Input(TransactionId transactionId,
                        Instant transactionAt,
                        Currency currency,
                        FspData payerFsp,
                        FspData payeeFsp,
                        BigDecimal transferAmount,
                        BigDecimal payeeFspFee,
                        BigDecimal payeeFspCommission) { }

}
