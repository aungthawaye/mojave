package org.mojave.core.transfer.domain.kafka.listener;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.kafka.KafkaConsumerConfigurer;
import org.mojave.core.transfer.contract.command.step.stateful.AbortTransferStep;
import org.mojave.core.transfer.domain.kafka.TopicNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class AbortTransferStepListener {

    public static final String QUALIFIER = "abortTransferStep";

    public static final String LISTENER_CONTAINER_FACTORY = "abortTransferStepListenerContainerFactory";

    public static final String GROUP_ID = "primary-consumer";

    private static final Logger LOGGER = LoggerFactory.getLogger(AbortTransferStepListener.class);

    private final AbortTransferStep abortTransferStep;

    public AbortTransferStepListener(AbortTransferStep abortTransferStep) {

        assert abortTransferStep != null;

        this.abortTransferStep = abortTransferStep;
    }

    @KafkaListener(
        topics = TopicNames.ABORT_TRANSFER_STEP,
        containerFactory = LISTENER_CONTAINER_FACTORY,
        groupId = GROUP_ID)
    public void handle(List<AbortTransferStep.Input> inputs, Acknowledgment ack) {

        try {

            for (AbortTransferStep.Input input : inputs) {
                this.abortTransferStep.execute(input);
            }

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
                        int maxPollRecords,
                        int concurrency,
                        int pollTimeoutMs,
                        boolean autoCommit,
                        ContainerProperties.AckMode ackMode) {

            super(
                bootstrapServers, groupId, clientId, autoOffsetReset, maxPollRecords, concurrency,
                pollTimeoutMs, autoCommit, ackMode);
        }

    }

}
