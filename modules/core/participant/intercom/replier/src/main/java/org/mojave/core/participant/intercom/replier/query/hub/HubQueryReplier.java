package org.mojave.core.participant.intercom.replier.query.hub;

import io.nats.client.Connection;
import io.nats.client.Message;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.Envelope;
import org.mojave.core.participant.contract.query.HubQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class HubQueryReplier {

    private static final Logger LOGGER = LoggerFactory.getLogger(HubQueryReplier.class);

    private final HubQuery hubQuery;

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public HubQueryReplier(final HubQuery hubQuery,
                           final Connection connection,
                           final ObjectMapper objectMapper) {

        Objects.requireNonNull(hubQuery);
        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.hubQuery = hubQuery;
        this.connection = connection;
        this.objectMapper = objectMapper;

        this.connection.createDispatcher(this::handleCount).subscribe(HubQuery.COUNT_SUBJECT_NAME);
        this.connection.createDispatcher(this::handleGet).subscribe(HubQuery.GET_SUBJECT_NAME);
        this.connection.createDispatcher(this::handleGetAll).subscribe(HubQuery.GET_ALL_SUBJECT_NAME);
    }

    private void handleCount(final Message message) {

        this.reply(message, HubQuery.CountInput.class, input -> {
            LOGGER.info("HubQueryReplier.count : input: ({})", ObjectLogger.log(input));
            final var output = this.hubQuery.count();
            LOGGER.info("HubQueryReplier.count : output : ({})", ObjectLogger.log(output));
            return output;
        });
    }

    private void handleGet(final Message message) {

        this.reply(message, HubQuery.GetInput.class, input -> {
            LOGGER.info("HubQueryReplier.get : input: ({})", ObjectLogger.log(input));
            final var output = this.hubQuery.get();
            LOGGER.info("HubQueryReplier.get : output : ({})", ObjectLogger.log(output));
            return output;
        });
    }

    private void handleGetAll(final Message message) {

        this.reply(message, HubQuery.GetAllInput.class, input -> {
            LOGGER.info("HubQueryReplier.getAll : input: ({})", ObjectLogger.log(input));
            final var output = this.hubQuery.getAll();
            LOGGER.info("HubQueryReplier.getAll : output : ({})", ObjectLogger.log(output));
            return output;
        });
    }

    private <I> void reply(final Message message,
                           final Class<I> inputType,
                           final Handler<I> handler) {

        final var replyTo = message.getReplyTo();

        if (replyTo == null || replyTo.isBlank()) {
            LOGGER.warn("HubQueryReplier : reply subject is missing.");
            return;
        }

        try {

            final var input = this.objectMapper.readValue(message.getData(), inputType);
            final var output = handler.handle(input);
            final var response = Envelope.success(this.objectMapper.valueToTree(output));
            final var responseData = this.objectMapper.writeValueAsBytes(response);

            this.connection.publish(replyTo, responseData);

        } catch (final Exception exception) {

            final var output = MojaveErrorResponse.from(exception);
            final var response = Envelope.failure(this.objectMapper.valueToTree(output));
            final var responseData = this.objectMapper.writeValueAsBytes(response);

            this.connection.publish(replyTo, responseData);
        }
    }

    @FunctionalInterface
    private interface Handler<I> {

        Object handle(I input) throws Exception;

    }

}
