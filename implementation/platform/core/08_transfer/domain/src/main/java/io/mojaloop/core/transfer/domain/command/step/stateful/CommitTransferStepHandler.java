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

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.component.misc.logger.ObjectLogger;
import io.mojaloop.core.common.datatype.enums.trasaction.StepPhase;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.transfer.TransferId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.core.transaction.contract.command.AddStepCommand;
import io.mojaloop.core.transaction.producer.publisher.AddStepPublisher;
import io.mojaloop.core.transfer.domain.repository.TransferRepository;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class CommitTransferStepHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommitTransferStepHandler.class);

    private final TransferRepository transferRepository;

    private final AddStepPublisher addStepPublisher;

    public CommitTransferStepHandler(TransferRepository transferRepository,
                                     AddStepPublisher addStepPublisher) {

        assert transferRepository != null;
        assert addStepPublisher != null;

        this.transferRepository = transferRepository;
        this.addStepPublisher = addStepPublisher;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Write
    public void execute(Input input) throws FspiopException {

        var startAt = System.nanoTime();

        var CONTEXT = input.context;
        var STEP_NAME = "CommitTransferStep";

        LOGGER.info("CommitTransferStep : input : ({})", ObjectLogger.log(input));

        try {

            var transfer = this.transferRepository.getReferenceById(input.transferId);

            this.addStepPublisher.publish(new AddStepCommand.Input(
                input.transactionId, STEP_NAME, CONTEXT, ObjectLogger.log(input).toString(),
                StepPhase.BEFORE));

            transfer.committed(
                input.ilpFulfilment, input.payerCommitId, input.payeeCommitId, input.completedAt);

            this.transferRepository.save(transfer);

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    input.transactionId, STEP_NAME, CONTEXT, "-", StepPhase.AFTER));

            var endAt = System.nanoTime();
            LOGGER.info("CommitTransferStep : done , took {} ms", (endAt - startAt) / 1_000_000);

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
                        TransferId transferId,
                        String ilpFulfilment,
                        PositionUpdateId payerCommitId,
                        PositionUpdateId payeeCommitId,
                        Instant completedAt) { }

}
