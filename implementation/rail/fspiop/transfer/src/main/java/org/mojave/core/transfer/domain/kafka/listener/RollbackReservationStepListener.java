package org.mojave.core.transfer.domain.kafka.listener;

import org.mojave.component.kafka.KafkaConsumerConfigurer;
import org.mojave.core.transfer.contract.command.step.financial.RollbackReservationStep;
import org.mojave.core.transfer.domain.kafka.TopicNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RollbackReservationStepListener {

    public static final String QUALIFIER = "rollbackReservationStep";

    public static final String LISTENER_CONTAINER_FACTORY = "rollbackReservationStepListenerContainerFactory";

    public static final String GROUP_ID = "primary-consumer";

    private static final Logger LOGGER = LoggerFactory.getLogger(RollbackReservationStepListener.class);

    private final RollbackReservationStep rollbackReservationStep;

    public RollbackReservationStepListener(RollbackReservationStep rollbackReservationStep) {

        assert rollbackReservationStep != null;

        this.rollbackReservationStep = rollbackReservationStep;
    }

    @KafkaListener(
        topics = TopicNames.ROLLBACK_RESERVATION_STEP,
        containerFactory = LISTENER_CONTAINER_FACTORY,
        groupId = GROUP_ID)
    public void handle(List<RollbackReservationStep.Input> inputs, Acknowledgment ack) {

        try {

            for (RollbackReservationStep.Input input : inputs) {
                this.rollbackReservationStep.execute(input);
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
