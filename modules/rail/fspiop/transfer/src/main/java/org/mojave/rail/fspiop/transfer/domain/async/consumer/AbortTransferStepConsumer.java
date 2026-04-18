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
import org.mojave.rail.fspiop.transfer.contract.command.step.stateful.AbortTransferStep;
import org.mojave.rail.fspiop.transfer.contract.constant.TransferStreamName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class AbortTransferStepConsumer {

    public static final String CONSUMER_NAME = "fspiop-transfer:abort-transfer-step-consumer";

    private static final Logger LOGGER = LoggerFactory.getLogger(AbortTransferStepConsumer.class);

    private final AbortTransferStep abortTransferStep;

    private final ObjectMapper objectMapper;

    private final Dispatcher dispatcher;

    public AbortTransferStepConsumer(final AbortTransferStep abortTransferStep,
                                     final ObjectMapper objectMapper, final Connection connection) {

        Objects.requireNonNull(abortTransferStep);
        Objects.requireNonNull(objectMapper);
        Objects.requireNonNull(connection);

        this.abortTransferStep = abortTransferStep;
        this.objectMapper = objectMapper;
        this.dispatcher = connection.createDispatcher(this::handle);

        this.subscribe(connection);
    }

    private void handle(final Message message) {

        try {

            final var input = this.objectMapper.readValue(
                message.getData(), AbortTransferStep.Input.class);

            this.abortTransferStep.execute(input);
            message.ack();

        } catch (final UncheckedDomainException exception) {

            LOGGER.error("AbortTransferStepConsumer business error:", exception);
            message.ack();

        } catch (final Exception exception) {

            LOGGER.error("AbortTransferStepConsumer processing error:", exception);
            message.nak();
        }
    }

    private void subscribe(final Connection connection) {

        try {

            JetStreamConfigurer.ensureStream(
                connection, TransferStreamName.STREAM_NAME, AbortTransferStep.TOPIC_NAME);

            final JetStream jetStream = connection.jetStream();

            final var consumerConfiguration = ConsumerConfiguration
                                                  .builder()
                                                  .durable(CONSUMER_NAME)
                                                  .ackPolicy(AckPolicy.Explicit)
                                                  .filterSubject(AbortTransferStep.TOPIC_NAME)
                                                  .build();

            final PushSubscribeOptions subscribeOptions = PushSubscribeOptions
                                                              .builder()
                                                              .stream(TransferStreamName.STREAM_NAME)
                                                              .configuration(consumerConfiguration)
                                                              .build();

            jetStream.subscribe(
                AbortTransferStep.TOPIC_NAME, this.dispatcher, this::handle, false,
                subscribeOptions);

        } catch (final Exception exception) {

            throw new RuntimeException(exception);
        }
    }

}
