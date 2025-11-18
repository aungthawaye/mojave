package io.mojaloop.core.transfer.domain.command.internal;

import io.mojaloop.component.jpa.transaction.TransactionContext;
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.common.datatype.enums.Direction;
import io.mojaloop.core.common.datatype.enums.trasaction.StepPhase;
import io.mojaloop.core.common.datatype.enums.trasaction.TransactionType;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.transfer.UdfTransferId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.transaction.contract.command.AddStepCommand;
import io.mojaloop.core.transaction.contract.command.OpenTransactionCommand;
import io.mojaloop.core.transaction.intercom.client.api.OpenTransaction;
import io.mojaloop.core.transaction.producer.publisher.AddStepPublisher;
import io.mojaloop.core.transfer.TransferDomainConfiguration;
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
import io.mojaloop.fspiop.component.handy.FspiopCurrencies;
import io.mojaloop.fspiop.component.handy.FspiopDates;
import io.mojaloop.fspiop.component.interledger.Interledger;
import io.mojaloop.fspiop.spec.core.Currency;
import io.mojaloop.fspiop.spec.core.TransfersPostRequest;
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
public class ReserveTransfer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReserveTransfer.class);

    private final PositionStore positionStore;

    private final OpenTransaction openTransaction;

    private final ReservePosition reservePosition;

    private final RollbackPosition rollbackPosition;

    private final AddStepPublisher addStepPublisher;

    private final PartyUnwrapperRegistry partyUnwrapperRegistry;

    private final TransferDomainConfiguration.TransferSettings transferSettings;

    private final TransferRepository transferRepository;

    private final PlatformTransactionManager transactionManager;

    public ReserveTransfer(PositionStore positionStore,
                           OpenTransaction openTransaction,
                           ReservePosition reservePosition,
                           RollbackPosition rollbackPosition,
                           AddStepPublisher addStepPublisher,
                           PartyUnwrapperRegistry partyUnwrapperRegistry,
                           TransferDomainConfiguration.TransferSettings transferSettings,
                           TransferRepository transferRepository,
                           PlatformTransactionManager transactionManager) {

        assert positionStore != null;
        assert openTransaction != null;
        assert reservePosition != null;
        assert rollbackPosition != null;
        assert addStepPublisher != null;
        assert partyUnwrapperRegistry != null;
        assert transferSettings != null;
        assert transferRepository != null;
        assert transactionManager != null;

        this.positionStore = positionStore;
        this.openTransaction = openTransaction;
        this.reservePosition = reservePosition;
        this.rollbackPosition = rollbackPosition;
        this.addStepPublisher = addStepPublisher;
        this.partyUnwrapperRegistry = partyUnwrapperRegistry;
        this.transferSettings = transferSettings;
        this.transferRepository = transferRepository;
        this.transactionManager = transactionManager;
    }

    public Result reserve(UdfTransferId udfTransferId, FspData payerFsp, FspData payeeFsp, TransfersPostRequest request) throws Exception {

        TransactionId transactionId = null;
        Instant transactionAt = null;

        PositionUpdateId positionReservationId = null;
        boolean reservedPosition = false;

        var payerFspInRequest = request.getPayerFsp();
        var payeeFspInRequest = request.getPayeeFsp();
        var payerFspCode = payerFsp.fspCode();
        var payeeFspCode = payeeFsp.fspCode();

        try {

            // Make sure the payer/payee information from the request body and request header are the same.
            if (!payerFspInRequest.equals(payerFspCode.value()) || !payeeFspInRequest.equals(payeeFspCode.value())) {

                throw new FspiopException(FspiopErrors.GENERIC_VALIDATION_ERROR, "FSPs information in the request body and request header must be the same.");
            }

            var currency = request.getAmount().getCurrency();
            var transferAmount = new BigDecimal(request.getAmount().getAmount());

            var ilpPacket = Interledger.unwrap(request.getIlpPacket());
            var ilpTransferAmount = Interledger.Amount.deserialize(ilpPacket.getAmount(), FspiopCurrencies.get(currency).scale());

            if (ilpTransferAmount.subtract(transferAmount).signum() != 0) {

                throw new FspiopException(FspiopErrors.GENERIC_VALIDATION_ERROR, "The amount from ILP packet must be equal to the transfer amount.");
            }

            var expiration = request.getExpiration();
            Instant requestExpiration = null;

            if (expiration != null) {

                requestExpiration = FspiopDates.fromRequestBody(expiration);

                if (requestExpiration.isBefore(Instant.now())) {

                    throw new FspiopException(FspiopErrors.TRANSFER_EXPIRED, "The transfer request from Payer FSP has expired. The expiration is : " + expiration);
                }

            }

            // Now start opening the transaction.
            var output = this.openTransaction.execute(new OpenTransactionCommand.Input(TransactionType.FUND_TRANSFER));

            transactionId = output.transactionId();
            transactionAt = output.transactionAt();

            LOGGER.info("Opened transaction successfully: transactionId : [{}], transactionAt : [{}]", transactionId, transactionAt);

            // Save the transfer in the database.
            this.saveTransferForReceiving(payerFsp, payeeFsp, transactionId, transactionAt, udfTransferId, currency, transferAmount, requestExpiration, ilpPacket);

            // Reserve the position of Payer.
            positionReservationId = this.reservePayerPosition(udfTransferId, payerFsp, payeeFsp, transactionId, transactionAt, currency, transferAmount);
            reservedPosition = true;

            // Update the transfer in the database with reserved positionId.
            this.saveTransferForReservation(udfTransferId, transactionId, positionReservationId);

        } catch (Exception e) {

            if (reservedPosition) {

                LOGGER.error("Failed to reserve position for payer FSP: [{}], udfTransferId : [{}]", payerFspCode, udfTransferId.getId(), e);

                this.rollbackReservation(udfTransferId, transactionId, positionReservationId, new PositionUpdateId(Snowflake.get().nextId()));
            }

            throw e;
        }

        return new Result(transactionId, transactionAt, positionReservationId);

    }

    public void rollbackReservation(UdfTransferId udfTransferId, TransactionId transactionId, PositionUpdateId positionReservationId, PositionUpdateId positionRollbackId)
        throws FspiopException {

        try {

            LOGGER.info("Rolling back reserved position : positionReservationId : [{}]", positionReservationId.getId().toString());

            var before = new HashMap<String, String>();

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|rollback-position", before, StepPhase.BEFORE));

            var output = this.rollbackPosition.execute(new RollbackPositionCommand.Input(positionReservationId, null));

            var after = Map.of("positionId", output.positionId().getId().toString(), "positionRollbackId", output.positionUpdateId().getId().toString(), "oldPosition",
                output.oldPosition().stripTrailingZeros().toPlainString(), "newPosition", output.newPosition().stripTrailingZeros().toPlainString(), "oldReserved",
                output.oldReserved().stripTrailingZeros().toPlainString(), "newReserved", output.newReserved().stripTrailingZeros().toPlainString(), "amount",
                output.amount().stripTrailingZeros().toPlainString());

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|rollback-position", after, StepPhase.AFTER));

            LOGGER.info("Rolled back position successfully. positionReservationId : [{}]", positionReservationId.getId());

        } catch (WalletIntercomClientException e) {

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|rollback-position", Map.of("error", e.getMessage()), StepPhase.ERROR));

            throw this.resolveWalletIntercomException(udfTransferId, e);

        } catch (Exception e) {

            LOGGER.error("Failed to rollback position for reservationId: [{}] : error [{}]", positionReservationId.getId(), e);

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|rollback-position", Map.of("error", e.getMessage()), StepPhase.ERROR));

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR);
        }

    }

    private PositionUpdateId reservePayerPosition(UdfTransferId udfTransferId,
                                                  FspData payerFsp,
                                                  FspData payeeFsp,
                                                  TransactionId transactionId,
                                                  Instant transactionAt,
                                                  Currency currency,
                                                  BigDecimal transferAmount) throws FspiopException {

        try {

            var payerFspCode = payerFsp.fspCode().value();
            var payeeFspCode = payeeFsp.fspCode().value();
            var payerId = payerFsp.fspId();

            LOGGER.info("Reserving position for payer FSP: [{}], currency: [{}], amount: [{}]", payerFspCode, currency, transferAmount);

            var payerPositionId = this.positionStore.get(new WalletOwnerId(payerId.getId()), currency).positionId();

            LOGGER.info("Reserving position for payer: payerPositionId : [{}] , currency : [{}], amount : [{}]", payerPositionId.getId(), currency,
                transferAmount.stripTrailingZeros().toPlainString());

            var description = "Transfer from " + payerFspCode + " to " + payeeFspCode + " for " + currency + " " + transferAmount.stripTrailingZeros().toPlainString();

            var before = Map.of("udfTransferId", udfTransferId.getId(), "payerFspCode", payerFspCode, "payeeFspCode", payeeFspCode, "currency", currency.name(), "amount",
                transferAmount.stripTrailingZeros().toPlainString(), "positionId", payerPositionId.getId().toString());

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|reserve-position", before, StepPhase.BEFORE));

            var output = this.reservePosition.execute(new ReservePositionCommand.Input(payerPositionId, transferAmount, transactionId, transactionAt, description));

            var after = Map.of("udfTransferId", udfTransferId.getId(), "positionReservationId", output.positionUpdateId().getId().toString(), "oldPosition",
                output.oldPosition().stripTrailingZeros().toPlainString(), "newPosition", output.newPosition().stripTrailingZeros().toPlainString(), "oldReserved",
                output.oldReserved().stripTrailingZeros().toPlainString(), "newReserved", output.newReserved().stripTrailingZeros().toPlainString());

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|reserve-position", after, StepPhase.AFTER));

            LOGGER.info("Reserved position successfully for payer FSP: [{}], positionReservationId : [{}]", payerFspCode, output.positionUpdateId().getId().toString());

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

            var transfer = new Transfer(transactionId, transactionAt, udfTransferId, payerFsp.fspCode(), payer, payeeFsp.fspCode(), payee, currency, amount, requestExpiration,
                Instant.now().plusMillis(this.transferSettings.reservationTimeoutMs()));

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

            var transfer = this.transferRepository.findOne(TransferRepository.Filters.withTransactionId(transactionId))
                                                  .orElseThrow(() -> new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR,
                                                      "Unable to load Transfer using transactionId (" + transactionId.getId() + ") in Hub."));

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

    public record Result(TransactionId transactionId, Instant transactionAt, PositionUpdateId positionReservationId) { }

}
