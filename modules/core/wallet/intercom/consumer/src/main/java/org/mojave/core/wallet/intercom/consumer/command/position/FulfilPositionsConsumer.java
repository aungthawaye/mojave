package org.mojave.core.wallet.intercom.consumer.command.position;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.JetStream;
import io.nats.client.Message;
import io.nats.client.PushSubscribeOptions;
import io.nats.client.api.AckPolicy;
import io.nats.client.api.ConsumerConfiguration;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.component.nats.JetStreamConfigurer;
import org.mojave.core.wallet.contract.command.position.FulfilPositionsCommand;
import org.mojave.core.wallet.contract.constant.WalletStreamName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class FulfilPositionsConsumer {

    public static final String CONSUMER_NAME = "wallet:fulfil-positions-consumer";

    private static final Logger LOGGER = LoggerFactory.getLogger(FulfilPositionsConsumer.class);

    private final FulfilPositionsCommand fulfilPositionsCommand;

    private final ObjectMapper objectMapper;

    private final Dispatcher dispatcher;

    public FulfilPositionsConsumer(final FulfilPositionsCommand fulfilPositionsCommand,
                                   final Connection connection,
                                   final ObjectMapper objectMapper) {

        Objects.requireNonNull(fulfilPositionsCommand);
        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.fulfilPositionsCommand = fulfilPositionsCommand;
        this.objectMapper = objectMapper;
        this.dispatcher = connection.createDispatcher(this::handle);

        this.subscribe(connection);
    }

    private void handle(final Message message) {

        try {

            final var input = this.objectMapper.readValue(
                message.getData(), FulfilPositionsCommand.Input.class);

            this.fulfilPositionsCommand.execute(input);
            message.ack();

        } catch (final UncheckedDomainException exception) {

            LOGGER.error("FulfilPositionsConsumer business error:", exception);
            message.ack();

        } catch (final Exception exception) {

            LOGGER.error("FulfilPositionsConsumer processing error:", exception);
            message.nak();
        }
    }

    private void subscribe(final Connection connection) {

        try {

            JetStreamConfigurer.ensureStream(
                connection, WalletStreamName.STREAM_NAME, FulfilPositionsCommand.TOPIC_NAME);

            final JetStream jetStream = connection.jetStream();

            final var consumerConfiguration = ConsumerConfiguration
                                                  .builder()
                                                  .durable(CONSUMER_NAME)
                                                  .ackPolicy(AckPolicy.Explicit)
                                                  .filterSubject(FulfilPositionsCommand.TOPIC_NAME)
                                                  .build();

            final PushSubscribeOptions subscribeOptions = PushSubscribeOptions
                                                              .builder()
                                                              .stream(WalletStreamName.STREAM_NAME)
                                                              .configuration(consumerConfiguration)
                                                              .build();

            jetStream.subscribe(
                FulfilPositionsCommand.TOPIC_NAME, this.dispatcher, this::handle, false,
                subscribeOptions);

        } catch (final Exception exception) {

            throw new RuntimeException(exception);
        }
    }

}
