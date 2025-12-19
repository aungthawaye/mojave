package org.mojave.core.transfer.domain.kafka.publisher;

import org.mojave.core.transfer.contract.command.step.stateful.AbortTransferStep;
import org.mojave.core.transfer.domain.kafka.TopicNames;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Qualifier(AbortTransferStep.Qualifiers.PUBLISHER)
public class AbortTransferStepPublisher implements AbortTransferStep {

    public static final String QUALIFIER = "abortTransferStep";

    private final KafkaTemplate<String, AbortTransferStep.Input> kafkaTemplate;

    public AbortTransferStepPublisher(
        @Qualifier(QUALIFIER) KafkaTemplate<String, AbortTransferStep.Input> kafkaTemplate) {

        assert kafkaTemplate != null;

        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void execute(AbortTransferStep.Input input) {

        this.kafkaTemplate.send(
            TopicNames.ABORT_TRANSFER_STEP, input.transferId().getId().toString(), input);

    }

}
