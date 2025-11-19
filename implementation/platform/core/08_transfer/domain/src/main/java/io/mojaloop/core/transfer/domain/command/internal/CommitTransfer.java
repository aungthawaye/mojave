package io.mojaloop.core.transfer.domain.command.internal;

import io.mojaloop.component.jpa.routing.RoutingDataSource;
import io.mojaloop.component.jpa.transaction.TransactionContext;
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.common.datatype.enums.trasaction.StepPhase;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.transaction.contract.command.AddStepCommand;
import io.mojaloop.core.transaction.intercom.client.api.command.OpenTransactionInvoker;
import io.mojaloop.core.transaction.producer.publisher.AddStepPublisher;
import io.mojaloop.core.transaction.producer.publisher.CloseTransactionPublisher;
import io.mojaloop.core.transfer.domain.model.Transfer;
import io.mojaloop.core.transfer.domain.repository.TransferRepository;
import io.mojaloop.core.wallet.contract.command.position.CommitReservationCommand;
import io.mojaloop.core.wallet.contract.command.position.DecreasePositionCommand;
import io.mojaloop.core.wallet.store.PositionStore;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.component.handy.FspiopDates;
import io.mojaloop.fspiop.service.api.forwarder.ForwardRequest;
import io.mojaloop.fspiop.service.api.transfers.RespondTransfers;
import io.mojaloop.fspiop.spec.core.TransfersIDPutResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class CommitTransfer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommitTransfer.class);

    private final PositionStore positionStore;

    private final OpenTransactionInvoker openTransactionInvoker;

    private final CommitPosition commitPosition;

    private final RollbackPosition rollbackPosition;

    private final DecreasePosition decreasePosition;

    private final IncreasePositionInvoker increasePositionInvoker;

    private final RespondTransfers respondTransfers;

    private final ForwardRequest forwardRequest;

    private final AddStepPublisher addStepPublisher;

    private final CloseTransactionPublisher closeTransactionPublisher;

    private final TransferRepository transferRepository;

    private final PlatformTransactionManager transactionManager;

    public CommitTransfer(PositionStore positionStore,
                          OpenTransactionInvoker openTransactionInvoker,
                          CommitPosition commitPosition,
                          RollbackPosition rollbackPosition,
                          DecreasePosition decreasePosition,
                          IncreasePositionInvoker increasePositionInvoker,
                          RespondTransfers respondTransfers,
                          ForwardRequest forwardRequest,
                          AddStepPublisher addStepPublisher,
                          CloseTransactionPublisher closeTransactionPublisher,
                          TransferRepository transferRepository,
                          PlatformTransactionManager transactionManager) {

        assert positionStore != null;
        assert openTransactionInvoker != null;
        assert commitPosition != null;
        assert rollbackPosition != null;
        assert decreasePosition != null;
        assert increasePositionInvoker != null;
        assert respondTransfers != null;
        assert forwardRequest != null;
        assert addStepPublisher != null;
        assert closeTransactionPublisher != null;
        assert transferRepository != null;
        assert transactionManager != null;

        this.positionStore = positionStore;
        this.openTransactionInvoker = openTransactionInvoker;
        this.commitPosition = commitPosition;
        this.rollbackPosition = rollbackPosition;
        this.decreasePosition = decreasePosition;
        this.increasePositionInvoker = increasePositionInvoker;
        this.respondTransfers = respondTransfers;
        this.forwardRequest = forwardRequest;
        this.addStepPublisher = addStepPublisher;
        this.closeTransactionPublisher = closeTransactionPublisher;
        this.transferRepository = transferRepository;
        this.transactionManager = transactionManager;

    }

    public Result commit(FspData payeeFsp, Transfer transfer, TransfersIDPutResponse response) throws Exception {

        boolean committedPosition = false;
        boolean decreasedPosition = false;

        try {

            Instant transferCompletedAt = Instant.now();

            if (response.getCompletedTimestamp() != null) {
                try {
                    transferCompletedAt = FspiopDates.fromRequestBody(response.getCompletedTimestamp());
                } catch (Exception ignored) { }
            }

            var commitOutput = this.commitPayerPosition(transfer);
            var payerCommitId = commitOutput.positionUpdateId();
            committedPosition = true;

            var decreaseOutput = this.decreasePayeePosition(payeeFsp, transfer);
            var payeeCommitId = decreaseOutput.positionUpdateId();
            decreasedPosition = true;

            // Update the Transfer data in the database.
            this.addStepPublisher.publish(new AddStepCommand.Input(transfer.getTransactionId(), "put-transfer|commit-transaction",
                Map.of("payerCommitId", payerCommitId.getId().toString(), "payeeCommitId", payeeCommitId.getId().toString()), StepPhase.BEFORE));

            TransactionContext.startNew(this.transactionManager, transfer.getUdfTransferId().getId(), RoutingDataSource.Keys.WRITE);

            transfer = this.transferRepository.getReferenceById(transfer.getId());
            transfer.committed(response.getFulfilment(), payerCommitId, payeeCommitId, transferCompletedAt);

            this.transferRepository.save(transfer);
            TransactionContext.commit();

            this.addStepPublisher.publish(new AddStepCommand.Input(transfer.getTransactionId(), "put-transfer|commit-transaction", Map.of("-", "-"), StepPhase.AFTER));

        } catch (Exception e) {

            if (committedPosition) {

                this.rollbackPayerPosition(transfer);
            }

            if (decreasedPosition) {

                this.rollbackPayeePosition(transfer);
            }

        }
    }

    private CommitReservationCommand.Output commitPayerPosition(Transfer transfer) throws FspiopException {

        try {

            LOGGER.info("Committing reserved position : positionId : [{}]", transfer.getReservationId().getId().toString());

            this.addStepPublisher.publish(
                new AddStepCommand.Input(transfer.getTransactionId(), "put-transfer|commit-position", Map.of("reservationId", transfer.getReservationId().getId().toString()),
                    StepPhase.BEFORE));

            var output = this.commitPosition.execute(new CommitReservationCommand.Input(transfer.getReservationId(), new PositionUpdateId(Snowflake.get().nextId()), null));

            this.addStepPublisher.publish(
                new AddStepCommand.Input(transfer.getTransactionId(), "put-transfer|commit-position", Map.of("payerCommitId", output.positionUpdateId().getId().toString()),
                    StepPhase.AFTER));

            LOGGER.info("Committed reserved position successfully : reservationId : [{}],  payerCommitId : [{}]", transfer.getReservationId().getId().toString(),
                output.positionUpdateId().getId().toString());

            return output;

        } catch (Exception e) {

            LOGGER.error("Failed to commit payer position : positionId : [{}]", transfer.getReservationId().getId().toString());

            throw new FspiopException(FspiopErrors.INTERNAL_SERVER_ERROR);
        }

    }

    private DecreasePositionCommand.Output decreasePayeePosition(FspData payeeFsp, Transfer transfer) throws FspiopException {

        try {

            LOGGER.info("Decreasing payee position : positionId : [{}]", transfer.getReservationId().getId().toString());

            var payeePosition = this.positionStore.get(new WalletOwnerId(payeeFsp.fspId().getId()), transfer.getCurrency());
            var payeePositionId = payeePosition.positionId();
            var positionUpdateId = new PositionUpdateId(Snowflake.get().nextId());

            this.addStepPublisher.publish(new AddStepCommand.Input(transfer.getTransactionId(), "put-transfer|decrease-position",
                Map.of("payeePositionId", payeePositionId.getId().toString(), "positionUpdateId", positionUpdateId.getId().toString()), StepPhase.BEFORE));

            var transferAmount = transfer.getTransferAmount();

            var output = this.decreasePosition.execute(new DecreasePositionCommand.Input(payeePositionId, transferAmount, transfer.getTransactionId(), transfer.getTransactionAt(),
                "Receiving : " + transferAmount.stripTrailingZeros().toPlainString() + " " + transfer.getCurrency().name()));

            var payeeCommitId = output.positionUpdateId();

            this.addStepPublisher.publish(
                new AddStepCommand.Input(transfer.getTransactionId(), "put-transfer|decrease-position", Map.of("payeeCommitId", payeeCommitId.getId().toString()),
                    StepPhase.AFTER));

            LOGGER.info("Committed decrease payee position successfully : positionId : [{}]", payeeCommitId.getId().toString());

            return output;

        } catch (Exception e) {

            LOGGER.error("Failed to decrease payee position : positionId : [{}]", transfer.getReservationId().getId().toString());

            throw new FspiopException(FspiopErrors.INTERNAL_SERVER_ERROR);

        }
    }

    private void rollbackPayeePosition(PositionUpdateId payeeCommitId) throws FspiopException {

        LOGGER.info("Rolling back payee position : payeeCommitId : [{}]", payeeCommitId.getId().toString());
    }

    private void rollbackPayerPosition(CommitReservationCommand.Output output) throws FspiopException {

        var payerPositionId = output.positionId();
        var payerCommitId = output.positionUpdateId();

        LOGGER.info("Rolling back/decrease payer position : payerPositionId : [{}], payerCommitId : [{}]", payerPositionId.getId(), payerCommitId.getId().toString());

        try {

            var description = "Decrease the committed Payer position.";

            var before = new HashMap<String, String>();

            before.put("positionId", payerPositionId.getId().toString());
            before.put("amount", output.amount().stripTrailingZeros().toPlainString());
            before.put("transactionId", output.transactionId().getId().toString());
            before.put("transactionAt", output.transactionAt().getEpochSecond() + "");
            before.put("description", description);

            this.addStepPublisher.publish(new AddStepCommand.Input(output.transactionId(), "post-transfers|rollback-payer-position", before, StepPhase.BEFORE));

            var decreased = this.decreasePosition.execute(
                new DecreasePositionCommand.Input(payerPositionId, output.amount(), output.transactionId(), output.transactionAt(), description));

            var after = new HashMap<String, String>();

            after.put("positionUpdateId", decreased.positionUpdateId().getId().toString());
            after.put("oldPosition", decreased.oldPosition().stripTrailingZeros().toPlainString());
            after.put("newPosition", decreased.newPosition().stripTrailingZeros().toPlainString());
            after.put("oldReserved", decreased.oldReserved().stripTrailingZeros().toPlainString());
            after.put("newReserved", decreased.newReserved().stripTrailingZeros().toPlainString());

            this.addStepPublisher.publish(new AddStepCommand.Input(output.transactionId(), "post-transfers|rollback-payer-position", after, StepPhase.AFTER));

            LOGGER.info("Rolled back/decreased payer position successfully : payerPositionId : [{}], payerCommitId : [{}]", payerPositionId.getId(),
                payerCommitId.getId().toString());

        } catch (Exception e) {

            LOGGER.error("Failed to rollback reserved position : payerCommitId : [{}]", payerCommitId.getId().toString());

            throw new FspiopException(FspiopErrors.INTERNAL_SERVER_ERROR);
        }
    }

    public record Result(PositionUpdateId payerCommitId, PositionUpdateId payeeCommitId) { }

}
