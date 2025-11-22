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

import java.util.HashMap;
import java.util.Map;

@Service
public class ReserveTransfer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReserveTransfer.class);

    private final TransferRepository transferRepository;

    private final AddStepPublisher addStepPublisher;

    public ReserveTransfer(TransferRepository transferRepository,
                           AddStepPublisher addStepPublisher) {

        assert transferRepository != null;
        assert addStepPublisher != null;

        this.transferRepository = transferRepository;
        this.addStepPublisher = addStepPublisher;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Write
    public Output execute(Input input) throws FspiopException {

        var CONTEXT = input.context;
        var STEP_NAME = "reserve-transfer";

        try {

            LOGGER.info("Reserve transfer : input : [{}]", input);

            var before = new HashMap<String, String>();
            var after = new HashMap<String, String>();

            before.put("transactionId", input.transactionId().getId().toString());
            before.put("transferId", input.transferId().getId().toString());

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    input.transactionId(), STEP_NAME, CONTEXT, before,
                    StepPhase.BEFORE));

            var transfer = this.transferRepository.getReferenceById(input.transferId());

            transfer.reserved(input.positionReservationId());

            this.transferRepository.save(transfer);

            after.put("transferId", transfer.getId().toString());

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    input.transactionId(), STEP_NAME, CONTEXT, after,
                    StepPhase.AFTER));

            var output = new Output();

            LOGGER.info("Reserved transfer : output : [{}]", output);

            return output;

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    input.transactionId(), STEP_NAME, CONTEXT, Map.of("error", e.getMessage()),
                    StepPhase.ERROR));

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, e.getMessage());
        }
    }

    public record Output() { }

    public record Input(String context,
                        TransactionId transactionId,
                        TransferId transferId,
                        PositionUpdateId positionReservationId) { }

}
