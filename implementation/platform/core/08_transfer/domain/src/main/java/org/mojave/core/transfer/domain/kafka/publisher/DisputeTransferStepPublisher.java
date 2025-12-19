package org.mojave.core.transfer.domain.kafka.publisher;

import org.mojave.core.transfer.contract.command.step.stateful.DisputeTransferStep;
import org.mojave.core.transfer.domain.kafka.TopicNames;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Qualifier(DisputeTransferStep.Qualifiers.PUBLISHER)
public class DisputeTransferStepPublisher implements DisputeTransferStep {

    public static final String QUALIFIER = "disputeTransferStep";

    private final KafkaTemplate<String, DisputeTransferStep.Input> kafkaTemplate;

    public DisputeTransferStepPublisher(
        @Qualifier(QUALIFIER) final KafkaTemplate<String, DisputeTransferStep.Input> kafkaTemplate) {

        assert kafkaTemplate != null;

        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void execute(final DisputeTransferStep.Input input) {

        this.kafkaTemplate.send(
            TopicNames.DISPUTE_TRANSFER_STEP, input.transferId().getId().toString(), input);

    }

}
