package org.mojave.rail.fspiop.transfer.domain.kafka.publisher;

import org.mojave.rail.fspiop.transfer.contract.command.step.stateful.DisputeTransferStep;
import org.mojave.rail.fspiop.transfer.domain.kafka.TopicNames;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.Objects;

@Service
public class DisputeTransferStepPublisher {

    public static final String QUALIFIER = "disputeTransferStep";

    private final KafkaTemplate<String, DisputeTransferStep.Input> kafkaTemplate;

    public DisputeTransferStepPublisher(@Qualifier(QUALIFIER)
                                        final KafkaTemplate<String, DisputeTransferStep.Input> kafkaTemplate) {

        Objects.requireNonNull(kafkaTemplate);

        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(final DisputeTransferStep.Input input) {

        this.kafkaTemplate.send(
            TopicNames.DISPUTE_TRANSFER_STEP, input.transferId().getId().toString(), input);

    }

}
