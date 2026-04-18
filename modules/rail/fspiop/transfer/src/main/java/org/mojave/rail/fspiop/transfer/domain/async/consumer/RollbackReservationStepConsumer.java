package org.mojave.rail.fspiop.transfer.domain.async.consumer;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.JetStream;
import io.nats.client.Message;
import io.nats.client.PushSubscribeOptions;
import io.nats.client.api.AckPolicy;
import io.nats.client.api.ConsumerConfiguration;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.component.nats.JetStreamConfigurer;
import org.mojave.rail.fspiop.transfer.contract.command.step.financial.RollbackReservationStep;
import org.mojave.rail.fspiop.transfer.contract.constant.TransferStreamName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class RollbackReservationStepConsumer {

    public static final String CONSUMER_NAME = "fspiop-transfer:rollback-reservation-step-consumer";

    private static final Logger LOGGER = LoggerFactory.getLogger(RollbackReservationStepConsumer.class);

    private final RollbackReservationStep rollbackReservationStep;

    private final ObjectMapper objectMapper;

    private final Dispatcher dispatcher;

    public RollbackReservationStepConsumer(final RollbackReservationStep rollbackReservationStep,
                                           final ObjectMapper objectMapper, final Connection connection) {

        Objects.requireNonNull(rollbackReservationStep);
        Objects.requireNonNull(objectMapper);
        Objects.requireNonNull(connection);

        this.rollbackReservationStep = rollbackReservationStep;
        this.objectMapper = objectMapper;
        this.dispatcher = connection.createDispatcher(this::handle);

        this.subscribe(connection);
    }

    private void handle(final Message message) {

        try {

            final var input = this.objectMapper.readValue(
                message.getData(), RollbackReservationStep.Input.class);

            this.rollbackReservationStep.execute(input);
            message.ack();

        } catch (final UncheckedDomainException exception) {

            LOGGER.error("RollbackReservationStepConsumer business error:", exception);
            message.ack();

        } catch (final Exception exception) {

            LOGGER.error("RollbackReservationStepConsumer processing error:", exception);
            message.nak();
        }
    }

    private void subscribe(final Connection connection) {

        try {

            JetStreamConfigurer.ensureStream(
                connection, TransferStreamName.STREAM_NAME, RollbackReservationStep.TOPIC_NAME);

            final JetStream jetStream = connection.jetStream();

            final var consumerConfiguration = ConsumerConfiguration
                                                  .builder()
                                                  .durable(CONSUMER_NAME)
                                                  .ackPolicy(AckPolicy.Explicit)
                                                  .filterSubject(RollbackReservationStep.TOPIC_NAME)
                                                  .build();

            final PushSubscribeOptions subscribeOptions = PushSubscribeOptions
                                                              .builder()
                                                              .stream(TransferStreamName.STREAM_NAME)
                                                              .configuration(consumerConfiguration)
                                                              .build();

            jetStream.subscribe(
                RollbackReservationStep.TOPIC_NAME, this.dispatcher, this::handle, false,
                subscribeOptions);

        } catch (final Exception exception) {

            throw new RuntimeException(exception);
        }
    }

}
