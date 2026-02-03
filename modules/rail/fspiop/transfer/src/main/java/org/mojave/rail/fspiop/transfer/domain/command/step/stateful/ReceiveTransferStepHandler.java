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

package org.mojave.rail.fspiop.transfer.domain.command.step.stateful;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.handy.Snowflake;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.common.datatype.enums.Direction;
import org.mojave.core.common.datatype.identifier.transaction.TransactionId;
import org.mojave.rail.fspiop.component.error.FspiopErrors;
import org.mojave.rail.fspiop.component.exception.FspiopException;
import org.mojave.rail.fspiop.transfer.contract.command.step.stateful.ReceiveTransferStep;
import org.mojave.rail.fspiop.transfer.domain.TransferDomainConfiguration;
import org.mojave.rail.fspiop.transfer.domain.model.Party;
import org.mojave.rail.fspiop.transfer.domain.model.Transfer;
import org.mojave.rail.fspiop.transfer.domain.repository.TransferIlpPacketRepository;
import org.mojave.rail.fspiop.transfer.domain.repository.TransferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;

@Service
public class ReceiveTransferStepHandler implements ReceiveTransferStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiveTransferStepHandler.class);

    private final TransferDomainConfiguration.TransferSettings transferSettings;

    private final TransferRepository transferRepository;

    private final TransferIlpPacketRepository transferIlpPacketRepository;

    public ReceiveTransferStepHandler(TransferDomainConfiguration.TransferSettings transferSettings,
                                      TransferRepository transferRepository,
                                      TransferIlpPacketRepository transferIlpPacketRepository) {

        Objects.requireNonNull(transferSettings);
        Objects.requireNonNull(transferRepository);
        Objects.requireNonNull(transferIlpPacketRepository);

        this.transferSettings = transferSettings;
        this.transferRepository = transferRepository;
        this.transferIlpPacketRepository = transferIlpPacketRepository;
    }

    @Transactional
    @Write
    @Override
    public ReceiveTransferStep.Output execute(ReceiveTransferStep.Input input)
        throws FspiopException {

        MDC.put("REQ_ID", input.udfTransferId().getId());

        var startAt = System.nanoTime();

        LOGGER.info("ReceiveTransferStep : input : ({})", ObjectLogger.log(input));

        TransactionId transactionId = null;
        Instant transactionAt = null;

        try {

            var where = TransferRepository.Filters.withUdfTransferId(input.udfTransferId());
            where = where.and(TransferRepository.Filters.withPayerFspId(input.payerFsp().fspId()));
            where = where.and(TransferRepository.Filters.withPayeeFspId(input.payeeFsp().fspId()));

            var optExistingTransfer = this.transferRepository.findOne(where);

            if (optExistingTransfer.isPresent()) {

                throw new FspiopException(
                    FspiopErrors.GENERIC_VALIDATION_ERROR,
                    "Transfer already exists with the same transfer ID, payer FSP and payee FSP.");
            }

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

            transactionId = new TransactionId(Snowflake.get().nextId());
            LOGGER.info("TransactionId : {}", transactionId);

            transactionAt = Instant.now();

            var reservationTimeoutAt = Instant
                                           .now()
                                           .plusMillis(
                                               this.transferSettings.reservationTimeoutMs());

            var payer = new Party(
                payerPartyIdInfo.getPartyIdType(), payerPartyIdInfo.getPartyIdentifier(),
                payerPartyIdInfo.getPartySubIdOrType());

            var payee = new Party(
                payeePartyIdInfo.getPartyIdType(), payeePartyIdInfo.getPartyIdentifier(),
                payeePartyIdInfo.getPartySubIdOrType());

            var transfer = new Transfer(
                transactionId, transactionAt, input.udfTransferId(), payerFsp.fspId(), payer,
                payeeFsp.fspId(), payee, input.agreement().amountType(),
                input.agreement().scenario(), input.agreement().subScenario(),
                input.transferAmount(), input.agreement().payeeFspFee(),
                input.agreement().payeeFspCommission(), input.agreement().payeeReceiveAmount(),
                input.ilpPacket(), input.ilpCondition(), input.requestExpiration(),
                reservationTimeoutAt);

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

            var endAt = System.nanoTime();
            LOGGER.info(
                "ReceivedTransfer : output : ({}) , took : {} ms", output,
                (endAt - startAt) / 1_000_000);

            return output;

        } catch (Exception e) {

            LOGGER.error("Error:", e);
            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, e.getMessage());

        } finally {
            MDC.remove("REQ_ID");
        }

    }

}
