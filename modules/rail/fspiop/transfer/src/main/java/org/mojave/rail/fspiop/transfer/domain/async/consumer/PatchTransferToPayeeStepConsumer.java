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
import org.mojave.rail.fspiop.transfer.contract.command.step.fspiop.PatchTransferToPayeeStep;
import org.mojave.rail.fspiop.transfer.contract.constant.TransferStreamName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class PatchTransferToPayeeStepConsumer {

    public static final String CONSUMER_NAME = "fspiop-transfer:patch-transfer-to-payee-step-consumer";

    private static final Logger LOGGER = LoggerFactory.getLogger(PatchTransferToPayeeStepConsumer.class);

    private final PatchTransferToPayeeStep patchTransferToPayeeStep;

    private final ObjectMapper objectMapper;

    private final Dispatcher dispatcher;

    public PatchTransferToPayeeStepConsumer(final PatchTransferToPayeeStep patchTransferToPayeeStep,
                                            final ObjectMapper objectMapper, final Connection connection) {

        Objects.requireNonNull(patchTransferToPayeeStep);
        Objects.requireNonNull(objectMapper);
        Objects.requireNonNull(connection);

        this.patchTransferToPayeeStep = patchTransferToPayeeStep;
        this.objectMapper = objectMapper;
        this.dispatcher = connection.createDispatcher(this::handle);

        this.subscribe(connection);
    }

    private void handle(final Message message) {

        try {

            final var input = this.objectMapper.readValue(
                message.getData(), PatchTransferToPayeeStep.Input.class);

            this.patchTransferToPayeeStep.execute(input);
            message.ack();

        } catch (final UncheckedDomainException exception) {

            LOGGER.error("PatchTransferToPayeeStepConsumer business error:", exception);
            message.ack();

        } catch (final Exception exception) {

            LOGGER.error("PatchTransferToPayeeStepConsumer processing error:", exception);
            message.nak();
        }
    }

    private void subscribe(final Connection connection) {

        try {

            JetStreamConfigurer.ensureStream(
                connection, TransferStreamName.STREAM_NAME, PatchTransferToPayeeStep.TOPIC_NAME);

            final JetStream jetStream = connection.jetStream();

            final var consumerConfiguration = ConsumerConfiguration
                                                  .builder()
                                                  .durable(CONSUMER_NAME)
                                                  .ackPolicy(AckPolicy.Explicit)
                                                  .filterSubject(PatchTransferToPayeeStep.TOPIC_NAME)
                                                  .build();

            final PushSubscribeOptions subscribeOptions = PushSubscribeOptions
                                                              .builder()
                                                              .stream(TransferStreamName.STREAM_NAME)
                                                              .configuration(consumerConfiguration)
                                                              .build();

            jetStream.subscribe(
                PatchTransferToPayeeStep.TOPIC_NAME, this.dispatcher, this::handle, false,
                subscribeOptions);

        } catch (final Exception exception) {

            throw new RuntimeException(exception);
        }
    }

}
