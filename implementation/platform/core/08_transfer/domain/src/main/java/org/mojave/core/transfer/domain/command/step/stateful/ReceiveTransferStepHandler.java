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
import org.mojave.core.common.datatype.enums.Direction;
import org.mojave.core.common.datatype.enums.trasaction.StepPhase;
import org.mojave.core.common.datatype.enums.trasaction.TransactionType;
import org.mojave.core.common.datatype.identifier.transaction.TransactionId;
import org.mojave.core.transaction.contract.command.AddStepCommand;
import org.mojave.core.transaction.contract.command.OpenTransactionCommand;
import org.mojave.core.transaction.producer.publisher.AddStepPublisher;
import org.mojave.core.transfer.TransferDomainConfiguration;
import org.mojave.core.transfer.contract.command.step.stateful.ReceiveTransferStep;
import org.mojave.core.transfer.domain.model.Party;
import org.mojave.core.transfer.domain.model.Transfer;
import org.mojave.core.transfer.domain.repository.TransferIlpPacketRepository;
import org.mojave.core.transfer.domain.repository.TransferRepository;
import org.mojave.fspiop.common.error.FspiopErrors;
import org.mojave.fspiop.common.exception.FspiopException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class ReceiveTransferStepHandler implements ReceiveTransferStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiveTransferStepHandler.class);

    private final OpenTransactionCommand openTransactionCommand;

    private final TransferDomainConfiguration.TransferSettings transferSettings;

    private final AddStepPublisher addStepPublisher;

    private final TransferRepository transferRepository;

    private final TransferIlpPacketRepository transferIlpPacketRepository;

    public ReceiveTransferStepHandler(OpenTransactionCommand openTransactionCommand,
                                      TransferDomainConfiguration.TransferSettings transferSettings,
                                      AddStepPublisher addStepPublisher,
                                      TransferRepository transferRepository,
                                      TransferIlpPacketRepository transferIlpPacketRepository) {

        assert openTransactionCommand != null;
        assert transferSettings != null;
        assert addStepPublisher != null;
        assert transferRepository != null;
        assert transferIlpPacketRepository != null;

        this.openTransactionCommand = openTransactionCommand;
        this.transferSettings = transferSettings;
        this.addStepPublisher = addStepPublisher;
        this.transferRepository = transferRepository;
        this.transferIlpPacketRepository = transferIlpPacketRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Write
    @Override
    public ReceiveTransferStep.Output execute(ReceiveTransferStep.Input input)
        throws FspiopException {

        var startAt = System.nanoTime();

        LOGGER.info("ReceiveTransferStep : input : ({})", ObjectLogger.log(input));

        final var CONTEXT = input.context();
        final var STEP_NAME = "ReceiveTransferStep";

        TransactionId transactionId = null;
        Instant transactionAt = null;

        try {

            var optExistingIlp = this.transferIlpPacketRepository.findOne(
                TransferIlpPacketRepository.Filters.withIlpCondition(input.ilpCondition()));

            if (optExistingIlp.isPresent()) {

                LOGGER.warn("Duplicate ILP condition : ({})", input.ilpCondition());

                throw new FspiopException(
                    FspiopErrors.GENERIC_VALIDATION_ERROR, "Duplicate ILP condition.");
            }

            var payerFsp = input.payerFsp();
            var payeeFsp = input.payeeFsp();

            var payerPartyIdInfo = input.agreement().payer();
            var payeePartyIdInfo = input.agreement().payee();

            var openTransactionOutput = this.openTransactionCommand.execute(
                new OpenTransactionCommand.Input(TransactionType.FUND_TRANSFER));

            transactionId = openTransactionOutput.transactionId();

            transactionAt = openTransactionOutput.transactionAt();
            var reservationTimeoutAt = Instant
                                           .now()
                                           .plusMillis(
                                               this.transferSettings.reservationTimeoutMs());

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    transactionId, STEP_NAME, CONTEXT, ObjectLogger.log(input).toString(),
                    StepPhase.BEFORE));

            var transfer = new Transfer(
                transactionId, transactionAt, input.udfTransferId(), payerFsp.code(), new Party(
                payerPartyIdInfo.getPartyIdType(), payerPartyIdInfo.getPartyIdentifier(),
                payerPartyIdInfo.getPartySubIdOrType()), payeeFsp.code(), new Party(
                payeePartyIdInfo.getPartyIdType(), payeePartyIdInfo.getPartyIdentifier(),
                payeePartyIdInfo.getPartySubIdOrType()), input.agreement().transferAmount(),
                input.agreement().payeeFspFee(), input.agreement().payeeFspCommission(),
                input.agreement().payeeReceiveAmount(), input.ilpPacket(), input.ilpCondition(),
                input.requestExpiration(), reservationTimeoutAt);

            var extensionList = input.extensionList();

            if (extensionList != null) {

                for (var extension : extensionList.getExtension()) {
                    transfer.addExtension(
                        Direction.TO_PAYEE, extension.getKey(), extension.getValue());
                }
            }

            this.transferRepository.save(transfer);

            var output = new ReceiveTransferStep.Output(
                transactionId, transactionAt, transfer.getId());

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    transactionId, STEP_NAME, CONTEXT, ObjectLogger.log(output).toString(),
                    StepPhase.AFTER));

            var endAt = System.nanoTime();
            LOGGER.info(
                "ReceivedTransfer : output : ({}) , took : {} ms", output,
                (endAt - startAt) / 1_000_000);

            return output;

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            if (transactionId != null) {
                this.addStepPublisher.publish(
                    new AddStepCommand.Input(
                        transactionId, STEP_NAME, CONTEXT, e.getMessage(),
                        StepPhase.ERROR));
            }

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, e.getMessage());
        }

    }

}
