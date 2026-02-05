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
import org.mojave.common.datatype.enums.transfer.DisputeReason;
import org.mojave.rail.fspiop.transfer.contract.command.step.financial.RollbackReservationStep;
import org.mojave.rail.fspiop.transfer.contract.command.step.stateful.DisputeTransferStep;
import org.mojave.rail.fspiop.transfer.domain.kafka.publisher.DisputeTransferStepPublisher;
import org.mojave.core.wallet.contract.command.position.RollbackReservationCommand;
import org.mojave.rail.fspiop.component.error.FspiopErrors;
import org.mojave.rail.fspiop.component.exception.FspiopException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import java.util.Objects;

@Service
public class RollbackReservationStepHandler implements RollbackReservationStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        RollbackReservationStepHandler.class);

    private final RollbackReservationCommand rollbackReservationCommand;

    private final DisputeTransferStepPublisher disputeTransferStepPublisher;

    public RollbackReservationStepHandler(RollbackReservationCommand rollbackReservationCommand,
                                          DisputeTransferStepPublisher disputeTransferStepPublisher) {

        Objects.requireNonNull(rollbackReservationCommand);
        Objects.requireNonNull(disputeTransferStepPublisher);

        this.rollbackReservationCommand = rollbackReservationCommand;
        this.disputeTransferStepPublisher = disputeTransferStepPublisher;
    }

    @Override
    public RollbackReservationStep.Output execute(RollbackReservationStep.Input input)
        throws FspiopException {

        MDC.put("REQ_ID", input.udfTransferId().getId());
        var startAt = System.nanoTime();

        LOGGER.info("RollbackReservationStep : input : ({})", ObjectLogger.log(input));

        try {

            var rollbackReservationInput = new RollbackReservationCommand.Input(
                input.positionReservationId(), "Roll back due to : " + input.error());

            var rollbackReservationOutput = this.rollbackReservationCommand.execute(
                rollbackReservationInput);

            var output = new RollbackReservationStep.Output(
                rollbackReservationOutput.positionUpdateId());

            var endAt = System.nanoTime();
            LOGGER.info(
                "RollbackReservationStep : output : ({}) , took : {} ms", ObjectLogger.log(output),
                (endAt - startAt) / 1_000_000);

            return output;

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            this.disputeTransferStepPublisher.publish(new DisputeTransferStep.Input(
                input.udfTransferId(), input.transactionId(), input.transferId(),
                DisputeReason.RESERVATION_ROLLBACK));

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, e.getMessage());

        } finally {
            MDC.remove("REQ_ID");
        }
    }

}
