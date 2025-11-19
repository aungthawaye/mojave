package io.mojaloop.core.transfer.domain.command.internal;

import io.mojaloop.core.common.datatype.enums.trasaction.StepPhase;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.transaction.contract.command.AddStepCommand;
import io.mojaloop.core.transaction.producer.publisher.AddStepPublisher;
import io.mojaloop.core.wallet.contract.command.position.ReservePositionCommand;
import io.mojaloop.core.wallet.contract.exception.position.PositionLimitExceededException;
import io.mojaloop.core.wallet.intercom.client.exception.WalletIntercomClientException;
import io.mojaloop.core.wallet.store.PositionStore;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.spec.core.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;

@Service
public class ReservePayerPosition {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservePayerPosition.class);

    private final PositionStore positionStore;

    private final ReservePosition reservePosition;

    private final AddStepPublisher addStepPublisher;

    public ReservePayerPosition(PositionStore positionStore, ReservePosition reservePosition, AddStepPublisher addStepPublisher) {

        assert positionStore != null;
        assert reservePosition != null;
        assert addStepPublisher != null;

        this.positionStore = positionStore;
        this.reservePosition = reservePosition;
        this.addStepPublisher = addStepPublisher;
    }

    public void execute(Input input) throws FspiopException {

        try {

            LOGGER.info("Reserving position for payer FSP: [{}], currency: [{}], amount: [{}]", input.payerFsp.fspId().getId(), input.currency(), input.transferAmount());

            var payerFsp = input.payerFsp;
            var payerFspCode = payerFsp.fspCode();

            var payeeFsp = input.payeeFsp;
            var payeeFspCode = payeeFsp.fspCode();

            var currency = input.currency();

            var transferAmount = input.transferAmount();
            var transferAmountString = transferAmount.stripTrailingZeros().toPlainString();

            var transactionId = input.transactionId();
            var transactionIdString = transactionId.getId().toString();

            var transactionAt = input.transactionAt();
            var transactionAtString = transactionAt.getEpochSecond() + "";

            var payerPositionId = this.positionStore.get(new WalletOwnerId(payerFsp.fspId().getId()), currency).positionId();
            var payerPositionIdString = payerPositionId.getId().toString();
            var description = "Transfer " + currency + " " + transferAmountString + " from " + payerFspCode.value() + " to " + payeeFspCode.value();

            var before = new HashMap<String, String>();
            var after = new HashMap<String, String>();

            before.put("positionId", payerPositionIdString);
            before.put("transferAmount", transferAmountString);
            before.put("transactionId", transactionIdString);
            before.put("transactionAt", transactionAtString);
            before.put("description", description);

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|reserve-position", before, StepPhase.BEFORE));

            before.clear();

            var reservePositionOutput = this.reservePosition.execute(new ReservePositionCommand.Input(payerPositionId, transferAmount, transactionId, transactionAt, description));

            after.put("positionUpdateId", reservePositionOutput.positionUpdateId().getId().toString());
            after.put("oldPosition", reservePositionOutput.oldPosition().stripTrailingZeros().toPlainString());
            after.put("newPosition", reservePositionOutput.newPosition().stripTrailingZeros().toPlainString());
            after.put("oldReserved", reservePositionOutput.oldReserved().stripTrailingZeros().toPlainString());
            after.put("newReserved", reservePositionOutput.newReserved().stripTrailingZeros().toPlainString());

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, "post-transfers|reserve-position", after, StepPhase.AFTER));

            after.clear();

            LOGGER.info("Position reserved successfully: positionUpdateId : [{}]", reservePositionOutput.positionUpdateId().getId().toString());

        } catch (WalletIntercomClientException e) {

            if (PositionLimitExceededException.CODE.equals(e.getCode())) {

                LOGGER.error("Payer position limit reached to NDC.");
                throw new FspiopException(FspiopErrors.PAYER_LIMIT_ERROR, "Payer position limit reached to NDC. " + e.getMessage());
            }
        }

    }

    public record Input(FspData payerFsp, FspData payeeFsp, Currency currency, BigDecimal transferAmount, TransactionId transactionId, Instant transactionAt) { }

}
