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
import io.mojaloop.core.common.datatype.enums.trasaction.StepPhase;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.transaction.contract.command.AddStepCommand;
import io.mojaloop.core.transaction.producer.publisher.AddStepPublisher;
import io.mojaloop.core.wallet.contract.command.position.ReservePositionCommand;
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

@Service
public class ReservePayerPosition {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservePayerPosition.class);

    private final ReservePositionCommand reservePositionCommand;

    private final AddStepPublisher addStepPublisher;

    public ReservePayerPosition(ReservePositionCommand reservePositionCommand,
                                AddStepPublisher addStepPublisher) {

        assert reservePositionCommand != null;
        assert addStepPublisher != null;

        this.reservePositionCommand = reservePositionCommand;
        this.addStepPublisher = addStepPublisher;
    }

    public Output execute(Input input) throws
                                       FspiopException,
                                       NoPositionUpdateForTransactionException,
                                       PositionLimitExceededException {

        var startAt = System.nanoTime();

        LOGGER.info("ReservePayerPosition : input : ({})", ObjectLogger.log(input));

        final var CONTEXT = input.context();
        final var STEP_NAME = "ReservePayerPosition";

        try {

            var payerFsp = input.payerFsp;
            var payerFspCode = payerFsp.fspCode();

            var payeeFsp = input.payeeFsp;
            var payeeFspCode = payeeFsp.fspCode();

            var currency = input.currency();

            var transferAmount = input.transferAmount();
            var transferAmountString = transferAmount.stripTrailingZeros().toPlainString();

            var transactionId = input.transactionId();
            var transactionAt = input.transactionAt();

            var walletOwnerId = new WalletOwnerId(payerFsp.fspId().getId());
            var description = "Transfer " + currency + " " + transferAmountString + " from " +
                                  payerFspCode.value() + " to " + payeeFspCode.value();

            var reservePayerPositionInput = new ReservePositionCommand.Input(
                walletOwnerId,
                currency, transferAmount, transactionId, transactionAt, description);

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    transactionId, STEP_NAME, CONTEXT,
                    ObjectLogger.log(reservePayerPositionInput).toString(), StepPhase.BEFORE));

            var reservePositionOutput = this.reservePositionCommand.execute(
                reservePayerPositionInput);

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    transactionId, STEP_NAME, CONTEXT,
                    ObjectLogger.log(reservePositionOutput).toString(), StepPhase.AFTER));

            var output = new Output(reservePositionOutput.positionUpdateId());

            var endAt = System.nanoTime();
            LOGGER.info(
                "ReservePayerPosition : output : ({}) , took : {} ms",
                ObjectLogger.log(output), (endAt - startAt) / 1_000_000);

            return output;

        } catch (NoPositionUpdateForTransactionException e) {

            LOGGER.error("Error:", e);

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    e.getTransactionId(), STEP_NAME, CONTEXT, e.getMessage(), StepPhase.ERROR));

            throw new FspiopException(FspiopErrors.INTERNAL_SERVER_ERROR, e.getMessage());

        } catch (PositionLimitExceededException e) {

            LOGGER.error("Error:", e);

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    e.getTransactionId(), STEP_NAME, CONTEXT, e.getMessage(), StepPhase.ERROR));

            throw e;

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    input.transactionId, STEP_NAME, CONTEXT, e.getMessage(), StepPhase.ERROR));

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

    public record Output(PositionUpdateId positionReservationId) { }

}
