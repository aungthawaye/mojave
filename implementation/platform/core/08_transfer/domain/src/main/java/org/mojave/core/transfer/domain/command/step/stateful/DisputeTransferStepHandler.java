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
package org.mojave.core.transfer.domain.command.step.stateful;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.common.datatype.enums.transfer.DisputeReason;
import org.mojave.core.common.datatype.enums.trasaction.StepPhase;
import org.mojave.core.common.datatype.identifier.transaction.TransactionId;
import org.mojave.core.common.datatype.identifier.transfer.TransferId;
import org.mojave.core.transaction.contract.command.AddStepCommand;
import org.mojave.core.transaction.producer.publisher.AddStepPublisher;
import org.mojave.core.transfer.domain.repository.TransferRepository;
import org.mojave.fspiop.common.error.FspiopErrors;
import org.mojave.fspiop.common.exception.FspiopException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DisputeTransferStepHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbortTransferStepHandler.class);

    private final TransferRepository transferRepository;

    private final AddStepPublisher addStepPublisher;

    public DisputeTransferStepHandler(TransferRepository transferRepository,
                                      AddStepPublisher addStepPublisher) {

        assert transferRepository != null;
        assert addStepPublisher != null;

        this.transferRepository = transferRepository;
        this.addStepPublisher = addStepPublisher;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Write
    public void execute(DisputeTransferStepHandler.Input input) throws FspiopException {

        var startAt = System.nanoTime();

        var CONTEXT = input.context;
        var STEP_NAME = "DisputeTransferStep";

        LOGGER.info("DisputeTransferStep : input : ({})", ObjectLogger.log(input));

        try {

            var transfer = this.transferRepository.getReferenceById(input.transferId);

            this.addStepPublisher.publish(new AddStepCommand.Input(
                input.transactionId, STEP_NAME, CONTEXT, ObjectLogger.log(input).toString(),
                StepPhase.BEFORE));

            transfer.disputed(input.disputeReason);

            this.transferRepository.save(transfer);

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    input.transactionId, STEP_NAME, CONTEXT, "-", StepPhase.AFTER));

            var endAt = System.nanoTime();
            LOGGER.info("DisputeTransferStep : done , took {} ms", (endAt - startAt) / 1_000_000);

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    input.transactionId, STEP_NAME, CONTEXT, e.getMessage(),
                    StepPhase.ERROR));

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, e.getMessage());
        }
    }

    public record Input(String context,
                        TransactionId transactionId,
                        TransferId transferId,
                        DisputeReason disputeReason) { }

}
