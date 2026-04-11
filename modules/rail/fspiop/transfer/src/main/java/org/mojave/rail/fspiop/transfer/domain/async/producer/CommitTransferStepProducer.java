package org.mojave.rail.fspiop.transfer.domain.async.producer;

import io.nats.client.Connection;
import jakarta.annotation.PostConstruct;
import org.mojave.component.nats.CommandPublisher;
import org.mojave.component.nats.JetStreamConfigurer;
import org.mojave.rail.fspiop.transfer.contract.command.step.stateful.CommitTransferStep;
import org.mojave.rail.fspiop.transfer.contract.constant.TransferStreamName;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Service
public class CommitTransferStepProducer {

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public CommitTransferStepProducer(final Connection connection, final ObjectMapper objectMapper) {

        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.connection = connection;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void initialize() {

        JetStreamConfigurer.ensureStream(
            this.connection, TransferStreamName.STREAM_NAME,
            CommitTransferStep.TOPIC_NAME);
    }

    public void publish(final CommitTransferStep.Input input) {

        CommandPublisher.publish(
            this.connection, CommitTransferStep.TOPIC_NAME, input,
            this.objectMapper);

    }

}
