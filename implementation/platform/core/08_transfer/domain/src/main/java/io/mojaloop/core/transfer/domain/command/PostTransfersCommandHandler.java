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
import io.mojaloop.core.common.datatype.enums.trasaction.StepPhase;
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
import io.mojaloop.fspiop.spec.core.Currency;
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
        Throwable lastThrowable = null;

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
            Instant requestExpiration = null;

            if (expiration != null) {

                requestExpiration = FspiopDates.fromRequestBody(expiration);

                if (requestExpiration.isBefore(Instant.now())) {

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
            this.saveTransferForReceiving(
                payerFsp, payeeFsp, transaction.transactionId(), transaction.transactionAt(), udfTransferId, currency, transferAmount, requestExpiration, ilpPacket);

            // Reserve the position of Payer.
            positionReservationId = this.reservePayerPosition(payerFsp, payeeFsp, transaction.transactionId(), transaction.transactionAt(), udfTransferId, input);
            reservedPosition = true;

            // Update the transfer in the database with reserved positionId.
            this.saveTransferForReservation(udfTransferId, transaction.transactionId(), positionReservationId);

            // Forward the request to Payee.
            var payeeBaseUrl = payeeFsp.endpoints().get(EndpointType.QUOTES).baseUrl();
            LOGGER.info("({}) Forwarding request to payee FSP (Url): [{}]", udfTransferId.getId(), payeeBaseUrl);

            this.addStepPublisher.publish(new AddStepCommand.Input(transaction.transactionId(), "post-transfers|forward-to-payee", Map.of("url", payeeBaseUrl), StepPhase.BEFORE));
            this.forwardRequest.forward(payeeBaseUrl, input.request());
            this.addStepPublisher.publish(new AddStepCommand.Input(transaction.transactionId(), "post-transfers|forward-to-payee", Map.of("url", payeeBaseUrl), StepPhase.AFTER));

            LOGGER.info("({}) Done forwarding request to payee FSP (Url): [{}]", udfTransferId.getId(), payeeBaseUrl);

        } catch (Exception e) {

            LOGGER.error("({}) Exception occurred while executing PostTransfersCommandHandler: [{}]", udfTransferId.getId(), e.getMessage());

            lastThrowable = e;

            if (payerFsp != null) {

                final var sendBackTo = new Payer(payerFspCode.value());
                final var baseUrl = payerFsp.endpoints().get(EndpointType.TRANSFERS).baseUrl();
                final var url = FspiopUrls.newUrl(baseUrl, "transfers/" + udfTransferId.getId() + "/error");

                try {

                    if (transaction != null) {

                        // Roll back the position if the position has been reserved.
                        if (reservedPosition) {

                            this.rollbackPosition(udfTransferId, transaction.transactionId(), positionReservationId, new PositionUpdateId(Snowflake.get().nextId()));

                        }

                        // Then, send an error response to Payer.
                        this.addStepPublisher.publish(
                            new AddStepCommand.Input(transaction.transactionId(), "post-transfers|send-error-to-payer", Map.of("error", e.getMessage()), StepPhase.BEFORE));

                        FspiopErrorResponder.toPayer(new Payer(payerFspCode.value()), e, (payer, error) -> this.respondTransfers.putTransfersError(sendBackTo, url, error));

                        this.addStepPublisher.publish(
                            new AddStepCommand.Input(transaction.transactionId(), "post-transfers|send-error-to-payer", Map.of("error", e.getMessage()), StepPhase.AFTER));

                    } else {

                        LOGGER.warn("({}) Transaction was not opened. Skipping adding steps to transaction.", udfTransferId.getId());
                        FspiopErrorResponder.toPayer(new Payer(payerFspCode.value()), e, (payer, error) -> this.respondTransfers.putTransfersError(sendBackTo, url, error));
                    }

                } catch (Throwable throwable) {

                    lastThrowable = throwable;

                    LOGGER.error("({}) Something went wrong while sending error response to payer FSP: ", udfTransferId.getId(), throwable);
                }

            }

        } finally {

            if (transaction != null) {

                if (lastThrowable != null) {

                    LOGGER.error(
                        "({}) Closing transaction failed: transactionId : [{}], transactionAt : [{}], error : [{}]", udfTransferId.getId(), transaction.transactionId(),
                        transaction.transactionAt(), lastThrowable.getMessage());

                    this.closeTransactionPublisher.publish(new CloseTransactionCommand.Input(transaction.transactionId(), lastThrowable.getMessage()));

                } else {

                    LOGGER.info(
                        "({}) Closing transaction successfully: transactionId : [{}], transactionAt : [{}]", udfTransferId.getId(), transaction.transactionId(),
                        transaction.transactionAt());

                    this.closeTransactionPublisher.publish(new CloseTransactionCommand.Input(transaction.transactionId(), null));

                }
            }
        }

        LOGGER.info("({}) Returning from PostTransfersCommandHandler successfully.", udfTransferId.getId());

        return new Output();
    }

    private PositionUpdateId reservePayerPosition(FspData payerFsp, FspData payeeFsp, TransactionId transactionId, Instant transactionAt, UdfTransferId udfTransferId, Input input)
        throws FspiopException {

        try {

            var payerId = payerFsp.fspId();
            var currency = input.transfersPostRequest().getAmount().getCurrency();
            var transferAmount = new BigDecimal(input.transfersPostRequest().getAmount().getAmount());

            LOGGER.info("({}) Reserving position for payer FSP: [{}], currency: [{}], amount: [{}]", udfTransferId.getId(), payerFsp.fspCode().value(), currency, transferAmount);

            var payerPositionId = this.positionStore.get(new WalletOwnerId(payerId.getId()), currency).positionId();

            LOGGER.info(
                "({}) Reserving position for payer: [{}] , currency : [{}], amount : [{}]", udfTransferId.getId(), payerPositionId.getId(), currency,
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

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|reserve-position", params, StepPhase.BEFORE));

            var output = this.reservePosition.execute(new ReservePositionCommand.Input(payerPositionId, transferAmount, transactionId, transactionAt, description));

            params.clear();

            params.put("udfTransferId", udfTransferId.getId());
            params.put("positionUpdateId", output.positionUpdateId().getId().toString());
            params.put("oldPosition", output.oldPosition().stripTrailingZeros().toPlainString());
            params.put("newPosition", output.newPosition().stripTrailingZeros().toPlainString());
            params.put("oldReserved", output.oldReserved().stripTrailingZeros().toPlainString());
            params.put("newReserved", output.newReserved().stripTrailingZeros().toPlainString());

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|reserve-position", params, StepPhase.AFTER));

            LOGGER.info(
                "({}) Reserved position successfully for payer FSP: [{}], positionReservationId : [{}]", udfTransferId.getId(), payerFsp.fspCode().value(),
                output.positionUpdateId().getId().toString());

            return output.positionUpdateId();

        } catch (WalletIntercomClientException e) {

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|reserve-position", Map.of("error", e.getMessage()), StepPhase.ERROR));

            throw this.resolveWalletIntercomException(udfTransferId, e);

        } catch (Exception e) {

            LOGGER.error("({}) Failed to reserve position for payer FSP: [{}] , error : [{}]", udfTransferId.getId(), payerFsp.fspCode().value(), e);

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|reserve-position", Map.of("error", e.getMessage()), StepPhase.ERROR));

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR);
        }

    }

    private FspiopException resolveWalletIntercomException(UdfTransferId udfTransferId, WalletIntercomClientException e) {

        if (PositionLimitExceededException.CODE.equals(e.getCode())) {

            LOGGER.error("({}) Wallet intercom error occurred: {}", udfTransferId.getId(), e.getMessage());
            return new FspiopException(FspiopErrors.PAYER_LIMIT_ERROR, "Payer position limit reached to NDC.");
        }

        LOGGER.error("({}) Wallet intercom error occurred: {}", udfTransferId.getId(), e.getMessage());
        return new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR);
    }

    private void rollbackPosition(UdfTransferId udfTransferId, TransactionId transactionId, PositionUpdateId positionReservationId, PositionUpdateId positionRollbackId)
        throws FspiopException {

        try {

            LOGGER.info("({}) Rolling back reserved position : positionReservationId : [{}]", udfTransferId.getId(), positionReservationId.getId().toString());

            var params = new HashMap<String, String>();

            params.put("positionReservationId", positionReservationId.getId().toString());
            params.put("positionRollbackId", positionRollbackId.getId().toString());

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|rollback-position", params, StepPhase.BEFORE));

            var output = this.rollbackPosition.execute(new RollbackPositionCommand.Input(positionReservationId, positionRollbackId, null));

            params.clear();
            params.put("positionUpdatedId", output.positionUpdateId().getId().toString());
            params.put("positionId", output.positionId().getId().toString());
            params.put("oldPosition", output.oldPosition().stripTrailingZeros().toPlainString());
            params.put("newPosition", output.newPosition().stripTrailingZeros().toPlainString());
            params.put("oldReserved", output.oldReserved().stripTrailingZeros().toPlainString());
            params.put("newReserved", output.newReserved().stripTrailingZeros().toPlainString());

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|rollback-position", params, StepPhase.AFTER));

            LOGGER.info("({}) Rolled back position successfully. positionReservationId : [{}]", udfTransferId.getId(), positionReservationId.getId());

        } catch (WalletIntercomClientException e) {

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|rollback-position", Map.of("error", e.getMessage()), StepPhase.ERROR));

            throw this.resolveWalletIntercomException(udfTransferId, e);

        } catch (Exception e) {

            LOGGER.error("({}) Failed to rollback position for reservationId: [{}] : error [{}]", udfTransferId.getId(), positionReservationId.getId(), e);

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|rollback-position", Map.of("error", e.getMessage()), StepPhase.ERROR));

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR);
        }

    }

    private void saveTransferForReceiving(FspData payerFsp,
                                          FspData payeeFsp,
                                          TransactionId transactionId,
                                          Instant transactionAt,
                                          UdfTransferId udfTransferId,
                                          Currency currency,
                                          BigDecimal amount,
                                          Instant requestExpiration,
                                          InterledgerPreparePacket ilpPacket) throws FspiopException {

        try {

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

                LOGGER.warn("No unwrapper found for FSP: [{}]. Parties information will not be included in the transfer.", payeeFsp.fspCode().value());

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
            params.put("expiration", requestExpiration.getEpochSecond() + "");

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|received-transfer", params, StepPhase.BEFORE));

            var transfer = new Transfer(transactionId, transactionAt, udfTransferId, payerFsp.fspCode(), payer, payeeFsp.fspCode(), payee, currency, amount, requestExpiration);

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
            transfer.addExtension(Direction.TO_PAYEE, "expiration", requestExpiration.getEpochSecond() + "");
            transfer.addExtension(Direction.TO_PAYEE, "transferState", transfer.getState().name());

            TransactionContext.startNew(this.transactionManager, transfer.getId().toString());
            this.transferRepository.save(transfer);
            TransactionContext.commit();

            params.clear();
            params.put("transferId", transfer.getId().toString());
            params.put("transferState", transfer.getState().name());

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|received-transfer", params, StepPhase.AFTER));

            LOGGER.info("({}) Received transfer successfully: transferId : [{}]", udfTransferId.getId(), transfer.getId().toString());

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            LOGGER.error("({}) Failed to receive transfer: [{}]", udfTransferId.getId(), e.getMessage());

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|received-transfer", Map.of("error", e.getMessage()), StepPhase.ERROR));

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR);
        }
    }

    private void saveTransferForReservation(UdfTransferId udfTransferId, TransactionId transactionId, PositionUpdateId positionReservationId) throws FspiopException {

        try {

            TransactionContext.startNew(this.transactionManager, transactionId.toString());

            var params = new HashMap<String, String>();

            params.put("transactionId", transactionId.getId().toString());
            params.put("reservationId", positionReservationId.getId().toString());

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|reserved-transfer", params, StepPhase.BEFORE));

            var transfer = this.transferRepository.findOne(TransferRepository.Filters.withTransactionId(transactionId)).orElseThrow(
                () -> new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, "Unable to load Transfer using transactionId (" + transactionId.getId() + ") in Hub."));

            transfer.reserved(positionReservationId);

            this.transferRepository.save(transfer);

            TransactionContext.commit();

            params.clear();
            params.put("reservedAt", transfer.getReservedAt().getEpochSecond() + "");

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|reserved-transfer", params, StepPhase.AFTER));

            LOGGER.info("({}) Reserved transfer successfully: transferId : [{}]", udfTransferId.getId(), transfer.getId().toString());

        } catch (Exception e) {

            LOGGER.error("({}) Failed to reserved transfer: {}", udfTransferId.getId(), e.getMessage());

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|reserved-transfer", Map.of("error", e.getMessage()), StepPhase.ERROR));

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR);
        }
    }

}
