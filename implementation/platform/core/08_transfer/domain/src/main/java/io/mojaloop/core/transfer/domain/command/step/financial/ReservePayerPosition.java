package io.mojaloop.core.transfer.domain.command.step.financial;

import io.mojaloop.core.common.datatype.enums.trasaction.StepPhase;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.transaction.contract.command.AddStepCommand;
import io.mojaloop.core.transaction.producer.publisher.AddStepPublisher;
import io.mojaloop.core.wallet.contract.command.position.ReservePositionCommand;
import io.mojaloop.core.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import io.mojaloop.core.wallet.contract.exception.position.PositionLimitExceededException;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.spec.core.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class ReservePayerPosition {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservePayerPosition.class);

    private final ReservePositionCommand reservePositionCommand;

    private final AddStepPublisher addStepPublisher;

    public ReservePayerPosition(ReservePositionCommand reservePositionCommand, AddStepPublisher addStepPublisher) {

        assert reservePositionCommand != null;
        assert addStepPublisher != null;

        this.reservePositionCommand = reservePositionCommand;
        this.addStepPublisher = addStepPublisher;
    }

    public Output execute(Input input) throws FspiopException, NoPositionUpdateForTransactionException, PositionLimitExceededException {

        LOGGER.info("Reserving payer position : input : [{}]", input);

        final var CONTEXT = input.context();
        final var STEP_NAME = "reserve-payer-position";

        try {

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

            var walletOwnerId = new WalletOwnerId(payerFsp.fspId().getId());
            var description = "Transfer " + currency + " " + transferAmountString + " from " + payerFspCode.value() + " to " + payeeFspCode.value();

            var before = new HashMap<String, String>();

            before.put("walletOwnerId", walletOwnerId.getId().toString());
            before.put("currency", currency.name());
            before.put("transferAmount", transferAmountString);
            before.put("transactionId", transactionIdString);
            before.put("transactionAt", transactionAtString);
            before.put("description", description);

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, STEP_NAME, CONTEXT, before, StepPhase.BEFORE));

            var reservePositionOutput = this.reservePositionCommand.execute(
                new ReservePositionCommand.Input(walletOwnerId, currency, transferAmount, transactionId, transactionAt, description));

            var after = new HashMap<String, String>();

            after.put("positionUpdateId", reservePositionOutput.positionUpdateId().getId().toString());
            after.put("oldPosition", reservePositionOutput.oldPosition().stripTrailingZeros().toPlainString());
            after.put("newPosition", reservePositionOutput.newPosition().stripTrailingZeros().toPlainString());
            after.put("oldReserved", reservePositionOutput.oldReserved().stripTrailingZeros().toPlainString());
            after.put("newReserved", reservePositionOutput.newReserved().stripTrailingZeros().toPlainString());

            this.addStepPublisher.publish(new AddStepCommand.Input(transactionId, STEP_NAME, CONTEXT, after, StepPhase.AFTER));

            var output = new Output(reservePositionOutput.positionUpdateId());

            LOGGER.info("Reserved payer position successfully: output : [{}]", output);

            return output;

        } catch (NoPositionUpdateForTransactionException e) {

            LOGGER.error("No position updated for payer FSP: [{}], currency: [{}], amount: [{}]", input.payerFsp.fspId().getId(), input.currency(), input.transferAmount());

            var error = new HashMap<String, String>();

            error.put("error", e.getMessage());

            this.addStepPublisher.publish(new AddStepCommand.Input(e.getTransactionId(), STEP_NAME, CONTEXT, error, StepPhase.ERROR));

            throw new FspiopException(FspiopErrors.INTERNAL_SERVER_ERROR, e.getMessage());

        } catch (PositionLimitExceededException e) {

            LOGGER.error("Error:", e);

            this.addStepPublisher.publish(new AddStepCommand.Input(e.getTransactionId(), STEP_NAME, CONTEXT, Map.of("error", e.getMessage()), StepPhase.ERROR));

            throw new FspiopException(FspiopErrors.PAYER_LIMIT_ERROR, e.getMessage());

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            this.addStepPublisher.publish(new AddStepCommand.Input(input.transactionId, STEP_NAME, CONTEXT, Map.of("error", e.getMessage()), StepPhase.ERROR));

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, e.getMessage());
        }

    }

    public record Input(String context, TransactionId transactionId, Instant transactionAt, FspData payerFsp, FspData payeeFsp, Currency currency, BigDecimal transferAmount) { }

    public record Output(PositionUpdateId positionReservationId) { }

}
