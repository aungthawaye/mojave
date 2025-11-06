package io.mojaloop.core.transaction.producer.publisher;

import io.mojaloop.core.transaction.contract.command.AddStepCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AddStepPublisher {

    public static final String QUALIFIER = "addStep";

    private static final Logger LOGGER = LoggerFactory.getLogger(AddStepPublisher.class);

    private final KafkaTemplate<String, AddStepCommand.Input> kafkaTemplate;

    public AddStepPublisher(@Qualifier(QUALIFIER) KafkaTemplate<String, AddStepCommand.Input> kafkaTemplate) {

        assert kafkaTemplate != null;

        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(AddStepCommand.Input input) {

        LOGGER.info("Publishing AddStepCommand with input: {}", input);

        this.kafkaTemplate.send("add-step-command", input.transactionId().getId().toString(), input);

        LOGGER.info("Published AddStepCommand with input: {}", input);

    }

}
