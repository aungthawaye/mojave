package org.mojave.rail.fspiop.transfer.domain.kafka.publisher;

import org.mojave.rail.fspiop.transfer.contract.command.step.stateful.CommitTransferStep;
import org.mojave.rail.fspiop.transfer.domain.kafka.TopicNames;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.Objects;

@Service
public class CommitTransferStepPublisher {

    public static final String QUALIFIER = "commitTransferStep";

    private final KafkaTemplate<String, CommitTransferStep.Input> kafkaTemplate;

    public CommitTransferStepPublisher(
        @Qualifier(QUALIFIER) final KafkaTemplate<String, CommitTransferStep.Input> kafkaTemplate) {

        Objects.requireNonNull(kafkaTemplate);

        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(final CommitTransferStep.Input input) {

        this.kafkaTemplate.send(
            TopicNames.COMMIT_TRANSFER_STEP, input.transferId().getId().toString(), input);

    }

}
