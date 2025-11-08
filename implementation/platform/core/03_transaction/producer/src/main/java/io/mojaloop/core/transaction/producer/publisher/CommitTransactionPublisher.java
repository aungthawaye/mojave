package io.mojaloop.core.transaction.producer.publisher;

import io.mojaloop.core.transaction.contract.command.CloseTransactionCommand;
import io.mojaloop.core.transaction.contract.constant.TopicNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CommitTransactionPublisher {

    public static final String QUALIFIER = "commitTransaction";

    private static final Logger LOGGER = LoggerFactory.getLogger(CommitTransactionPublisher.class);

    private final KafkaTemplate<String, CloseTransactionCommand.Input> kafkaTemplate;

    public CommitTransactionPublisher(@Qualifier(QUALIFIER) KafkaTemplate<String, CloseTransactionCommand.Input> kafkaTemplate) {

        assert kafkaTemplate != null;

        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(CloseTransactionCommand.Input input) {

        LOGGER.info("Publishing CommitTransactionCommand with input: {}", input);

        this.kafkaTemplate.send(TopicNames.COMMIT_TRANSACTION, input.transactionId().getId().toString(), input);

        LOGGER.info("Published CommitTransactionCommand with input: {}", input);

    }

}
