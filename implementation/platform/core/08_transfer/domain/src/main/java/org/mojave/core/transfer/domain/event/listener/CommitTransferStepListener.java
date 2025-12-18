package org.mojave.core.transfer.domain.event.listener;

import org.mojave.component.kafka.KafkaConsumerConfigurer;
import org.mojave.core.transfer.contract.command.step.stateful.CommitTransferStep;
import org.mojave.core.transfer.domain.event.TopicNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class CommitTransferStepListener {

    public static final String QUALIFIER = "commitTransferStep";

    public static final String LISTENER_CONTAINER_FACTORY = "commitTransferStepListenerContainerFactory";

    public static final String GROUP_ID = "primary-consumer";

    private static final Logger LOGGER = LoggerFactory.getLogger(CommitTransferStepListener.class);

    private final CommitTransferStep commitTransferStep;

    public CommitTransferStepListener(CommitTransferStep commitTransferStep) {

        assert commitTransferStep != null;

        this.commitTransferStep = commitTransferStep;
    }

    @KafkaListener(
        topics = TopicNames.COMMIT_TRANSFER_STEP,
        containerFactory = LISTENER_CONTAINER_FACTORY,
        groupId = GROUP_ID)
    public void handle(CommitTransferStep.Input input, Acknowledgment ack) {

        try {

            this.commitTransferStep.execute(input);

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
                bootstrapServers, groupId, clientId, autoOffsetReset, concurrency, pollTimeoutMs,
                autoCommit, ackMode);
        }

    }

}
