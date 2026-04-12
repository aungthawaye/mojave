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
import org.mojave.core.wallet.contract.command.position.RollbackReservationCommand;
import org.mojave.core.wallet.contract.constant.WalletStreamName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class RollbackReservationConsumer {

    public static final String CONSUMER_NAME = "wallet:rollback-reservation-consumer";

    private static final Logger LOGGER = LoggerFactory.getLogger(
        RollbackReservationConsumer.class);

    private final RollbackReservationCommand rollbackReservationCommand;

    private final ObjectMapper objectMapper;

    private final Dispatcher dispatcher;

    public RollbackReservationConsumer(final RollbackReservationCommand rollbackReservationCommand,
                                       final Connection connection,
                                       final ObjectMapper objectMapper) {

        Objects.requireNonNull(rollbackReservationCommand);
        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.rollbackReservationCommand = rollbackReservationCommand;
        this.objectMapper = objectMapper;
        this.dispatcher = connection.createDispatcher(this::handle);

        this.subscribe(connection);
    }

    private void handle(final Message message) {

        try {

            final var input = this.objectMapper.readValue(
                message.getData(), RollbackReservationCommand.Input.class);

            this.rollbackReservationCommand.execute(input);
            message.ack();

        } catch (final UncheckedDomainException exception) {

            LOGGER.error("RollbackReservationConsumer business error:", exception);
            message.ack();

        } catch (final Exception exception) {

            LOGGER.error("RollbackReservationConsumer processing error:", exception);
            message.nak();
        }
    }

    private void subscribe(final Connection connection) {

        try {

            JetStreamConfigurer.ensureStream(
                connection, WalletStreamName.STREAM_NAME,
                RollbackReservationCommand.TOPIC_NAME);

            final JetStream jetStream = connection.jetStream();

            final var consumerConfiguration = ConsumerConfiguration
                                                  .builder()
                                                  .durable(CONSUMER_NAME)
                                                  .ackPolicy(AckPolicy.Explicit)
                                                  .filterSubject(RollbackReservationCommand.TOPIC_NAME)
                                                  .build();

            final PushSubscribeOptions subscribeOptions = PushSubscribeOptions
                                                              .builder()
                                                              .stream(WalletStreamName.STREAM_NAME)
                                                              .configuration(consumerConfiguration)
                                                              .build();

            jetStream.subscribe(
                RollbackReservationCommand.TOPIC_NAME, this.dispatcher, this::handle, false,
                subscribeOptions);

        } catch (final Exception exception) {

            throw new RuntimeException(exception);
        }
    }

}
