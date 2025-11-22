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
import io.mojaloop.core.common.datatype.enums.Direction;
import io.mojaloop.core.common.datatype.enums.trasaction.StepPhase;
import io.mojaloop.core.common.datatype.enums.trasaction.TransactionType;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.transfer.TransferId;
import io.mojaloop.core.common.datatype.identifier.transfer.UdfTransferId;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.transaction.contract.command.AddStepCommand;
import io.mojaloop.core.transaction.contract.command.OpenTransactionCommand;
import io.mojaloop.core.transaction.producer.publisher.AddStepPublisher;
import io.mojaloop.core.transfer.TransferDomainConfiguration;
import io.mojaloop.core.transfer.domain.model.Party;
import io.mojaloop.core.transfer.domain.model.Transfer;
import io.mojaloop.core.transfer.domain.repository.TransferRepository;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.spec.core.Currency;
import io.mojaloop.fspiop.spec.core.ExtensionList;
import io.mojaloop.fspiop.spec.core.PartyIdInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class ReceiveTransfer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiveTransfer.class);

    private final OpenTransactionCommand openTransactionCommand;

    private final TransferDomainConfiguration.TransferSettings transferSettings;

    private final AddStepPublisher addStepPublisher;

    private final TransferRepository transferRepository;

    public ReceiveTransfer(OpenTransactionCommand openTransactionCommand,
                           TransferDomainConfiguration.TransferSettings transferSettings,
                           AddStepPublisher addStepPublisher,
                           TransferRepository transferRepository) {

        assert openTransactionCommand != null;
        assert transferSettings != null;
        assert addStepPublisher != null;
        assert transferRepository != null;

        this.openTransactionCommand = openTransactionCommand;
        this.transferSettings = transferSettings;
        this.addStepPublisher = addStepPublisher;
        this.transferRepository = transferRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Write
    public Output execute(Input input) throws FspiopException {

        LOGGER.info("Receiving transfer request : input : [{}]", input);

        final var CONTEXT = input.context;
        final var STEP_NAME = "receive-transfer";

        TransactionId transactionId = null;
        Instant transactionAt = null;

        try {

            var payerFsp = input.payerFsp();
            var payeeFsp = input.payeeFsp();

            var payerPartyIdInfo = input.payerPartyIdInfo();
            var payeePartyIdInfo = input.payeePartyIdInfo();

            var openTransactionOutput = this.openTransactionCommand.execute(new OpenTransactionCommand.Input(TransactionType.FUND_TRANSFER));

            transactionId = openTransactionOutput.transactionId();
            var transactionIdString = transactionId.getId().toString();

            transactionAt = openTransactionOutput.transactionAt();
            var transactionAtString = transactionAt.getEpochSecond() + "";

            var transferAmountString = input.transferAmount().stripTrailingZeros().toPlainString();
            var reservationTimeoutAt = Instant.now().plusMillis(this.transferSettings.reservationTimeoutMs());

            var extensions = new HashMap<String, String>();

            input.extensionList.getExtension().forEach(e -> extensions.put(e.getKey(), e.getValue()));

            var before = new HashMap<>(extensions);
            var after = new HashMap<String, String>();

            before.put("transactionId", transactionIdString);
            before.put("transactionAt", transactionAtString);
            before.put("udfTransferId", input.udfTransferId.getId());

            before.put("payerFspCode", payerFsp.fspCode().value());
            before.put("payerPartyIdType", payerPartyIdInfo.getPartyIdType().name());
            before.put("payerPartyIdentifier", payerPartyIdInfo.getPartyIdentifier());
            before.put("payerPartySubIdOrType", payerPartyIdInfo.getPartySubIdOrType());

            before.put("payeeFspCode", payeeFsp.fspCode().value());
            before.put("payeePartyIdType", payeePartyIdInfo.getPartyIdType().name());
            before.put("payeePartyIdentifier", payeePartyIdInfo.getPartyIdentifier());
            before.put("payeePartySubIdOrType", payeePartyIdInfo.getPartySubIdOrType());

            before.put("currency", input.currency.name());
            before.put("amount", transferAmountString);
            before.put("requestExpiration", "" + (input.requestExpiration != null ? input.requestExpiration.getEpochSecond() : 0));
            before.put("reservationTimeoutAt", reservationTimeoutAt.getEpochSecond() + "");

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, STEP_NAME, CONTEXT, before, StepPhase.BEFORE));

            before.clear();

            var transfer = new Transfer(transactionId, transactionAt, input.udfTransferId, payerFsp.fspCode(),
                new Party(payerPartyIdInfo.getPartyIdType(), payerPartyIdInfo.getPartyIdentifier(), payerPartyIdInfo.getPartySubIdOrType()), payeeFsp.fspCode(),
                new Party(payeePartyIdInfo.getPartyIdType(), payeePartyIdInfo.getPartyIdentifier(), payeePartyIdInfo.getPartySubIdOrType()), input.currency, input.transferAmount,
                input.ilpPacket, input.ilpCondition, input.requestExpiration, reservationTimeoutAt);

            transfer.addExtension(Direction.TO_PAYEE, "payerFspCode", payerFsp.fspCode().value());
            transfer.addExtension(Direction.TO_PAYEE, "payerPartyIdType", payerPartyIdInfo.getPartyIdType().name());
            transfer.addExtension(Direction.TO_PAYEE, "payerPartyIdentifier", payerPartyIdInfo.getPartyIdentifier());
            transfer.addExtension(Direction.TO_PAYEE, "payerPartySubIdOrType", payerPartyIdInfo.getPartySubIdOrType());

            transfer.addExtension(Direction.TO_PAYEE, "payeeFspCode", payeeFsp.fspCode().value());
            transfer.addExtension(Direction.TO_PAYEE, "payeePartyIdType", payeePartyIdInfo.getPartyIdType().name());
            transfer.addExtension(Direction.TO_PAYEE, "payeePartyIdentifier", payeePartyIdInfo.getPartyIdentifier());
            transfer.addExtension(Direction.TO_PAYEE, "payeePartySubIdOrType", payeePartyIdInfo.getPartySubIdOrType());

            this.transferRepository.save(transfer);

            after.put("transferId", transfer.getId().toString());

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, STEP_NAME, CONTEXT, after, StepPhase.AFTER));

            after.clear();

            var output = new Output(transactionId, transactionAt, transfer.getId());

            LOGGER.info("Received transfer successfully: output : [{}]", output);

            return output;

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            if (transactionId != null) {
                this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, STEP_NAME, CONTEXT, Map.of("error", e.getMessage()), StepPhase.ERROR));
            }

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, e.getMessage());
        }

    }

    public record Input(String context,
                        UdfTransferId udfTransferId,
                        FspData payerFsp,
                        FspData payeeFsp,
                        PartyIdInfo payerPartyIdInfo,
                        PartyIdInfo payeePartyIdInfo,
                        Currency currency,
                        BigDecimal transferAmount,
                        String ilpPacket,
                        String ilpCondition,
                        Instant requestExpiration,
                        ExtensionList extensionList) { }

    public record Output(TransactionId transactionId, Instant transactionAt, TransferId transferId) { }

}
