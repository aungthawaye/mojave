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
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.store.ParticipantStore;
import io.mojaloop.core.transaction.contract.command.AddStepCommand;
import io.mojaloop.core.transaction.contract.command.CloseTransactionCommand;
import io.mojaloop.core.transaction.contract.command.OpenTransactionCommand;
import io.mojaloop.core.transaction.intercom.client.api.OpenTransaction;
import io.mojaloop.core.transaction.producer.publisher.AddStepPublisher;
import io.mojaloop.core.transaction.producer.publisher.CloseTransactionPublisher;
import io.mojaloop.core.transfer.TransferDomainConfiguration;
import io.mojaloop.core.transfer.contract.command.PostTransfersCommand;
import io.mojaloop.core.transfer.domain.component.interledger.PartyUnwrapperRegistry;
import io.mojaloop.core.transfer.domain.model.Party;
import io.mojaloop.core.transfer.domain.model.Transfer;
import io.mojaloop.core.transfer.domain.repository.TransferRepository;
import io.mojaloop.core.wallet.contract.command.position.ReservePositionCommand;
import io.mojaloop.core.wallet.contract.exception.position.PositionLimitExceededException;
import io.mojaloop.core.wallet.intercom.client.api.ReservePosition;
import io.mojaloop.core.wallet.intercom.client.exception.WalletIntercomClientException;
import io.mojaloop.core.wallet.store.PositionStore;
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
import org.interledger.core.InterledgerPreparePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class PostTransfersCommandHandler implements PostTransfersCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostTransfersCommandHandler.class);

    private final ParticipantStore participantStore;

    private final PositionStore positionStore;

    private final OpenTransaction openTransaction;

    private final ReservePosition reservePosition;

    private final RespondTransfers respondTransfers;

    private final ForwardRequest forwardRequest;

    private final AddStepPublisher addStepPublisher;

    private final CloseTransactionPublisher closeTransactionPublisher;

    private final TransferRepository transferRepository;

    private final PlatformTransactionManager transactionManager;

    private final PartyUnwrapperRegistry partyUnwrapperRegistry;

    private final TransferDomainConfiguration.TransferSettings transferSettings;

    public PostTransfersCommandHandler(ParticipantStore participantStore,
                                       PositionStore positionStore,
                                       OpenTransaction openTransaction,
                                       ReservePosition reservePosition,
                                       RespondTransfers respondTransfers,
                                       ForwardRequest forwardRequest,
                                       AddStepPublisher addStepPublisher,
                                       CloseTransactionPublisher closeTransactionPublisher,
                                       TransferRepository transferRepository,
                                       PlatformTransactionManager transactionManager,
                                       PartyUnwrapperRegistry partyUnwrapperRegistry,
                                       TransferDomainConfiguration.TransferSettings transferSettings) {

        assert participantStore != null;
        assert positionStore != null;
        assert openTransaction != null;
        assert reservePosition != null;
        assert respondTransfers != null;
        assert forwardRequest != null;
        assert addStepPublisher != null;
        assert closeTransactionPublisher != null;
        assert transferRepository != null;
        assert transactionManager != null;
        assert partyUnwrapperRegistry != null;
        assert transferSettings != null;

        this.participantStore = participantStore;
        this.positionStore = positionStore;
        this.openTransaction = openTransaction;
        this.reservePosition = reservePosition;
        this.respondTransfers = respondTransfers;
        this.forwardRequest = forwardRequest;
        this.addStepPublisher = addStepPublisher;
        this.closeTransactionPublisher = closeTransactionPublisher;
        this.transferRepository = transferRepository;
        this.transactionManager = transactionManager;
        this.partyUnwrapperRegistry = partyUnwrapperRegistry;
        this.transferSettings = transferSettings;
    }

    @Override
    public Output execute(Input input) {

        var udfTransferId = new UdfTransferId(input.transfersPostRequest().getTransferId());

        LOGGER.info("({}) Executing PostTransfersCommandHandler with input: {}", udfTransferId.getId(), input);

        OpenTransactionCommand.Output transaction = null;

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

                    throw new FspiopException(FspiopErrors.TRANSFER_EXPIRED, "The transfer request has expired. The expiration is : " + expiration);
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
            transaction = this.openTransaction.execute(new OpenTransactionCommand.Input(TransactionType.FUND_TRANSFER_RESERVE));
            LOGGER.info("({}) Opened transaction successfully: {}", udfTransferId.getId(), transaction.transactionId());

            // Save the transfer in the database.
            this.saveTransfer(payerFspCode, payeeFspCode, transaction.transactionId(), transaction.transactionAt(), udfTransferId, input, ilpPacket);

            // Reserve the position of Payer.
            this.reservePayerPosition(payerFspCode, payeeFspCode, transaction.transactionId(), transaction.transactionAt(), udfTransferId, input);

            var payeeBaseUrl = payeeFsp.endpoints().get(EndpointType.QUOTES).baseUrl();
            LOGGER.info("({}) Forwarding request to payee FSP (Url): [{}]", udfTransferId.getId(), payeeFsp);

            this.addStepPublisher.publish(new AddStepCommand.Input(transaction.transactionId(), "post-transfers|before:forward-to-payee", new HashMap<>()));
            this.forwardRequest.forward(payeeBaseUrl, input.request());
            this.addStepPublisher.publish(new AddStepCommand.Input(transaction.transactionId(), "post-transfers|after:forward-to-payee", new HashMap<>()));

            LOGGER.info("({}) Done forwarding request to payee FSP (Url): [{}]", udfTransferId.getId(), payeeFsp);

        } catch (Exception e) {

            LOGGER.error("({}) Exception occurred while executing PostTransfersCommandHandler: [{}]", udfTransferId.getId(), e.getMessage());

            if (payerFsp != null) {

                final var sendBackTo = new Payer(payerFspCode.value());
                final var baseUrl = payerFsp.endpoints().get(EndpointType.TRANSFERS).baseUrl();
                final var url = FspiopUrls.newUrl(baseUrl, input.request().uri() + "/error");

                try {

                    if (transaction != null) {

                        this.addStepPublisher.publish(
                            new AddStepCommand.Input(transaction.transactionId(), "post-transfers|before:send-error-to-payer", Map.of("error", e.getMessage())));

                        FspiopErrorResponder.toPayer(new Payer(payerFspCode.value()), e, (payer, error) -> this.respondTransfers.putTransfersError(sendBackTo, url, error));

                        this.addStepPublisher.publish(
                            new AddStepCommand.Input(transaction.transactionId(), "post-transfers|after:send-error-to-payer", Map.of("error", e.getMessage())));

                        this.closeTransactionPublisher.publish(new CloseTransactionCommand.Input(transaction.transactionId(), e.getMessage()));

                    } else {

                        LOGGER.warn("({}) Transaction was not opened. Skipping adding steps to transaction.", udfTransferId.getId());
                        FspiopErrorResponder.toPayer(new Payer(payerFspCode.value()), e, (payer, error) -> this.respondTransfers.putTransfersError(sendBackTo, url, error));
                    }

                } catch (Throwable ignored) {
                    LOGGER.error("({}) Something went wrong while sending error response to payer FSP: ", udfTransferId.getId(), e);
                }
            }
        }

        LOGGER.info("({}) Returning from PostTransfersCommandHandler successfully.", udfTransferId.getId());

        return new Output();
    }

    private void reservePayerPosition(FspCode payerFspCode, FspCode payeeFspCode, TransactionId transactionId, Instant transactionAt, UdfTransferId udfTransferId, Input input)
        throws FspiopException {

        var payerFsp = this.participantStore.getFspData(payerFspCode);
        var payerId = payerFsp.fspId();
        var currency = input.transfersPostRequest().getAmount().getCurrency();
        var transferAmount = new BigDecimal(input.transfersPostRequest().getAmount().getAmount());

        var payerPositionId = this.positionStore.get(new WalletOwnerId(payerId.getId()), currency).positionId();

        try {

            LOGGER.info(
                "({}) Reserving position for payer: [{}] , currency : [{}], amount : [{}]", udfTransferId.getId(), payerPositionId, currency,
                transferAmount.stripTrailingZeros().toPlainString());

            var description =
                "Transfer from " + payerFspCode.value() + " to " + payeeFspCode.value() + " for " + currency + " " + transferAmount.stripTrailingZeros().toPlainString();

            var params = new HashMap<String, String>();

            params.put("udfTransferId", udfTransferId.getId());
            params.put("payerFspCode", payerFspCode.value());
            params.put("payeeFspCode", payeeFspCode.value());
            params.put("currency", currency.toString());
            params.put("amount", transferAmount.stripTrailingZeros().toPlainString());
            params.put("positionId", payerPositionId.getId().toString());

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|before:reserve-position", params));

            var output = this.reservePosition.execute(new ReservePositionCommand.Input(payerPositionId, transferAmount, transactionId, transactionAt, description));

            params.clear();

            params.put("udfTransferId", udfTransferId.getId());
            params.put("positionUpdateId", output.positionUpdateId().getId().toString());
            params.put("oldPosition", output.oldPosition().stripTrailingZeros().toPlainString());
            params.put("newPosition", output.newPosition().stripTrailingZeros().toPlainString());
            params.put("oldReserved", output.oldReserved().stripTrailingZeros().toPlainString());
            params.put("newReserved", output.newReserved().stripTrailingZeros().toPlainString());

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|after:reserve-position", params));

        } catch (WalletIntercomClientException e) {

            throw this.resolveWalletIntercomException(e);
        }

    }

    private FspiopException resolveWalletIntercomException(WalletIntercomClientException e) {

        if (PositionLimitExceededException.CODE.equals(e.getCode())) {

            return new FspiopException(FspiopErrors.PAYER_LIMIT_ERROR, "Payer position limit reached.");
        }

        return new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, "Unknown error occurred.");
    }

    private void saveTransfer(FspCode payerFspCode,
                              FspCode payeeFspCode,
                              TransactionId transactionId,
                              Instant transactionAt,
                              UdfTransferId udfTransferId,
                              Input input,
                              InterledgerPreparePacket ilpPacket) {

        var unwrapper = this.partyUnwrapperRegistry.get(payeeFspCode);

        var payer = Party.empty();
        var payee = Party.empty();

        if (unwrapper == null) {

            LOGGER.warn("No unwrapper found for FSP: {}. Parties information will not be included in the transfer.", input.transfersPostRequest().getPayeeFsp());

        } else {

            var parties = unwrapper.unwrap(ilpPacket.getData());

            if (parties.payer().isPresent()) {
                var payerParty = parties.payer().get();
                payer = new Party(payerParty.getPartyIdType(), payerParty.getPartyIdentifier(), payerParty.getPartySubIdOrType());
            }

            if (parties.payee().isPresent()) {
                var payeeParty = parties.payee().get();
                payee = new Party(payeeParty.getPartyIdType(), payeeParty.getPartyIdentifier(), payeeParty.getPartySubIdOrType());
            }
        }

        var currency = input.transfersPostRequest().getAmount().getCurrency();
        var amount = new BigDecimal(input.transfersPostRequest().getAmount().getAmount());

        var transfer = new Transfer(transactionId, transactionAt, udfTransferId, payerFspCode, payer, payeeFspCode, payee, currency, amount);

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
