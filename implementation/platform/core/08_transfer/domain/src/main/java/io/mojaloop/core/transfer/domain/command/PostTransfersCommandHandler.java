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
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.common.datatype.enums.Direction;
import io.mojaloop.core.common.datatype.enums.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.enums.trasaction.TransactionType;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.transfer.UdfTransferId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
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
import io.mojaloop.core.wallet.contract.command.position.RollbackPositionCommand;
import io.mojaloop.core.wallet.contract.exception.position.PositionLimitExceededException;
import io.mojaloop.core.wallet.intercom.client.api.ReservePosition;
import io.mojaloop.core.wallet.intercom.client.api.RollbackPosition;
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

    private final RollbackPosition rollbackPosition;

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
                                       RollbackPosition rollbackPosition,
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
        assert rollbackPosition != null;
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
        this.rollbackPosition = rollbackPosition;
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

        boolean reservedPosition = false;
        PositionUpdateId positionReservationId = null;

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

                throw new FspiopException(FspiopErrors.GENERIC_VALIDATION_ERROR, "The amount from ILP packet must be equal to the transfer amount.");
            }

            var expiration = postTransfersRequest.getExpiration();
            Instant expireAt = null;

            if (expiration != null) {

                expireAt = FspiopDates.fromRequestBody(expiration);

                if (expireAt.isBefore(Instant.now())) {

                    throw new FspiopException(FspiopErrors.TRANSFER_EXPIRED, "The transfer request from Payer FSP has expired. The expiration is : " + expiration);
                }

            }

            // The required validations are OK. We continue to do these steps:
            // - Open the transaction
            // - (Receive) Save the transfer in the database together with transactionId
            //      - Unwrap ILP Packet and get Party information.
            //      - Based on ILP packet's Parties information, prepare a Transfer object with/without Payer/Payee information.
            //      - Publish addStep message to Kafka.
            //      - Save the transfer in the database.
            //      - Publish addStep message to Kafka.
            // - Reserve the position of Payer.
            //      - Call Wallet's Increase Position API
            //      - Publish addStep message to Kafka.
            //      - Update the transfer with reserved positionId.
            //      - Publish addStep message to Kafka.
            // - Forward the request to Payee.
            // (Error)
            // - If something goes wrong, check whether the position has been reserved?
            //      - Yes, then rollback the position and send an error response to Payer.
            //      - No, then send an error response to Payer.

            // Now start opening the transaction.
            transaction = this.openTransaction.execute(new OpenTransactionCommand.Input(TransactionType.FUND_TRANSFER_RESERVE));
            LOGGER.info(
                "({}) Opened transaction successfully: transactionId : [{}], transactionAt : [{}]", udfTransferId.getId(), transaction.transactionId(),
                transaction.transactionAt());

            // Save the transfer in the database.
            this.receivedTransfer(payerFsp, payeeFsp, transaction.transactionId(), transaction.transactionAt(), udfTransferId, input, ilpPacket);

            // Reserve the position of Payer.
            positionReservationId = this.reservePayerPosition(payerFsp, payeeFsp, transaction.transactionId(), transaction.transactionAt(), udfTransferId, input);
            reservedPosition = true;
            LOGGER.info(
                "({}) Reserved position successfully for payer FSP: [{}], positionReservationId : [{}]", udfTransferId.getId(), payerFsp.fspCode().value(),
                positionReservationId.getId().toString());

            // Update the transfer in the database with reserved positionId.
            this.reservedTransfer(udfTransferId, transaction.transactionId(), positionReservationId);

            // Forward the request to Payee.
            var payeeBaseUrl = payeeFsp.endpoints().get(EndpointType.QUOTES).baseUrl();
            LOGGER.info("({}) Forwarding request to payee FSP (Url): [{}]", udfTransferId.getId(), payeeBaseUrl);

            this.addStepPublisher.publish(new AddStepCommand.Input(transaction.transactionId(), "post-transfers|before:forward-to-payee", new HashMap<>()));
            this.forwardRequest.forward(payeeBaseUrl, input.request());
            this.addStepPublisher.publish(new AddStepCommand.Input(transaction.transactionId(), "post-transfers|after:forward-to-payee", new HashMap<>()));

            LOGGER.info("({}) Done forwarding request to payee FSP (Url): [{}]", udfTransferId.getId(), payeeBaseUrl);

        } catch (Exception e) {

            LOGGER.error("({}) Exception occurred while executing PostTransfersCommandHandler: [{}]", udfTransferId.getId(), e.getMessage());

            if (payerFsp != null) {

                final var sendBackTo = new Payer(payerFspCode.value());
                final var baseUrl = payerFsp.endpoints().get(EndpointType.TRANSFERS).baseUrl();
                final var url = FspiopUrls.newUrl(baseUrl, "transfers/" + udfTransferId.getId() + "/error");

                try {

                    if (transaction != null) {

                        // Roll back the position if the position has been reserved.
                        if (reservedPosition) {

                            LOGGER.info(
                                "({}) Rolling back reserved position for payer FSP: [{}], positionReservationId : [{}]", udfTransferId.getId(), payerFsp.fspCode().value(),
                                positionReservationId.getId().toString());

                            this.rollbackPosition(udfTransferId, transaction.transactionId(), positionReservationId, new PositionUpdateId(Snowflake.get().nextId()));

                        }

                        // Then, send an error response to Payer.
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

                } catch (Throwable throwable) {
                    LOGGER.error("({}) Something went wrong while sending error response to payer FSP: ", udfTransferId.getId(), throwable);
                }
            }
        }

        LOGGER.info("({}) Returning from PostTransfersCommandHandler successfully.", udfTransferId.getId());

        return new Output();
    }

    private void receivedTransfer(FspData payerFsp,
                                  FspData payeeFsp,
                                  TransactionId transactionId,
                                  Instant transactionAt,
                                  UdfTransferId udfTransferId,
                                  Input input,
                                  InterledgerPreparePacket ilpPacket) {

        var unwrapper = this.partyUnwrapperRegistry.get(payeeFsp.fspCode());

        var payer = Party.empty();
        var payee = Party.empty();

        String payerPartyIdType = null;
        String payerPartyIdentifier = null;
        String payerPartySubIdOrType = null;

        String payeePartyIdType = null;
        String payeePartyIdentifier = null;
        String payeePartySubIdOrType = null;

        if (unwrapper == null) {

            LOGGER.warn("No unwrapper found for FSP: {}. Parties information will not be included in the transfer.", input.transfersPostRequest().getPayeeFsp());

        } else {

            var parties = unwrapper.unwrap(ilpPacket.getData());

            if (parties.payer().isPresent()) {

                var payerParty = parties.payer().get();

                payer = new Party(payerParty.getPartyIdType(), payerParty.getPartyIdentifier(), payerParty.getPartySubIdOrType());

                payerPartyIdType = payer.partyIdType().name();
                payerPartyIdentifier = payer.partyId();
                payerPartySubIdOrType = payer.subId();
            }

            if (parties.payee().isPresent()) {

                var payeeParty = parties.payee().get();

                payee = new Party(payeeParty.getPartyIdType(), payeeParty.getPartyIdentifier(), payeeParty.getPartySubIdOrType());

                payeePartyIdType = payee.partyIdType().name();
                payeePartyIdentifier = payee.partyId();
                payeePartySubIdOrType = payee.subId();
            }
        }

        var currency = input.transfersPostRequest().getAmount().getCurrency();
        var amount = new BigDecimal(input.transfersPostRequest().getAmount().getAmount());

        var params = new HashMap<String, String>();

        params.put("udfTransferId", udfTransferId.getId());

        params.put("payerFspCode", payerFsp.fspCode().value());
        params.put("payerPartyIdType", payerPartyIdType);
        params.put("payerPartyIdentifier", payerPartyIdentifier);
        params.put("payerPartySubIdOrType", payerPartySubIdOrType);

        params.put("payeeFspCode", payeeFsp.fspCode().value());
        params.put("payeePartyIdType", payeePartyIdType);
        params.put("payeePartyIdentifier", payeePartyIdentifier);
        params.put("payeePartySubIdOrType", payeePartySubIdOrType);

        params.put("currency", currency.name());
        params.put("amount", amount.stripTrailingZeros().toPlainString());
        params.put("expiration", input.transfersPostRequest().getExpiration());

        this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|before:received-transfer", params));

        var transfer = new Transfer(transactionId, transactionAt, udfTransferId, payerFsp.fspCode(), payer, payeeFsp.fspCode(), payee, currency, amount);

        transfer.addExtension(Direction.TO_PAYEE, "udfTransferId", udfTransferId.getId());

        transfer.addExtension(Direction.TO_PAYEE, "payerFspCode", payerFsp.fspCode().value());
        transfer.addExtension(Direction.TO_PAYEE, "payerPartyIdType", payerPartyIdType);
        transfer.addExtension(Direction.TO_PAYEE, "payerPartyIdentifier", payerPartyIdentifier);
        transfer.addExtension(Direction.TO_PAYEE, "payerPartySubIdOrType", payerPartySubIdOrType);

        transfer.addExtension(Direction.TO_PAYEE, "payeeFspCode", payeeFsp.fspCode().value());
        transfer.addExtension(Direction.TO_PAYEE, "payeePartyIdType", payeePartyIdType);
        transfer.addExtension(Direction.TO_PAYEE, "payeePartyIdentifier", payeePartyIdentifier);
        transfer.addExtension(Direction.TO_PAYEE, "payeePartySubIdOrType", payeePartySubIdOrType);

        transfer.addExtension(Direction.TO_PAYEE, "currency", currency.name());
        transfer.addExtension(Direction.TO_PAYEE, "amount", amount.stripTrailingZeros().toPlainString());
        transfer.addExtension(Direction.TO_PAYEE, "expiration", input.transfersPostRequest().getExpiration());
        transfer.addExtension(Direction.TO_PAYEE, "transferState", transfer.getState().name());

        TransactionContext.startNew(this.transactionManager, transfer.getId().toString());
        this.transferRepository.save(transfer);
        TransactionContext.commit();

        params.clear();
        params.put("transferId", transfer.getId().toString());
        params.put("transferState", transfer.getState().name());

        this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|after:received-transfer", params));

        LOGGER.info("({}) Saved transfer successfully: {}", udfTransferId.getId(), transfer);
    }

    private PositionUpdateId reservePayerPosition(FspData payerFsp, FspData payeeFsp, TransactionId transactionId, Instant transactionAt, UdfTransferId udfTransferId, Input input)
        throws FspiopException {

        var payerId = payerFsp.fspId();
        var currency = input.transfersPostRequest().getAmount().getCurrency();
        var transferAmount = new BigDecimal(input.transfersPostRequest().getAmount().getAmount());

        var payerPositionId = this.positionStore.get(new WalletOwnerId(payerId.getId()), currency).positionId();

        try {

            LOGGER.info(
                "({}) Reserving position for payer: [{}] , currency : [{}], amount : [{}]", udfTransferId.getId(), payerPositionId, currency,
                transferAmount.stripTrailingZeros().toPlainString());

            var description = "Transfer from " + payerFsp.fspCode().value() + " to " + payeeFsp.fspCode().value() + " for " + currency + " " +
                                  transferAmount.stripTrailingZeros().toPlainString();

            var params = new HashMap<String, String>();

            params.put("udfTransferId", udfTransferId.getId());
            params.put("payerFspCode", payerFsp.fspCode().value());
            params.put("payeeFspCode", payeeFsp.fspCode().value());
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

            return output.positionUpdateId();

        } catch (WalletIntercomClientException e) {

            throw this.resolveWalletIntercomException(udfTransferId, e);
        }

    }

    private void reservedTransfer(UdfTransferId udfTransferId, TransactionId transactionId, PositionUpdateId positionReservationId) throws FspiopException {

        TransactionContext.startNew(this.transactionManager, transactionId.toString());

        var transfer = this.transferRepository.findOne(TransferRepository.Filters.withTransactionId(transactionId))
                                              .orElseThrow(() -> new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, "Unable to load Transfer data in Hub."));

        transfer.reserved(positionReservationId);

        this.transferRepository.save(transfer);

        TransactionContext.commit();

        LOGGER.info("({}) Reserved transfer successfully: transferId : [{}]", udfTransferId.getId(), transfer.getId().toString());
    }

    private FspiopException resolveWalletIntercomException(UdfTransferId udfTransferId, WalletIntercomClientException e) {

        if (PositionLimitExceededException.CODE.equals(e.getCode())) {

            LOGGER.error("({}) Wallet intercom error occurred: {}", udfTransferId.getId(), e.getMessage());
            return new FspiopException(FspiopErrors.PAYER_LIMIT_ERROR, "Payer position limit reached.");
        }

        LOGGER.error("({}) Wallet intercom error occurred: {}", udfTransferId.getId(), e.getMessage());
        return new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, "Unknown error occurred.");
    }

    private void rollbackPosition(UdfTransferId udfTransferId, TransactionId transactionId, PositionUpdateId positionReservationId, PositionUpdateId positionRollbackId)
        throws FspiopException {

        try {

            var params = new HashMap<String, String>();

            params.put("positionReservationId", positionReservationId.getId().toString());
            params.put("positionRollbackId", positionRollbackId.getId().toString());

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|before:rollback-position", params));

            var output = this.rollbackPosition.execute(new RollbackPositionCommand.Input(positionReservationId, positionRollbackId, null));

            params.clear();
            params.put("positionUpdatedId", output.positionUpdateId().getId().toString());
            params.put("positionId", output.positionId().getId().toString());
            params.put("oldPosition", output.oldPosition().stripTrailingZeros().toPlainString());
            params.put("newPosition", output.newPosition().stripTrailingZeros().toPlainString());
            params.put("oldReserved", output.oldReserved().stripTrailingZeros().toPlainString());
            params.put("newReserved", output.newReserved().stripTrailingZeros().toPlainString());

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|after:rollback-position", params));

            LOGGER.info("({}) Rolled back position successfully. positionReservationId : [{}]", udfTransferId.getId(), positionReservationId.getId());

        } catch (WalletIntercomClientException e) {

            throw this.resolveWalletIntercomException(udfTransferId, e);
        }

    }

}
