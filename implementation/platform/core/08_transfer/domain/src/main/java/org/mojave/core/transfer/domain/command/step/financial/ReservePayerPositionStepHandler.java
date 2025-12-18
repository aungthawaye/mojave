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
import org.mojave.core.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.core.transfer.contract.command.step.financial.ReservePayerPositionStep;
import org.mojave.core.wallet.contract.command.position.ReservePositionCommand;
import org.mojave.core.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import org.mojave.core.wallet.contract.exception.position.PositionLimitExceededException;
import org.mojave.fspiop.component.error.FspiopErrors;
import org.mojave.fspiop.component.exception.FspiopException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ReservePayerPositionStepHandler implements ReservePayerPositionStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        ReservePayerPositionStepHandler.class);

    private final ReservePositionCommand reservePositionCommand;

    public ReservePayerPositionStepHandler(ReservePositionCommand reservePositionCommand) {

        assert reservePositionCommand != null;

        this.reservePositionCommand = reservePositionCommand;
    }

    @Override
    public ReservePayerPositionStep.Output execute(ReservePayerPositionStep.Input input) throws
                                                                                         FspiopException,
                                                                                         NoPositionUpdateForTransactionException,
                                                                                         PositionLimitExceededException {

        var startAt = System.nanoTime();

        LOGGER.info("ReservePayerPositionStep : input : ({})", ObjectLogger.log(input));

        try {

            var payerFsp = input.payerFsp();
            var payerFspCode = payerFsp.code();

            var payeeFsp = input.payeeFsp();
            var payeeFspCode = payeeFsp.code();

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

            var reservePositionOutput = this.reservePositionCommand.execute(
                reservePayerPositionInput);

            var output = new ReservePayerPositionStep.Output(
                reservePositionOutput.positionUpdateId());

            var endAt = System.nanoTime();
            LOGGER.info(
                "ReservePayerPositionStep : output : ({}) , took : {} ms",
                ObjectLogger.log(output), (endAt - startAt) / 1_000_000);

            return output;

        } catch (NoPositionUpdateForTransactionException e) {

            LOGGER.error("Error:", e);

            throw new FspiopException(FspiopErrors.INTERNAL_SERVER_ERROR, e.getMessage());

        } catch (PositionLimitExceededException e) {

            LOGGER.error("Error:", e);

            throw e;

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, e.getMessage());
        }

    }

}
