package org.mojave.rail.fspiop.quoting.domain.async.consumer;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.JetStream;
import io.nats.client.Message;
import io.nats.client.PushSubscribeOptions;
import io.nats.client.api.AckPolicy;
import io.nats.client.api.ConsumerConfiguration;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.component.nats.JetStreamConfigurer;
import org.mojave.rail.fspiop.quoting.contract.command.step.UpdateQuotesResponseStep;
import org.mojave.rail.fspiop.quoting.contract.constant.QuotingStreamName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class UpdateQuotesResponseStepConsumer {

    public static final String CONSUMER_NAME =
        "fspiop-quoting:update-quotes-response-step-consumer";

    private static final Logger LOGGER = LoggerFactory.getLogger(
        UpdateQuotesResponseStepConsumer.class);

    private final UpdateQuotesResponseStep updateQuotesResponseStep;

    private final ObjectMapper objectMapper;

    private final Dispatcher dispatcher;

    public UpdateQuotesResponseStepConsumer(final UpdateQuotesResponseStep updateQuotesResponseStep,
                                            final ObjectMapper objectMapper,
                                            final Connection connection) {

        Objects.requireNonNull(updateQuotesResponseStep);
        Objects.requireNonNull(objectMapper);
        Objects.requireNonNull(connection);

        this.updateQuotesResponseStep = updateQuotesResponseStep;
        this.objectMapper = objectMapper;
        this.dispatcher = connection.createDispatcher(this::handle);

        this.subscribe(connection);
    }

    private void handle(final Message message) {

        try {

            final var input = this.objectMapper.readValue(
                message.getData(), UpdateQuotesResponseStep.Input.class);

            this.updateQuotesResponseStep.execute(input);
            message.ack();

        } catch (final UncheckedDomainException exception) {

            LOGGER.error("UpdateQuotesResponseStepConsumer business error:", exception);
            message.ack();

        } catch (final Exception exception) {

            LOGGER.error("UpdateQuotesResponseStepConsumer processing error:", exception);
            message.nak();
        }
    }

    private void subscribe(final Connection connection) {

        try {

            JetStreamConfigurer.ensureStream(
                connection, QuotingStreamName.STREAM_NAME, UpdateQuotesResponseStep.TOPIC_NAME);

            final JetStream jetStream = connection.jetStream();

            final var consumerConfiguration = ConsumerConfiguration
                                                  .builder()
                                                  .durable(CONSUMER_NAME)
                                                  .ackPolicy(AckPolicy.Explicit)
                                                  .filterSubject(UpdateQuotesResponseStep.TOPIC_NAME)
                                                  .build();

            final PushSubscribeOptions subscribeOptions = PushSubscribeOptions
                                                              .builder()
                                                              .stream(QuotingStreamName.STREAM_NAME)
                                                              .configuration(consumerConfiguration)
                                                              .build();

            jetStream.subscribe(
                UpdateQuotesResponseStep.TOPIC_NAME, this.dispatcher, this::handle, false,
                subscribeOptions);

        } catch (final Exception exception) {

            throw new RuntimeException(exception);
        }
    }

}
