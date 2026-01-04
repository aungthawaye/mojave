package org.mojave.rail.fspiop.transfer.domain.kafka.listener;

import org.mojave.component.kafka.KafkaConsumerConfigurer;
import org.mojave.rail.fspiop.transfer.contract.command.step.fspiop.PatchTransferToPayeeStep;
import org.mojave.rail.fspiop.transfer.domain.kafka.TopicNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class PatchTransferToPayeeStepListener {

    public static final String QUALIFIER = "patchTransferToPayeeStep";

    public static final String LISTENER_CONTAINER_FACTORY =
        "patchTransferToPayeeStepListenerContainerFactory";

    public static final String GROUP_ID = "primary-consumer";

    private static final Logger LOGGER = LoggerFactory.getLogger(
        PatchTransferToPayeeStepListener.class);

    private final PatchTransferToPayeeStep patchTransferToPayeeStep;

    public PatchTransferToPayeeStepListener(PatchTransferToPayeeStep patchTransferToPayeeStep) {

        assert patchTransferToPayeeStep != null;

        this.patchTransferToPayeeStep = patchTransferToPayeeStep;
    }

    @KafkaListener(
        topics = TopicNames.PATCH_TRANSFER_TO_PAYEE_STEP,
        containerFactory = LISTENER_CONTAINER_FACTORY,
        groupId = GROUP_ID)
    public void handle(PatchTransferToPayeeStep.Input input, Acknowledgment ack) {

        try {
            this.patchTransferToPayeeStep.execute(input);
            ack.acknowledge();
        } catch (Exception e) {

            LOGGER.error("Error:", e);
        }

    }

    public static class Settings extends KafkaConsumerConfigurer.ConsumerSettings {

        public Settings(String bootstrapServers,
                        String groupId,
                        String clientId,
                        String autoOffsetReset,
                        int concurrency,
                        int pollTimeoutMs,
                        boolean autoCommit,
                        ContainerProperties.AckMode ackMode) {

            super(
                bootstrapServers, groupId, clientId, autoOffsetReset, 1, concurrency,
                pollTimeoutMs, autoCommit, ackMode);
        }

    }

}
