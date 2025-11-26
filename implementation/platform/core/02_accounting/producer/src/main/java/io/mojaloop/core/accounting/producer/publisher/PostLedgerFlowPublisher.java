package io.mojaloop.core.accounting.producer.publisher;

import io.mojaloop.core.accounting.contract.command.ledger.PostLedgerFlowCommand;
import io.mojaloop.core.accounting.contract.constant.TopicNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PostLedgerFlowPublisher {

    public static final String QUALIFIER = "addStep";

    private static final Logger LOGGER = LoggerFactory.getLogger(PostLedgerFlowPublisher.class);

    private final KafkaTemplate<String, PostLedgerFlowCommand.Input> kafkaTemplate;

    public PostLedgerFlowPublisher(@Qualifier(QUALIFIER) KafkaTemplate<String, PostLedgerFlowCommand.Input> kafkaTemplate) {

        assert kafkaTemplate != null;

        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(PostLedgerFlowCommand.Input input) {

        this.kafkaTemplate.send(
            TopicNames.POST_LEDGER_FLOW, input.transactionId().getId().toString(), input);

    }

}
