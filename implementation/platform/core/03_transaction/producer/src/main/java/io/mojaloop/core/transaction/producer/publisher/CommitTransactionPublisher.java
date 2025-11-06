package io.mojaloop.core.transaction.producer.publisher;

import io.mojaloop.core.transaction.contract.command.CommitTransactionCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CommitTransactionPublisher {

    public static final String QUALIFIER = "commitTransaction";

    private static final Logger LOGGER = LoggerFactory.getLogger(CommitTransactionPublisher.class);

    private final KafkaTemplate<String, CommitTransactionCommand.Input> kafkaTemplate;

    public CommitTransactionPublisher(@Qualifier(QUALIFIER) KafkaTemplate<String, CommitTransactionCommand.Input> kafkaTemplate) {

        assert kafkaTemplate != null;

        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(CommitTransactionCommand.Input input) {

        LOGGER.info("Publishing CommitTransactionCommand with input: {}", input);

        this.kafkaTemplate.send("commit-transaction-command", input.transactionId().getId().toString(), input);

        LOGGER.info("Published CommitTransactionCommand with input: {}", input);

    }

}
