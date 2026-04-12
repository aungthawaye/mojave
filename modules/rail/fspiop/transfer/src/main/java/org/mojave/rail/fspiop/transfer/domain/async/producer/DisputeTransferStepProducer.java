package org.mojave.rail.fspiop.transfer.domain.async.producer;

import io.nats.client.Connection;
import jakarta.annotation.PostConstruct;
import org.mojave.component.nats.NatsPublisher;
import org.mojave.component.nats.JetStreamConfigurer;
import org.mojave.rail.fspiop.transfer.contract.command.step.stateful.DisputeTransferStep;
import org.mojave.rail.fspiop.transfer.contract.constant.TransferStreamName;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Service
public class DisputeTransferStepProducer {

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public DisputeTransferStepProducer(final Connection connection, final ObjectMapper objectMapper) {

        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.connection = connection;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void initialize() {

        JetStreamConfigurer.ensureStream(
            this.connection, TransferStreamName.STREAM_NAME,
            DisputeTransferStep.TOPIC_NAME);
    }

    public void publish(final DisputeTransferStep.Input input) {

        NatsPublisher.publish(
            this.connection, DisputeTransferStep.TOPIC_NAME, input,
            this.objectMapper);

    }

}
