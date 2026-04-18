package org.mojave.core.participant.intercom.replier.query.ssp;

import io.nats.client.Connection;
import io.nats.client.Message;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.Envelope;
import org.mojave.core.participant.contract.query.SspQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class SspQueryReplier {

    private static final Logger LOGGER = LoggerFactory.getLogger(SspQueryReplier.class);

    private final SspQuery sspQuery;

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public SspQueryReplier(final SspQuery sspQuery,
                           final Connection connection,
                           final ObjectMapper objectMapper) {

        Objects.requireNonNull(sspQuery);
        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.sspQuery = sspQuery;
        this.connection = connection;
        this.objectMapper = objectMapper;

        this.connection.createDispatcher(this::handleGetById).subscribe(SspQuery.GET_BY_ID_SUBJECT_NAME);
        this.connection.createDispatcher(this::handleGetByCode).subscribe(SspQuery.GET_BY_CODE_SUBJECT_NAME);
        this.connection.createDispatcher(this::handleGetAll).subscribe(SspQuery.GET_ALL_SUBJECT_NAME);
    }

    private void handleGetById(final Message message) {

        this.reply(message, SspQuery.GetByIdInput.class, input -> {
            LOGGER.info("SspQueryReplier.get(SspId) : input: ({})", ObjectLogger.log(input));
            final var output = this.sspQuery.get(input.sspId());
            LOGGER.info("SspQueryReplier.get(SspId) : output : ({})", ObjectLogger.log(output));
            return output;
        });
    }

    private void handleGetByCode(final Message message) {

        this.reply(message, SspQuery.GetByCodeInput.class, input -> {
            LOGGER.info("SspQueryReplier.get(SspCode) : input: ({})", ObjectLogger.log(input));
            final var output = this.sspQuery.get(input.sspCode());
            LOGGER.info("SspQueryReplier.get(SspCode) : output : ({})", ObjectLogger.log(output));
            return output;
        });
    }

    private void handleGetAll(final Message message) {

        this.reply(message, SspQuery.GetAllInput.class, input -> {
            LOGGER.info("SspQueryReplier.getAll : input: ({})", ObjectLogger.log(input));
            final var output = this.sspQuery.getAll();
            LOGGER.info("SspQueryReplier.getAll : output : ({})", ObjectLogger.log(output));
            return output;
        });
    }

    private <I> void reply(final Message message,
                           final Class<I> inputType,
                           final Handler<I> handler) {

        final var replyTo = message.getReplyTo();

        if (replyTo == null || replyTo.isBlank()) {
            LOGGER.warn("SspQueryReplier : reply subject is missing.");
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
