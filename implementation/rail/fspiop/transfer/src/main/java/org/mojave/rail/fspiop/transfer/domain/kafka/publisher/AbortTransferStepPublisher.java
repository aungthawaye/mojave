package org.mojave.rail.fspiop.transfer.domain.kafka.publisher;

import org.mojave.rail.fspiop.transfer.contract.command.step.stateful.AbortTransferStep;
import org.mojave.rail.fspiop.transfer.domain.kafka.TopicNames;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AbortTransferStepPublisher {

    public static final String QUALIFIER = "abortTransferStep";

    private final KafkaTemplate<String, AbortTransferStep.Input> kafkaTemplate;

    public AbortTransferStepPublisher(
        @Qualifier(QUALIFIER) KafkaTemplate<String, AbortTransferStep.Input> kafkaTemplate) {

        assert kafkaTemplate != null;

        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(AbortTransferStep.Input input) {

        this.kafkaTemplate.send(
            TopicNames.ABORT_TRANSFER_STEP, input.transferId().getId().toString(), input);

    }

}
