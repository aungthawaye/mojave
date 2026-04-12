package org.mojave.rail.fspiop.quoting.domain.async.producer;

import io.nats.client.Connection;
import jakarta.annotation.PostConstruct;
import org.mojave.component.nats.CommandPublisher;
import org.mojave.component.nats.JetStreamConfigurer;
import org.mojave.rail.fspiop.quoting.contract.command.step.UpdateQuotesResponseStep;
import org.mojave.rail.fspiop.quoting.contract.constant.QuotingStreamName;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Service
public class UpdateQuotesResponseStepProducer {

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public UpdateQuotesResponseStepProducer(final Connection connection,
                                            final ObjectMapper objectMapper) {

        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.connection = connection;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void initialize() {

        JetStreamConfigurer.ensureStream(
            this.connection, QuotingStreamName.STREAM_NAME,
            UpdateQuotesResponseStep.TOPIC_NAME);
    }

    public void publish(final UpdateQuotesResponseStep.Input input) {

        CommandPublisher.publish(
            this.connection, UpdateQuotesResponseStep.TOPIC_NAME, input,
            this.objectMapper);
    }

}
