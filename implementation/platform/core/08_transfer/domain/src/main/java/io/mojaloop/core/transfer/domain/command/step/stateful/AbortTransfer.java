package io.mojaloop.core.transfer.domain.command.step.stateful;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.core.common.datatype.enums.trasaction.StepPhase;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.transfer.TransferId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
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

import java.util.HashMap;
import java.util.Map;

@Service
public class AbortTransfer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbortTransfer.class);

    private final TransferRepository transferRepository;

    private final AddStepPublisher addStepPublisher;

    public AbortTransfer(TransferRepository transferRepository, AddStepPublisher addStepPublisher) {

        assert transferRepository != null;
        assert addStepPublisher != null;

        this.transferRepository = transferRepository;
        this.addStepPublisher = addStepPublisher;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Write
    public Output execute(Input input) throws FspiopException {

        var CONTEXT = input.context;
        var STEP_NAME = "abort-transfer";

        LOGGER.info("Aborting transfer : input : [{}]", input);

        try {

            var transfer = this.transferRepository.getReferenceById(input.transferId);

            var before = new HashMap<String, String>();

            before.put("rollbackId", input.rollbackId.getId().toString());
            before.put("error", input.error);

            this.addStepPublisher.publish(new AddStepCommand.Input(input.transactionId, STEP_NAME, CONTEXT, before, StepPhase.BEFORE));

            transfer.aborted(input.rollbackId, input.error);

            this.transferRepository.save(transfer);

            LOGGER.info("Aborted transfer successfully : transferId [{}]", transfer.getId());

            this.addStepPublisher.publish(new AddStepCommand.Input(input.transactionId, STEP_NAME, CONTEXT, Map.of("-", "-"), StepPhase.AFTER));

            return new Output();

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            this.addStepPublisher.publish(new AddStepCommand.Input(input.transactionId, STEP_NAME, CONTEXT, Map.of("error", e.getMessage()), StepPhase.ERROR));

            throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, e.getMessage());
        }
    }

    public record Input(String context, TransactionId transactionId, TransferId transferId, PositionUpdateId rollbackId, String error) { }

    public record Output() { }

}
