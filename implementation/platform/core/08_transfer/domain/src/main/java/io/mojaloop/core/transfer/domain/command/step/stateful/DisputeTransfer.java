package io.mojaloop.core.transfer.domain.command.step.stateful;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.component.misc.logger.ObjectLogger;
import io.mojaloop.core.common.datatype.enums.transfer.DisputeReason;
import io.mojaloop.core.common.datatype.enums.trasaction.StepPhase;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.transfer.TransferId;
import io.mojaloop.core.transaction.contract.command.AddStepCommand;
import io.mojaloop.core.transaction.producer.publisher.AddStepPublisher;
import io.mojaloop.core.transfer.domain.repository.TransferRepository;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DisputeTransfer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbortTransfer.class);

    private final TransferRepository transferRepository;

    private final AddStepPublisher addStepPublisher;

    public DisputeTransfer(TransferRepository transferRepository,
                           AddStepPublisher addStepPublisher) {

        assert transferRepository != null;
        assert addStepPublisher != null;

        this.transferRepository = transferRepository;
        this.addStepPublisher = addStepPublisher;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Write
    public void execute(DisputeTransfer.Input input) throws FspiopException {

        var startAt = System.nanoTime();

        var CONTEXT = input.context;
        var STEP_NAME = "DisputeTransfer";

        LOGGER.info("DisputeTransfer : input : ({})", ObjectLogger.log(input));

        try {

            var transfer = this.transferRepository.getReferenceById(input.transferId);

            this.addStepPublisher.publish(new AddStepCommand.Input(
                input.transactionId, STEP_NAME, CONTEXT, ObjectLogger.log(input).toString(),
                StepPhase.BEFORE));

            transfer.disputed(input.disputeReason);

            this.transferRepository.save(transfer);

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    input.transactionId, STEP_NAME, CONTEXT, "-", StepPhase.AFTER));

            var endAt = System.nanoTime();
            LOGGER.info("DisputeTransfer : done , took {} ms", (endAt - startAt) / 1_000_000);

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            this.addStepPublisher.publish(
                new AddStepCommand.Input(
                    input.transactionId, STEP_NAME, CONTEXT, e.getMessage(),
                    StepPhase.ERROR));

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, e.getMessage());
        }
    }

    public record Input(String context,
                        TransactionId transactionId,
                        TransferId transferId,
                        DisputeReason disputeReason) { }

}
