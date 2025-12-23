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

package org.mojave.core.transfer.domain.command.step.stateful;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.transfer.contract.command.step.stateful.ReserveTransferStep;
import org.mojave.core.transfer.domain.repository.TransferRepository;
import org.mojave.fspiop.component.error.FspiopErrors;
import org.mojave.fspiop.component.exception.FspiopException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReserveTransferStepHandler implements ReserveTransferStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReserveTransferStepHandler.class);

    private final TransferRepository transferRepository;

    public ReserveTransferStepHandler(TransferRepository transferRepository) {

        assert transferRepository != null;

        this.transferRepository = transferRepository;
    }

    @Transactional
    @Write
    @Override
    public void execute(ReserveTransferStep.Input input) throws FspiopException {

        MDC.put("REQ_ID", input.udfTransferId().getId());

        var startAt = System.nanoTime();

        LOGGER.info("ReserveTransferStep : input : ({})", ObjectLogger.log(input));

        try {

            var transfer = this.transferRepository.getReferenceById(input.transferId());

            transfer.reserved(input.positionReservationId());

            this.transferRepository.save(transfer);

            var endAt = System.nanoTime();
            LOGGER.info("ReservedTransfer : done, took {} ms", (endAt - startAt) / 1_000_000);

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, e.getMessage());

        } finally {
            MDC.remove("REQ_ID");
        }
    }

}
