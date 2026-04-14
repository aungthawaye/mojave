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

import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.wallet.contract.command.position.ReservePositionCommand;
import org.mojave.core.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import org.mojave.core.wallet.contract.exception.position.PositionLimitExceededException;
import org.mojave.core.wallet.store.WalletStore;
import org.mojave.rail.fspiop.component.error.FspiopErrors;
import org.mojave.rail.fspiop.component.exception.FspiopException;
import org.mojave.rail.fspiop.transfer.contract.command.step.financial.ReservePayerPositionStep;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.identifier.wallet.WalletOwnerId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ReservePayerPositionStepHandler implements ReservePayerPositionStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        ReservePayerPositionStepHandler.class);

    private final WalletStore walletStore;

    private final ReservePositionCommand reservePositionCommand;

    public ReservePayerPositionStepHandler(final WalletStore walletStore,
                                           final ReservePositionCommand reservePositionCommand) {

        Objects.requireNonNull(walletStore);
        Objects.requireNonNull(reservePositionCommand);

        this.walletStore = walletStore;
        this.reservePositionCommand = reservePositionCommand;
    }

    @Override
    public ReservePayerPositionStep.Output execute(final ReservePayerPositionStep.Input input)
        throws
        FspiopException,
        NoPositionUpdateForTransactionException,
        PositionLimitExceededException {

        MDC.put("REQ_ID", input.udfTransferId().getId());

        final var startAt = System.nanoTime();

        LOGGER.info("ReservePayerPositionStep : input : ({})", ObjectLogger.log(input));

        try {

            final var payerFsp = input.payerFsp();
            final var currency = input.currency();

            final var transferAmount = input.transferAmount();

            final var transactionId = input.transactionId();
            final var transactionAt = input.transactionAt();

            final var walletOwnerId = new WalletOwnerId(payerFsp.fspId().getId());
            final var wallet = this.walletStore.getWalletData(
                walletOwnerId, Currency.valueOf(currency.toString()), input.scenario());
            final var description = "-";

            final var reservePayerPositionInput = new ReservePositionCommand.Input(
                wallet.walletId(), transferAmount, transactionId, transactionAt, description);

            final var reservePositionOutput = this.reservePositionCommand.execute(
                reservePayerPositionInput);

            final var output = new ReservePayerPositionStep.Output(
                reservePositionOutput.positionUpdateId());

            final var endAt = System.nanoTime();
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

        } finally {
            MDC.remove("REQ_ID");
        }

    }

}
