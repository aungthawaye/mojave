package org.mojave.rail.fspiop.transfer.domain.kafka.publisher;

import org.mojave.rail.fspiop.transfer.contract.command.step.fspiop.PatchTransferToPayeeStep;
import org.mojave.rail.fspiop.transfer.domain.kafka.TopicNames;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PatchTransferToPayeeStepPublisher {

    public static final String QUALIFIER = "patchTransferToPayeeStep";

    private final KafkaTemplate<String, PatchTransferToPayeeStep.Input> kafkaTemplate;

    public PatchTransferToPayeeStepPublisher(
        @Qualifier(QUALIFIER) KafkaTemplate<String, PatchTransferToPayeeStep.Input> kafkaTemplate) {

        assert kafkaTemplate != null;

        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(PatchTransferToPayeeStep.Input input) {

        this.kafkaTemplate.send(
            TopicNames.PATCH_TRANSFER_TO_PAYEE_STEP,
            input.udfTransferId().getId(),
            input);

    }

}
