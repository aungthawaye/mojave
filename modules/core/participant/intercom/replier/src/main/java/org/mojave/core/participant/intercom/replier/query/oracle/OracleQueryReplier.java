package org.mojave.core.participant.intercom.replier.query.oracle;

import io.nats.client.Connection;
import io.nats.client.Message;
import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.nats.Envelope;
import org.mojave.core.participant.contract.query.OracleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
public class OracleQueryReplier {

    private static final Logger LOGGER = LoggerFactory.getLogger(OracleQueryReplier.class);

    private final OracleQuery oracleQuery;

    private final Connection connection;

    private final ObjectMapper objectMapper;

    public OracleQueryReplier(final OracleQuery oracleQuery,
                              final Connection connection,
                              final ObjectMapper objectMapper) {

        Objects.requireNonNull(oracleQuery);
        Objects.requireNonNull(connection);
        Objects.requireNonNull(objectMapper);

        this.oracleQuery = oracleQuery;
        this.connection = connection;
        this.objectMapper = objectMapper;

        this.connection.createDispatcher(this::handleFindByType).subscribe(OracleQuery.FIND_BY_TYPE_SUBJECT_NAME);
        this.connection.createDispatcher(this::handleGetByType).subscribe(OracleQuery.GET_BY_TYPE_SUBJECT_NAME);
        this.connection.createDispatcher(this::handleGetById).subscribe(OracleQuery.GET_BY_ID_SUBJECT_NAME);
        this.connection.createDispatcher(this::handleGetAll).subscribe(OracleQuery.GET_ALL_SUBJECT_NAME);
    }

    private void handleFindByType(final Message message) {

        this.reply(message, OracleQuery.FindByTypeInput.class, input -> {
            LOGGER.info("OracleQueryReplier.find : input: ({})", ObjectLogger.log(input));
            final var output = this.oracleQuery.find(input.type()).orElse(null);
            LOGGER.info("OracleQueryReplier.find : output : ({})", ObjectLogger.log(output));
            return output;
        });
    }

    private void handleGetByType(final Message message) {

        this.reply(message, OracleQuery.GetByTypeInput.class, input -> {
            LOGGER.info("OracleQueryReplier.get(PartyIdType) : input: ({})", ObjectLogger.log(input));
            final var output = this.oracleQuery.get(input.type());
            LOGGER.info("OracleQueryReplier.get(PartyIdType) : output : ({})", ObjectLogger.log(output));
            return output;
        });
    }

    private void handleGetById(final Message message) {

        this.reply(message, OracleQuery.GetByIdInput.class, input -> {
            LOGGER.info("OracleQueryReplier.get(OracleId) : input: ({})", ObjectLogger.log(input));
            final var output = this.oracleQuery.get(input.oracleId());
            LOGGER.info("OracleQueryReplier.get(OracleId) : output : ({})", ObjectLogger.log(output));
            return output;
        });
    }

    private void handleGetAll(final Message message) {

        this.reply(message, OracleQuery.GetAllInput.class, input -> {
            LOGGER.info("OracleQueryReplier.getAll : input: ({})", ObjectLogger.log(input));
            final var output = this.oracleQuery.getAll();
            LOGGER.info("OracleQueryReplier.getAll : output : ({})", ObjectLogger.log(output));
            return output;
        });
    }

    private <I> void reply(final Message message,
                           final Class<I> inputType,
                           final Handler<I> handler) {

        final var replyTo = message.getReplyTo();

        if (replyTo == null || replyTo.isBlank()) {
            LOGGER.warn("OracleQueryReplier : reply subject is missing.");
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
