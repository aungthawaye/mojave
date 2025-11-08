package io.mojaloop.core.transaction.consumer.listener;

import io.mojaloop.component.kafka.KafkaConsumerConfigurer;
import io.mojaloop.core.transaction.contract.command.CloseTransactionCommand;
import io.mojaloop.core.transaction.contract.constant.TopicNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class CloseTransactionListener {

    public static final String QUALIFIER = "closeTransaction";

    public static final String LISTENER_CONTAINER_FACTORY = "closeTransactionListenerContainerFactory";

    public static final String GROUP_ID = "primary-consumer";

    private static final Logger LOGGER = LoggerFactory.getLogger(CloseTransactionListener.class);

    private final CloseTransactionCommand closeTransactionCommand;

    public CloseTransactionListener(CloseTransactionCommand closeTransactionCommand) {

        assert closeTransactionCommand != null;

        this.closeTransactionCommand = closeTransactionCommand;
    }

    @KafkaListener(topics = TopicNames.COMMIT_TRANSACTION, containerFactory = LISTENER_CONTAINER_FACTORY, groupId = GROUP_ID)
    public void handle(CloseTransactionCommand.Input input, Acknowledgment ack) {

        try {

            LOGGER.info("Received CloseTransactionCommand with input: {}", input);

            this.closeTransactionCommand.execute(input);

            ack.acknowledge();

            LOGGER.info("Acknowledged CloseTransactionCommand with input: {}", input);

        } catch (Exception e) {

            LOGGER.error("Error handling CloseTransactionCommand with input: {}", input, e);
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

            super(bootstrapServers, groupId, clientId, autoOffsetReset, concurrency, pollTimeoutMs, autoCommit, ackMode);
        }

    }

}
