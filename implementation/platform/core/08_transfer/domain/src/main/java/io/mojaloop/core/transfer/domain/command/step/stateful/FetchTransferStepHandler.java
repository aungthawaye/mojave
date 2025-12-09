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

package io.mojaloop.core.transfer.domain.command.step.stateful;

import io.mojaloop.component.jpa.routing.annotation.Read;
import io.mojaloop.component.misc.logger.ObjectLogger;
import io.mojaloop.core.transfer.contract.command.step.stateful.FetchTransferStep;
import io.mojaloop.core.transfer.domain.repository.TransferRepository;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class FetchTransferStepHandler implements FetchTransferStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(FetchTransferStepHandler.class);

    private final TransferRepository transferRepository;

    public FetchTransferStepHandler(TransferRepository transferRepository) {

        assert transferRepository != null;

        this.transferRepository = transferRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Read
    @Override
    public FetchTransferStep.Output execute(FetchTransferStep.Input input) throws FspiopException {

        var startAt = System.nanoTime();

        LOGGER.info("FetchTransferStep: input : ({})", ObjectLogger.log(input));

        try {

            var optTransfer = this.transferRepository.findOne(
                TransferRepository.Filters.withUdfTransferId(input.udfTransferId()));

            if (optTransfer.isEmpty()) {

                LOGGER.info(
                    "Transfer not found for udfTransferId : ({})", input.udfTransferId().getId());

                var endAt = System.nanoTime();
                var output = new FetchTransferStep.Output(null, null, null, null, null, null, null, null, null);

                LOGGER.info(
                    "FetchTransferStep : output : ({}) , took : {} ms", output,
                    (endAt - startAt) / 1_000_000);

                return output;
            }

            var transfer = optTransfer.get();

            LOGGER.info("Transfer found for udfTransferId : ({})", input.udfTransferId().getId());

            var output = new FetchTransferStep.Output(
                transfer.getId(), transfer.getStatus(), transfer.getReservationId(),
                transfer.getTransferCurrency(), transfer.getTransferAmount(), BigDecimal.ZERO,
                BigDecimal.ZERO, transfer.getTransactionId(), transfer.getTransactionAt());

            var endAt = System.nanoTime();
            LOGGER.info(
                "FetchTransferStep : output : ({}) , took : {} ms", output,
                (endAt - startAt) / 1_000_000);

            return output;

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, e.getMessage());
        }
    }

    

}
