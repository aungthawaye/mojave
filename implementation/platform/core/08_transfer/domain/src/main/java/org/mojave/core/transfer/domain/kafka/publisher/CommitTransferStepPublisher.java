package org.mojave.core.transfer.domain.kafka.publisher;

import org.mojave.core.transfer.contract.command.step.stateful.CommitTransferStep;
import org.mojave.core.transfer.domain.kafka.TopicNames;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Qualifier(CommitTransferStep.Qualifiers.PUBLISHER)
public class CommitTransferStepPublisher implements CommitTransferStep {

    public static final String QUALIFIER = "commitTransferStep";

    private final KafkaTemplate<String, CommitTransferStep.Input> kafkaTemplate;

    public CommitTransferStepPublisher(
        @Qualifier(QUALIFIER) final KafkaTemplate<String, CommitTransferStep.Input> kafkaTemplate) {

        assert kafkaTemplate != null;

        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void execute(final CommitTransferStep.Input input) {

        this.kafkaTemplate.send(
            TopicNames.COMMIT_TRANSFER_STEP, input.transferId().getId().toString(), input);

    }

}
