package org.mojave.core.transfer.domain.kafka.publisher;

import org.mojave.core.transfer.contract.command.step.financial.RollbackReservationStep;
import org.mojave.core.transfer.domain.kafka.TopicNames;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class RollbackReservationStepPublisher {

    public static final String QUALIFIER = "rollbackReservationStep";

    private final KafkaTemplate<String, RollbackReservationStep.Input> kafkaTemplate;

    public RollbackReservationStepPublisher(@Qualifier(QUALIFIER)
                                             final KafkaTemplate<String, RollbackReservationStep.Input> kafkaTemplate) {

        assert kafkaTemplate != null;

        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(final RollbackReservationStep.Input input) {

        this.kafkaTemplate.send(
            TopicNames.ROLLBACK_RESERVATION_STEP, input.transferId().getId().toString(), input);

    }

}
