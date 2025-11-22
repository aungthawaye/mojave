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

import io.mojaloop.core.common.datatype.enums.trasaction.StepPhase;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.transaction.contract.command.AddStepCommand;
import io.mojaloop.core.transaction.producer.publisher.AddStepPublisher;
import io.mojaloop.core.wallet.contract.command.position.DecreasePositionCommand;
import io.mojaloop.core.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import io.mojaloop.core.wallet.contract.exception.position.PositionLimitExceededException;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.spec.core.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class DecreasePayeePosition {

    private static final Logger LOGGER = LoggerFactory.getLogger(DecreasePayeePosition.class);

    private final DecreasePositionCommand decreasePositionCommand;

    private final AddStepPublisher addStepPublisher;

    public DecreasePayeePosition(DecreasePositionCommand decreasePositionCommand,
                                 AddStepPublisher addStepPublisher) {

        assert decreasePositionCommand != null;
        assert addStepPublisher != null;

        this.decreasePositionCommand = decreasePositionCommand;
        this.addStepPublisher = addStepPublisher;
    }

    public Output execute(Input input) throws
                                       FspiopException,
                                       NoPositionUpdateForTransactionException,
                                       PositionLimitExceededException {

        LOGGER.info("Decreasing payee position : input : [{}]", input);

        final var CONTEXT = input.context();
        final var STEP_NAME = "decrease-payee-position";

        try {

            var payerFsp = input.payerFsp;
            var payerFspCode = payerFsp.fspCode();

            var payeeFsp = input.payeeFsp;
            var payeeFspCode = payeeFsp.fspCode();

            var currency = input.currency();

            var transferAmount = input.transferAmount();
            var transferAmountString = transferAmount.stripTrailingZeros().toPlainString();

            var transactionId = input.transactionId();
            var transactionIdString = transactionId.getId().toString();

            var transactionAt = input.transactionAt();
            var transactionAtString = transactionAt.getEpochSecond() + "";

            var walletOwnerId = new WalletOwnerId(payeeFsp.fspId().getId());
            var description = "Transfer " + currency + " " + transferAmountString + " from " +
                                  payerFspCode.value() + " to " + payeeFspCode.value();

            var before = new HashMap<String, String>();

            before.put("walletOwnerId", walletOwnerId.getId().toString());
            before.put("currency", currency.name());
            before.put("transferAmount", transferAmountString);
            before.put("transactionId", transactionIdString);
            before.put("transactionAt", transactionAtString);
            before.put("description", description);

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    transactionId, STEP_NAME, CONTEXT, before,
                    StepPhase.BEFORE));

            var decreasePositionOutput = this.decreasePositionCommand.execute(
                new DecreasePositionCommand.Input(
                    walletOwnerId, currency, transferAmount,
                    transactionId, transactionAt, description));

            var after = new HashMap<String, String>();

            after.put(
                "positionUpdateId", decreasePositionOutput.positionUpdateId().getId().toString());
            after.put(
                "oldPosition",
                decreasePositionOutput.oldPosition().stripTrailingZeros().toPlainString());
            after.put(
                "newPosition",
                decreasePositionOutput.newPosition().stripTrailingZeros().toPlainString());
            after.put(
                "oldReserved",
                decreasePositionOutput.oldReserved().stripTrailingZeros().toPlainString());
            after.put(
                "newReserved",
                decreasePositionOutput.newReserved().stripTrailingZeros().toPlainString());

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    transactionId, STEP_NAME, CONTEXT, after,
                    StepPhase.AFTER));

            var output = new Output(decreasePositionOutput.positionUpdateId());

            LOGGER.info("Decreased payee position successfully: output : [{}]", output);

            return output;

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    input.transactionId, STEP_NAME, CONTEXT, Map.of("error", e.getMessage()),
                    StepPhase.ERROR));

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, e.getMessage());
        }

    }

    public record Input(String context,
                        TransactionId transactionId,
                        Instant transactionAt,
                        FspData payerFsp,
                        FspData payeeFsp,
                        Currency currency,
                        BigDecimal transferAmount) { }

    public record Output(PositionUpdateId payeeCommitId) { }

}
