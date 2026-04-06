package org.mojave.wallet.intercom.producer.command.position;

import io.nats.client.Connection;
import jakarta.annotation.PostConstruct;
import org.mojave.component.nats.CommandPublisher;
import org.mojave.component.nats.JetStreamConfigurer;
import org.mojave.wallet.contract.command.position.FulfilPositionsCommand;
import org.mojave.wallet.contract.constant.WalletStreamName;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Service
public class FulfilPositionsProducer {

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public FulfilPositionsProducer(final Connection connection,
                                   final ObjectMapper objectMapper) {

        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.connection = connection;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void initialize() {

        JetStreamConfigurer.ensureStream(
            this.connection, WalletStreamName.STREAM_NAME,
            FulfilPositionsCommand.TOPIC_NAME);
    }

    public void publish(final FulfilPositionsCommand.Input input) {

        CommandPublisher.publish(
            this.connection, FulfilPositionsCommand.TOPIC_NAME, input,
            this.objectMapper);
    }

}
