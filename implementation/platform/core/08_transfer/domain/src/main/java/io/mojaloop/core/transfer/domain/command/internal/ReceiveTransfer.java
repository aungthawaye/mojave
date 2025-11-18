package io.mojaloop.core.transfer.domain.command.internal;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.core.common.datatype.enums.Direction;
import io.mojaloop.core.common.datatype.enums.trasaction.StepPhase;
import io.mojaloop.core.common.datatype.enums.trasaction.TransactionType;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.transfer.TransferId;
import io.mojaloop.core.common.datatype.identifier.transfer.UdfTransferId;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.transaction.contract.command.AddStepCommand;
import io.mojaloop.core.transaction.contract.command.CloseTransactionCommand;
import io.mojaloop.core.transaction.contract.command.OpenTransactionCommand;
import io.mojaloop.core.transaction.intercom.client.api.CloseTransaction;
import io.mojaloop.core.transaction.intercom.client.api.OpenTransaction;
import io.mojaloop.core.transaction.intercom.client.exception.TransactionIntercomClientException;
import io.mojaloop.core.transaction.producer.publisher.AddStepPublisher;
import io.mojaloop.core.transfer.TransferDomainConfiguration;
import io.mojaloop.core.transfer.domain.model.Party;
import io.mojaloop.core.transfer.domain.model.Transfer;
import io.mojaloop.core.transfer.domain.repository.TransferRepository;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.spec.core.Currency;
import io.mojaloop.fspiop.spec.core.PartyIdInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;

@Service
public class ReceiveTransfer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiveTransfer.class);

    private final OpenTransaction openTransaction;

    private final CloseTransaction closeTransaction;

    private final TransferDomainConfiguration.TransferSettings transferSettings;

    private final AddStepPublisher addStepPublisher;

    private final TransferRepository transferRepository;

    public ReceiveTransfer(OpenTransaction openTransaction,
                           CloseTransaction closeTransaction,
                           TransferDomainConfiguration.TransferSettings transferSettings,
                           AddStepPublisher addStepPublisher,
                           TransferRepository transferRepository) {

        assert openTransaction != null;
        assert closeTransaction != null;
        assert transferSettings != null;
        assert addStepPublisher != null;
        assert transferRepository != null;

        this.openTransaction = openTransaction;
        this.closeTransaction = closeTransaction;
        this.transferSettings = transferSettings;
        this.addStepPublisher = addStepPublisher;
        this.transferRepository = transferRepository;
    }

    @Transactional
    @Write
    public Output execute(Input input) throws FspiopException {

        LOGGER.info("Receiving transfer request : udfTransferId : [{}]", input.udfTransferId.getId());

        TransactionId transactionId = null;
        Instant transactionAt = null;

        try {

            var payerFsp = input.payerFsp();
            var payeeFsp = input.payeeFsp();

            var payerPartyIdInfo = input.payerPartyIdInfo();
            var payeePartyIdInfo = input.payeePartyIdInfo();

            var openTransactionOutput = this.openTransaction.execute(new OpenTransactionCommand.Input(TransactionType.FUND_TRANSFER));

            transactionId = openTransactionOutput.transactionId();
            var transactionIdString = transactionId.getId().toString();

            transactionAt = openTransactionOutput.transactionAt();
            var transactionAtString = transactionAt.getEpochSecond() + "";

            var transferAmountString = input.transferAmount().stripTrailingZeros().toPlainString();
            var reservationTimeoutAt = Instant.now().plusMillis(this.transferSettings.reservationTimeoutMs());

            var before = new HashMap<String, String>();
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

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|receive-transfer", before, StepPhase.BEFORE));

            before.clear();

            var transfer = new Transfer(transactionId, transactionAt, input.udfTransferId, payerFsp.fspCode(),
                new Party(payerPartyIdInfo.getPartyIdType(), payerPartyIdInfo.getPartyIdentifier(), payerPartyIdInfo.getPartySubIdOrType()), payeeFsp.fspCode(),
                new Party(payeePartyIdInfo.getPartyIdType(), payeePartyIdInfo.getPartyIdentifier(), payeePartyIdInfo.getPartySubIdOrType()), input.currency, input.transferAmount,
                input.requestExpiration, reservationTimeoutAt);

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

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|receive-transfer", after, StepPhase.AFTER));

            after.clear();

            LOGGER.info("Received transfer successfully: udfTransferId : [{}], transferId : [{}]", input.udfTransferId.getId(), transfer.getId().toString());

            return new Output(transactionId, transactionAt, transfer.getId());

        } catch (Exception e) {

            LOGGER.error("Failed to receive transfer: {}", e.getMessage());

            if (transactionId != null) {

                LOGGER.error("Close transaction with ERROR : transactionId : [{}]", transactionId.getId());

                try {
                    this.closeTransaction.execute(new CloseTransactionCommand.Input(transactionId, e.getMessage()));
                } catch (TransactionIntercomClientException ignored) {
                    LOGGER.error("Failed to close transaction with ERROR : transactionId : [{}]", transactionId.getId());
                }
            }

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, e.getMessage());
        }

    }

    public record Input(UdfTransferId udfTransferId,
                        FspData payerFsp,
                        FspData payeeFsp,
                        PartyIdInfo payerPartyIdInfo,
                        PartyIdInfo payeePartyIdInfo,
                        Currency currency,
                        BigDecimal transferAmount,
                        Instant requestExpiration) { }

    public record Output(TransactionId transactionId, Instant transactionAt, TransferId transferId) { }

}
