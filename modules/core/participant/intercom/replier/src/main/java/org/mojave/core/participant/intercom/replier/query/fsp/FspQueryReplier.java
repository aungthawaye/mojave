package org.mojave.core.participant.intercom.replier.query.fsp;

import io.nats.client.Connection;
import io.nats.client.Message;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.Envelope;
import org.mojave.core.participant.contract.query.FspQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class FspQueryReplier {

    private static final Logger LOGGER = LoggerFactory.getLogger(FspQueryReplier.class);

    private final FspQuery fspQuery;

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public FspQueryReplier(final FspQuery fspQuery,
                           final Connection connection,
                           final ObjectMapper objectMapper) {

        Objects.requireNonNull(fspQuery);
        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.fspQuery = fspQuery;
        this.connection = connection;
        this.objectMapper = objectMapper;

        this.connection.createDispatcher(this::handleGetById).subscribe(FspQuery.GET_BY_ID_SUBJECT_NAME);
        this.connection.createDispatcher(this::handleGetByCode).subscribe(FspQuery.GET_BY_CODE_SUBJECT_NAME);
        this.connection.createDispatcher(this::handleGetAll).subscribe(FspQuery.GET_ALL_SUBJECT_NAME);
    }

    private void handleGetById(final Message message) {

        this.reply(message, FspQuery.GetByIdInput.class, input -> {
            LOGGER.info("FspQueryReplier.get(FspId) : input: ({})", ObjectLogger.log(input));
            final var output = this.fspQuery.get(input.fspId());
            LOGGER.info("FspQueryReplier.get(FspId) : output : ({})", ObjectLogger.log(output));
            return output;
        });
    }

    private void handleGetByCode(final Message message) {

        this.reply(message, FspQuery.GetByCodeInput.class, input -> {
            LOGGER.info("FspQueryReplier.get(FspCode) : input: ({})", ObjectLogger.log(input));
            final var output = this.fspQuery.get(input.fspCode());
            LOGGER.info("FspQueryReplier.get(FspCode) : output : ({})", ObjectLogger.log(output));
            return output;
        });
    }

    private void handleGetAll(final Message message) {

        this.reply(message, FspQuery.GetAllInput.class, input -> {
            LOGGER.info("FspQueryReplier.getAll : input: ({})", ObjectLogger.log(input));
            final var output = this.fspQuery.getAll();
            LOGGER.info("FspQueryReplier.getAll : output : ({})", ObjectLogger.log(output));
            return output;
        });
    }

    private <I> void reply(final Message message,
                           final Class<I> inputType,
                           final Handler<I> handler) {

        final var replyTo = message.getReplyTo();

        if (replyTo == null || replyTo.isBlank()) {
            LOGGER.warn("FspQueryReplier : reply subject is missing.");
            return;
        }

        try {

            final var input = this.objectMapper.readValue(message.getData(), inputType);
            final var output = handler.handle(input);
            final var response = Envelope.success(this.objectMapper.valueToTree(output));
            final var responseData = this.objectMapper.writeValueAsBytes(response);

            LOGGER.info("FspQueryReplier : response : ({})", ObjectLogger.log(response));

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
