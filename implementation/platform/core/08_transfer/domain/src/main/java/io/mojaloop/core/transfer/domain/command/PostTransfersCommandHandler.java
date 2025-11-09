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
package io.mojaloop.core.transfer.domain.command;

import io.mojaloop.component.jpa.transaction.TransactionContext;
import io.mojaloop.core.common.datatype.enums.Direction;
import io.mojaloop.core.common.datatype.enums.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.enums.trasaction.TransactionType;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.transfer.UdfTransferId;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.store.ParticipantStore;
import io.mojaloop.core.transaction.contract.command.AddStepCommand;
import io.mojaloop.core.transaction.contract.command.OpenTransactionCommand;
import io.mojaloop.core.transaction.intercom.client.api.OpenTransaction;
import io.mojaloop.core.transaction.producer.publisher.AddStepPublisher;
import io.mojaloop.core.transfer.TransferDomainConfiguration;
import io.mojaloop.core.transfer.contract.command.PostTransfersCommand;
import io.mojaloop.core.transfer.domain.component.interledger.PartyUnwrapperRegistry;
import io.mojaloop.core.transfer.domain.model.Party;
import io.mojaloop.core.transfer.domain.model.Transfer;
import io.mojaloop.core.transfer.domain.repository.TransferRepository;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Payer;
import io.mojaloop.fspiop.component.handy.FspiopCurrencies;
import io.mojaloop.fspiop.component.handy.FspiopDates;
import io.mojaloop.fspiop.component.handy.FspiopErrorResponder;
import io.mojaloop.fspiop.component.handy.FspiopUrls;
import io.mojaloop.fspiop.component.interledger.Interledger;
import io.mojaloop.fspiop.service.api.forwarder.ForwardRequest;
import io.mojaloop.fspiop.service.api.transfers.RespondTransfers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;

@Service
public class PostTransfersCommandHandler implements PostTransfersCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostTransfersCommandHandler.class);

    private final ParticipantStore participantStore;

    private final OpenTransaction openTransaction;

    private final RespondTransfers respondTransfers;

    private final ForwardRequest forwardRequest;

    private final AddStepPublisher addStepPublisher;

    private final TransferRepository transferRepository;

    private final PlatformTransactionManager transactionManager;

    private final PartyUnwrapperRegistry partyUnwrapperRegistry;

    private final TransferDomainConfiguration.TransferSettings transferSettings;

    public PostTransfersCommandHandler(ParticipantStore participantStore,
                                       OpenTransaction openTransaction,
                                       RespondTransfers respondTransfers,
                                       ForwardRequest forwardRequest,
                                       AddStepPublisher addStepPublisher,
                                       TransferRepository transferRepository,
                                       PlatformTransactionManager transactionManager,
                                       PartyUnwrapperRegistry partyUnwrapperRegistry,
                                       TransferDomainConfiguration.TransferSettings transferSettings) {

        assert participantStore != null;
        assert openTransaction != null;
        assert respondTransfers != null;
        assert forwardRequest != null;
        assert addStepPublisher != null;
        assert transferRepository != null;
        assert transactionManager != null;
        assert partyUnwrapperRegistry != null;
        assert transferSettings != null;

        this.participantStore = participantStore;
        this.openTransaction = openTransaction;
        this.respondTransfers = respondTransfers;
        this.forwardRequest = forwardRequest;
        this.addStepPublisher = addStepPublisher;
        this.transferRepository = transferRepository;
        this.transactionManager = transactionManager;
        this.partyUnwrapperRegistry = partyUnwrapperRegistry;
        this.transferSettings = transferSettings;
    }

    @Override
    public Output execute(Input input) {

        var udfTransferId = new UdfTransferId(input.transfersPostRequest().getTransferId());

        LOGGER.info("({}) Executing PostTransfersCommandHandler with input: {}", udfTransferId.getId(), input);

        FspCode payerFspCode = null;
        FspData payerFsp = null;
        FspCode payeeFspCode = null;
        FspData payeeFsp = null;

        try {

            payerFspCode = new FspCode(input.request().payer().fspCode());
            payerFsp = this.participantStore.getFspData(payerFspCode);
            LOGGER.debug("({}) Found payer FSP: [{}]", udfTransferId.getId(), payerFsp);

            payeeFspCode = new FspCode(input.request().payee().fspCode());
            payeeFsp = this.participantStore.getFspData(payeeFspCode);
            LOGGER.debug("({}) Found payee FSP: [{}]", udfTransferId.getId(), payeeFsp);

            var postTransfersRequest = input.transfersPostRequest();

            var payerFspInRequest = postTransfersRequest.getPayerFsp();
            var payeeFspInRequest = postTransfersRequest.getPayeeFsp();

            // Make sure the payer/payee information from the request body and request header are the same.
            if (!payerFspInRequest.equals(payerFspCode.value()) || !payeeFspInRequest.equals(payeeFspCode.value())) {

                throw new FspiopException(FspiopErrors.GENERIC_VALIDATION_ERROR, "FSPs information in the request body and request header must be the same.");
            }

            var currency = postTransfersRequest.getAmount().getCurrency();
            var transferAmount = new BigDecimal(postTransfersRequest.getAmount().getAmount());

            var ilpPacket = Interledger.unwrap(postTransfersRequest.getIlpPacket());
            var ilpTransferAmount = Interledger.Amount.deserialize(ilpPacket.getAmount(), FspiopCurrencies.get(currency).scale());

            if (ilpTransferAmount.subtract(transferAmount).signum() != 0) {

                throw new FspiopException(FspiopErrors.GENERIC_VALIDATION_ERROR, "ILP/Agreement amount must be equal to transfer amount.");
            }

            var expiration = postTransfersRequest.getExpiration();
            Instant expireAt = null;

            if (expiration != null) {

                expireAt = FspiopDates.fromRequestBody(expiration);

                if (expireAt.isBefore(Instant.now())) {

                    throw new FspiopException(FspiopErrors.TRANSFER_EXPIRED, "The transfer has expired. The expiration is : " + expiration);
                }

            }

            // The required validations are OK. We continue to do these steps:
            // - Open the transaction
            // - Save the transfer in the database together with transactionId
            //      - Unwrap ILP Packet and get Party information.
            //      - Based on ILP packet's Parties information, prepare a Transfer object with/without Payer/Payee information.
            //      - Save the transfer in the database.
            //      - Publish addStep message to Kafka.
            // - Reserve the position of Payer.
            //      - Call Wallet's Increase Position API
            // - Forward the request to Payee.

            // Now start opening the transaction.
            var transaction = this.openTransaction.execute(new OpenTransactionCommand.Input(TransactionType.FUND_TRANSFER_RESERVE));
            LOGGER.info("({}) Opened transaction successfully: {}", udfTransferId.getId(), transaction.transactionId());

            // Save the transfer in the database.
            this.saveTransfer(payerFspCode, payeeFspCode, transaction.transactionId(), udfTransferId, input);

            var payeeBaseUrl = payeeFsp.endpoints().get(EndpointType.QUOTES).baseUrl();
            LOGGER.info("({}) Forwarding request to payee FSP (Url): [{}]", udfTransferId.getId(), payeeFsp);

            this.forwardRequest.forward(payeeBaseUrl, input.request());
            LOGGER.info("({}) Done forwarding request to payee FSP (Url): [{}]", udfTransferId.getId(), payeeFsp);

        } catch (Exception e) {

            LOGGER.error("({}) Exception occurred while executing PostTransfersCommandHandler: [{}]", udfTransferId.getId(), e.getMessage());

            if (payerFsp != null) {

                final var sendBackTo = new Payer(payerFspCode.value());
                final var baseUrl = payerFsp.endpoints().get(EndpointType.TRANSFERS).baseUrl();
                final var url = FspiopUrls.newUrl(baseUrl, input.request().uri() + "/error");

                try {

                    FspiopErrorResponder.toPayer(new Payer(payerFspCode.value()), e, (payer, error) -> this.respondTransfers.putTransfersError(sendBackTo, url, error));

                } catch (Throwable ignored) {
                    LOGGER.error("Something went wrong while sending error response to payer FSP: ", e);
                }
            }
        }

        LOGGER.info("({}) Returning from PostTransfersCommandHandler successfully.", udfTransferId.getId());

        return new Output();
    }

    private void saveTransfer(FspCode payerFspCode, FspCode payeeFspCode, TransactionId transactionId, UdfTransferId udfTransferId, Input input) {

        var unwrapper = this.partyUnwrapperRegistry.get(payeeFspCode);

        var payer = Party.empty();
        var payee = Party.empty();

        if (unwrapper == null) {

            LOGGER.warn("No unwrapper found for FSP: {}. Parties information will not be included in the transfer.", input.transfersPostRequest().getPayeeFsp());

        } else {

            var ilpPacket = Interledger.unwrap(input.transfersPostRequest().getIlpPacket());
            var parties = unwrapper.unwrap(ilpPacket.getData());

            var payerParty = parties.payer();
            var payeeParty = parties.payee();

            payer = new Party(payerParty.getPartyIdType(), payerParty.getPartyIdentifier(), payerParty.getPartySubIdOrType());
            payee = new Party(payeeParty.getPartyIdType(), payeeParty.getPartyIdentifier(), payeeParty.getPartySubIdOrType());
        }

        var currency = input.transfersPostRequest().getAmount().getCurrency();
        var amount = new BigDecimal(input.transfersPostRequest().getAmount().getAmount());

        var transfer = new Transfer(transactionId, udfTransferId, payerFspCode, payer, payeeFspCode, payee, currency, amount);

        transfer.addExtension(Direction.TO_PAYEE, "udfTransferId", udfTransferId.getId());
        transfer.addExtension(Direction.TO_PAYEE, "payerFspCode", payerFspCode.value());
        transfer.addExtension(Direction.TO_PAYEE, "payerPartyIdType", payer.partyIdType().toString());
        transfer.addExtension(Direction.TO_PAYEE, "payerPartyIdentifier", payer.partyId());
        transfer.addExtension(Direction.TO_PAYEE, "payerPartySubIdOrType", payer.subId());
        transfer.addExtension(Direction.TO_PAYEE, "payeeFspCode", payeeFspCode.value());
        transfer.addExtension(Direction.TO_PAYEE, "payeePartyIdType", payee.partyIdType().toString());
        transfer.addExtension(Direction.TO_PAYEE, "payeePartyIdentifier", payee.partyId());
        transfer.addExtension(Direction.TO_PAYEE, "payeePartySubIdOrType", payee.subId());
        transfer.addExtension(Direction.TO_PAYEE, "currency", currency.toString());
        transfer.addExtension(Direction.TO_PAYEE, "amount", amount.stripTrailingZeros().toPlainString());
        transfer.addExtension(Direction.TO_PAYEE, "expiration", input.transfersPostRequest().getExpiration());
        transfer.addExtension(Direction.TO_PAYEE, "transferState", transfer.getState().toString());

        TransactionContext.startNew(this.transactionManager, transfer.getId().toString());
        this.transferRepository.save(transfer);
        TransactionContext.commit();

        var params = new HashMap<String, String>();

        params.put("udfTransferId", udfTransferId.getId());

        params.put("payerFspCode", payerFspCode.value());
        params.put("payerPartyIdType", payer.partyIdType().toString());
        params.put("payerPartyIdentifier", payer.partyId());

        params.put("payerPartySubIdOrType", payer.subId());
        params.put("payeeFspCode", payeeFspCode.value());
        params.put("payeePartyIdType", payee.partyIdType().toString());
        params.put("payeePartyIdentifier", payee.partyId());
        params.put("payeePartySubIdOrType", payee.subId());

        params.put("currency", currency.toString());
        params.put("amount", amount.stripTrailingZeros().toPlainString());
        params.put("expiration", input.transfersPostRequest().getExpiration());
        params.put("transferState", transfer.getState().toString());

        this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|save-transfer", params));
        LOGGER.info("({}) Saved transfer successfully: {}", udfTransferId.getId(), transfer);
    }

}
